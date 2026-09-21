// app/src/main/java/ir/smsgaclient/domain/forward/PayloadBuilder.kt
package ir.smsgaclient.domain.forward

import ir.smsgaclient.domain.model.ParsedSms
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Serialized JSON payload for merchant webhook forwarding.
 */
@Serializable
data class ForwardPayload(
    @SerialName("event")
    val event: String,

    @SerialName("message_id")
    val messageId: String,

    @SerialName("device_id")
    val deviceId: String,

    @SerialName("merchant_id")
    val merchantId: String,

    @SerialName("received_at")
    val receivedAt: String,

    @SerialName("bank")
    val bank: String,

    @SerialName("type")
    val type: String,

    @SerialName("amount_rial")
    val amountRial: Long?,

    @SerialName("amount_toman")
    val amountToman: Long?,

    @SerialName("card_last4")
    val cardLast4: String?,

    @SerialName("balance_rial")
    val balanceRial: Long?,

    @SerialName("raw_sms")
    val rawSms: String,

    @SerialName("parse_version")
    val parseVersion: Int
)

/**
 * Builds the canonical JSON payload for forwarding to the merchant webhook.
 */
object PayloadBuilder {

    private val json = Json {
        encodeDefaults = true
        prettyPrint = false
    }

    private val ISO_FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME
        .withZone(ZoneId.of("Asia/Tehran"))

    /**
     * Constructs the [ForwardPayload] from a [ParsedSms].
     */
    fun build(
        parsedSms: ParsedSms,
        deviceId: String,
        merchantId: String
    ): ForwardPayload {
        val eventName = when (parsedSms.type) {
            ir.smsgaclient.domain.model.SmsType.DEPOSIT -> "sms.deposit"
            ir.smsgaclient.domain.model.SmsType.WITHDRAW -> "sms.withdraw"
            ir.smsgaclient.domain.model.SmsType.UNKNOWN -> "sms.unknown"
        }

        val formattedDate = ISO_FORMATTER.format(parsedSms.receivedAt)

        return ForwardPayload(
            event = eventName,
            messageId = parsedSms.messageId,
            deviceId = deviceId,
            merchantId = merchantId,
            receivedAt = formattedDate,
            bank = parsedSms.bankId,
            type = parsedSms.type.name.lowercase(),
            amountRial = parsedSms.amountRial,
            amountToman = parsedSms.amountToman,
            cardLast4 = parsedSms.cardLast4,
            balanceRial = parsedSms.balanceRial,
            rawSms = parsedSms.rawSms,
            parseVersion = parsedSms.parseVersion
        )
    }

    /**
     * Serializes the [ForwardPayload] into a JSON string.
     */
    fun toJson(payload: ForwardPayload): String {
        return json.encodeToString(payload)
    }
}
