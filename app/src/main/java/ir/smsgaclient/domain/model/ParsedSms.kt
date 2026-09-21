package ir.smsgaclient.domain.model

import java.time.Instant
import kotlinx.serialization.Serializable

@Serializable
data class ParsedSms(
    val messageId: String,
    val bankId: String,
    val type: SmsType,
    val amountRial: Long?,
    val amountToman: Long?,
    val cardLast4: String?,
    val balanceRial: Long?,
    val rawSms: String,
    val sender: String,
    val receivedAtEpochMilli: Long,
    val parseVersion: Int
) {
    val receivedAt: Instant
        get() = Instant.ofEpochMilli(receivedAtEpochMilli)
}
