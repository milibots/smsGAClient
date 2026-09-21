// app/src/main/java/ir/smsgaclient/service/ForwardWorker.kt
package ir.smsgaclient.service

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import ir.smsgaclient.BuildConfig
import ir.smsgaclient.data.db.dao.ForwardAttemptDao
import ir.smsgaclient.data.db.dao.ParsedSmsDao
import ir.smsgaclient.data.db.dao.RawSmsDao
import ir.smsgaclient.data.db.dao.TransactionDao
import ir.smsgaclient.data.db.entity.ForwardAttemptEntity
import ir.smsgaclient.data.prefs.SecurePrefs
import ir.smsgaclient.domain.forward.HmacSigner
import ir.smsgaclient.domain.forward.PayloadBuilder
import ir.smsgaclient.domain.forward.RetryPolicy
import ir.smsgaclient.domain.model.ParsedSms
import ir.smsgaclient.domain.model.SmsType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import timber.log.Timber
import java.io.IOException

/**
 * WorkManager worker that signs and forwards parsed SMS to merchant's HTTPS webhook.
 * Guarantees zero data loss, idempotency, HMAC-SHA256 signing, and battery-aware retries.
 */
@HiltWorker
class ForwardWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val rawSmsDao: RawSmsDao,
    private val parsedSmsDao: ParsedSmsDao,
    private val transactionDao: TransactionDao,
    private val forwardAttemptDao: ForwardAttemptDao,
    private val securePrefs: SecurePrefs,
    private val okHttpClient: OkHttpClient
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val messageId = inputData.getString(KEY_MESSAGE_ID)
        if (messageId.isNullOrEmpty()) {
            return Result.failure()
        }

        val webhookUrl = securePrefs.webhookUrl
        val apiToken = securePrefs.apiToken
        val hmacSecret = securePrefs.hmacSecret
        val merchantId = securePrefs.merchantId ?: "unknown_merchant"

        // Check if merchant webhook is configured
        if (webhookUrl.isNullOrEmpty() || hmacSecret.isNullOrEmpty()) {
            Timber.w("Webhook or HMAC secret not configured. Forwarding postponed.")
            return Result.retry()
        }

        // Battery check: pause retries if battery < 15% and not charging
        val (batteryLevel, isCharging) = getBatteryState()
        if (RetryPolicy.shouldPauseForBattery(batteryLevel, isCharging)) {
            Timber.w("Low battery ($batteryLevel%) and not charging. Pausing forward.")
            return Result.retry()
        }

        val rawSms = rawSmsDao.getByMessageId(messageId) ?: return Result.failure()
        val parsedEntity = parsedSmsDao.getByMessageId(messageId) ?: return Result.failure()

        val parsedSms = ParsedSms(
            messageId = parsedEntity.messageId,
            bankId = parsedEntity.bankId,
            type = runCatching { SmsType.valueOf(parsedEntity.type) }.getOrDefault(SmsType.UNKNOWN),
            amountRial = parsedEntity.amountRial,
            amountToman = parsedEntity.amountToman,
            cardLast4 = parsedEntity.cardLast4,
            balanceRial = parsedEntity.balanceRial,
            rawSms = rawSms.body,
            sender = rawSms.sender,
            receivedAtEpochMilli = rawSms.receivedAt,
            parseVersion = parsedEntity.parseVersion
        )

        val payloadObj = PayloadBuilder.build(
            parsedSms = parsedSms,
            deviceId = securePrefs.deviceId,
            merchantId = merchantId
        )
        val payloadJson = PayloadBuilder.toJson(payloadObj)
        val timestampSeconds = System.currentTimeMillis() / 1000
        val signature = HmacSigner.sign(hmacSecret, timestampSeconds, payloadJson)

        val request = Request.Builder()
            .url(webhookUrl)
            .post(payloadJson.toRequestBody("application/json; charset=utf-8".toMediaType()))
            .addHeader("Authorization", "Bearer $apiToken")
            .addHeader("X-SmsGA-Signature", signature)
            .addHeader("X-SmsGA-Device", securePrefs.deviceId)
            .addHeader("X-SmsGA-Timestamp", timestampSeconds.toString())
            .addHeader("User-Agent", "smsGAClient/${BuildConfig.VERSION_NAME} (Android ${Build.VERSION.SDK_INT})")
            .build()

        var httpStatus: Int? = null
        var responseBody: String? = null
        var errorMessage: String? = null

        try {
            val response = okHttpClient.newCall(request).execute()
            httpStatus = response.code
            responseBody = response.body?.string()

            if (response.isSuccessful) {
                // Success: mark SENT
                rawSmsDao.updateStatus(messageId, "FORWARDED")
                transactionDao.updateStatus(messageId, "SENT")
                recordAttempt(messageId, httpStatus, null, responseBody)
                Timber.i("Forwarded successfully for $messageId, status: $httpStatus")
                return Result.success()
            } else if (httpStatus == 401) {
                // Auth failure: clear tokens, trigger re-pair
                securePrefs.clearCredentials()
                rawSmsDao.updateStatus(messageId, "FAILED")
                transactionDao.updateStatus(messageId, "FAILED")
                recordAttempt(messageId, httpStatus, "HTTP 401 Unauthorized", responseBody)
                return Result.failure()
            } else if (httpStatus == 400 || httpStatus == 403) {
                // Permanent client error
                rawSmsDao.updateStatus(messageId, "FAILED")
                transactionDao.updateStatus(messageId, "FAILED")
                recordAttempt(messageId, httpStatus, "HTTP $httpStatus Non-retryable", responseBody)
                return Result.failure()
            }
        } catch (e: IOException) {
            errorMessage = e.message ?: "Network error"
            Timber.e(e, "Forwarding network error for $messageId")
        }

        recordAttempt(messageId, httpStatus, errorMessage, responseBody)

        // Check retry attempt limits
        if (runAttemptCount >= RetryPolicy.MAX_ATTEMPTS) {
            Timber.e("Exceeded max retry attempts for $messageId")
            rawSmsDao.updateStatus(messageId, "FAILED")
            transactionDao.updateStatus(messageId, "FAILED")
            return Result.failure()
        }

        return Result.retry()
    }

    private suspend fun recordAttempt(
        messageId: String,
        status: Int?,
        error: String?,
        body: String?
    ) {
        forwardAttemptDao.insert(
            ForwardAttemptEntity(
                messageId = messageId,
                attemptedAt = System.currentTimeMillis(),
                httpStatus = status,
                error = error,
                responseBody = body?.take(500)
            )
        )
    }

    private fun getBatteryState(): Pair<Int, Boolean> {
        val batteryIntent = appContext.registerReceiver(
            null,
            IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        )
        val level = batteryIntent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale = batteryIntent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
        val batteryPct = if (level >= 0 && scale > 0) (level * 100) / scale else 100

        val status = batteryIntent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL

        return Pair(batteryPct, isCharging)
    }

    companion object {
        const val KEY_MESSAGE_ID = "key_forward_message_id"
    }
}
