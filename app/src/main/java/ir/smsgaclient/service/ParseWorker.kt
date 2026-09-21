package ir.smsgaclient.service

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import ir.smsgaclient.data.db.dao.ParsedSmsDao
import ir.smsgaclient.data.db.dao.PatternCacheDao
import ir.smsgaclient.data.db.dao.RawSmsDao
import ir.smsgaclient.data.db.dao.TransactionDao
import ir.smsgaclient.data.db.entity.ParsedSmsEntity
import ir.smsgaclient.data.db.entity.TransactionEntity
import ir.smsgaclient.domain.model.PatternSet
import ir.smsgaclient.domain.model.SmsType
import ir.smsgaclient.domain.parser.SmsParser
import kotlinx.serialization.json.Json
import timber.log.Timber

@HiltWorker
class ParseWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val rawSmsDao: RawSmsDao,
    private val parsedSmsDao: ParsedSmsDao,
    private val transactionDao: TransactionDao,
    private val patternCacheDao: PatternCacheDao,
    private val json: Json
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val messageId = inputData.getString(KEY_MESSAGE_ID)
        if (messageId.isNullOrEmpty()) {
            Timber.e("ParseWorker received null or empty messageId")
            return Result.failure()
        }

        val rawSms = rawSmsDao.getByMessageId(messageId)
        if (rawSms == null) {
            Timber.e("No raw SMS found in DB for messageId: $messageId")
            return Result.failure()
        }

        Timber.i("Parsing raw SMS for messageId: $messageId")

        val cachedPatternEntity = patternCacheDao.getLatestPattern()
        val patternSet = if (cachedPatternEntity != null) {
            try {
                json.decodeFromString<PatternSet>(cachedPatternEntity.json)
            } catch (e: Exception) {
                Timber.e(e, "Failed to decode cached patterns, falling back")
                DEFAULT_FALLBACK_PATTERNS
            }
        } else {
            DEFAULT_FALLBACK_PATTERNS
        }

        val parsed = SmsParser.parse(
            rawSms = rawSms.body,
            sender = rawSms.sender,
            receivedAtEpochMilli = rawSms.receivedAt,
            patternSet = patternSet
        )

        val parsedEntity = ParsedSmsEntity(
            messageId = parsed.messageId,
            bankId = parsed.bankId,
            type = parsed.type.name,
            amountRial = parsed.amountRial,
            amountToman = parsed.amountToman,
            cardLast4 = parsed.cardLast4,
            balanceRial = parsed.balanceRial,
            parseVersion = parsed.parseVersion
        )
        parsedSmsDao.insert(parsedEntity)

        if (parsed.type != SmsType.UNKNOWN && parsed.amountRial != null) {
            val transaction = TransactionEntity(
                messageId = parsed.messageId,
                orderId = null,
                amountRial = parsed.amountRial,
                bankId = parsed.bankId,
                cardLast4 = parsed.cardLast4,
                status = "PENDING",
                receivedAt = rawSms.receivedAt,
                note = null
            )
            transactionDao.insert(transaction)
        }

        rawSmsDao.updateStatus(messageId, "PARSED")

        enqueueForwardWorker(messageId)

        return Result.success()
    }

    private fun enqueueForwardWorker(messageId: String) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val forwardWork = OneTimeWorkRequestBuilder<ForwardWorker>()
            .setConstraints(constraints)
            .setInputData(Data.Builder().putString(ForwardWorker.KEY_MESSAGE_ID, messageId).build())
            .build()

        WorkManager.getInstance(appContext).enqueue(forwardWork)
    }

    companion object {
        const val KEY_MESSAGE_ID = "key_message_id"

        private val DEFAULT_FALLBACK_PATTERNS = PatternSet(
            version = 0,
            updatedAt = "2026-01-01T00:00:00Z",
            banks = emptyList()
        )
    }
}
