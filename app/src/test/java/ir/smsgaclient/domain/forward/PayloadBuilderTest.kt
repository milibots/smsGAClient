// app/src/test/java/ir/smsgaclient/domain/forward/PayloadBuilderTest.kt
package ir.smsgaclient.domain.forward

import ir.smsgaclient.domain.model.ParsedSms
import ir.smsgaclient.domain.model.SmsType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class PayloadBuilderTest {

    @Test
    fun `builds valid payload and serializes to json`() {
        val parsed = ParsedSms(
            messageId = "sha256:abc12345",
            bankId = "blu",
            type = SmsType.DEPOSIT,
            amountRial = 500000L,
            amountToman = 50000L,
            cardLast4 = "1234",
            balanceRial = 1000000L,
            rawSms = "+۵۰٬۰۰۰ تومان بلو",
            sender = "BLUBANK",
            receivedAtEpochMilli = 1790000000000L,
            parseVersion = 1
        )

        val payload = PayloadBuilder.build(
            parsedSms = parsed,
            deviceId = "dev_12345",
            merchantId = "m_8821"
        )

        assertEquals("sms.deposit", payload.event)
        assertEquals("sha256:abc12345", payload.messageId)
        assertEquals("dev_12345", payload.deviceId)
        assertEquals("m_8821", payload.merchantId)
        assertEquals("blu", payload.bank)
        assertEquals(500000L, payload.amountRial)
        assertEquals(50000L, payload.amountToman)
        assertEquals("1234", payload.cardLast4)

        val json = PayloadBuilder.toJson(payload)
        assertTrue(json.contains("\"event\":\"sms.deposit\""))
        assertTrue(json.contains("\"message_id\":\"sha256:abc12345\""))
        assertTrue(json.contains("\"amount_rial\":500000"))
    }
}
