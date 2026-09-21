package ir.smsgaclient.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import ir.smsgaclient.data.db.dao.RawSmsDao
import ir.smsgaclient.data.db.entity.RawSmsEntity
import ir.smsgaclient.domain.parser.SmsParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class SmsReceiver : BroadcastReceiver() {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface SmsReceiverEntryPoint {
        fun rawSmsDao(): RawSmsDao
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            return
        }

        val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
        if (messages.isNullOrEmpty()) {
            return
        }

        val sender = messages[0].displayOriginatingAddress ?: "UNKNOWN"
        val bodyBuilder = StringBuilder()
        for (sms in messages) {
            sms.displayMessageBody?.let { bodyBuilder.append(it) }
        }
        val rawBody = bodyBuilder.toString()
        val receivedAt = messages[0].timestampMillis

        val messageId = SmsParser.generateMessageId(rawBody, receivedAt)
        Timber.i("SMS received from $sender, messageId: $messageId")

        val pendingResult = goAsync()

        val appContext = context.applicationContext
        val entryPoint = EntryPointAccessors.fromApplication(
            appContext,
            SmsReceiverEntryPoint::class.java
        )
        val rawSmsDao = entryPoint.rawSmsDao()

        CoroutineScope(Dispatchers.IO).launch {
            try {

                val rawEntity = RawSmsEntity(
                    messageId = messageId,
                    sender = sender,
                    body = rawBody,
                    receivedAt = receivedAt,
                    status = "RECEIVED"
                )
                rawSmsDao.insert(rawEntity)
                Timber.d("Raw SMS persisted to Room DB with status RECEIVED")

                val parseWork = OneTimeWorkRequestBuilder<ParseWorker>()
                    .setInputData(Data.Builder().putString(ParseWorker.KEY_MESSAGE_ID, messageId).build())
                    .build()

                WorkManager.getInstance(appContext).enqueue(parseWork)
            } catch (e: Exception) {
                Timber.e(e, "Error ingesting raw SMS into Room DB")
            } finally {
                pendingResult.finish()
            }
        }
    }
}
