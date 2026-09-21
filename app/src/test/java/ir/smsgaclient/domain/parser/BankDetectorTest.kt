// app/src/test/java/ir/smsgaclient/domain/parser/BankDetectorTest.kt
package ir.smsgaclient.domain.parser

import ir.smsgaclient.domain.model.BankPattern
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class BankDetectorTest {

    private val samplePatterns = listOf(
        BankPattern(
            id = "blu",
            nameFa = "بلو",
            senders = listOf("BLUBANK", "98300099"),
            detectRegex = "(بلو|blu)",
            depositRegex = "\\+(?<amount>\\d+)\\s*ریال",
            withdrawRegex = "-(?<amount>\\d+)\\s*ریال",
            cardRegex = "(?<card>\\*{0,4}\\d{4})",
            balanceRegex = "موجودی[:\\s]*(?<balance>\\d+)"
        ),
        BankPattern(
            id = "mellat",
            nameFa = "ملت",
            senders = listOf("MELLAT", "982000444"),
            detectRegex = "(ملت|mellat)",
            depositRegex = "واریز[:\\s]*(?<amount>\\d+)\\s*ریال",
            withdrawRegex = "برداشت[:\\s]*(?<amount>\\d+)\\s*ریال",
            cardRegex = "(?<card>\\d{4})",
            balanceRegex = "موجودی[:\\s]*(?<balance>\\d+)"
        )
    )

    private val detector = BankDetector(samplePatterns)

    @Test
    fun `detects bank by sender name`() {
        val detected = detector.detect(sender = "BLUBANK", normalizedBody = "واریز وجه")
        assertEquals("blu", detected.id)
    }

    @Test
    fun `detects bank by sender number`() {
        val detected = detector.detect(sender = "982000444", normalizedBody = "مبلغ واریزی")
        assertEquals("mellat", detected.id)
    }

    @Test
    fun `detects bank by body content regex when sender is unknown`() {
        val detected = detector.detect(sender = "UNKNOWN_NUM", normalizedBody = "انتقال وجه از طریق بلو بانک")
        assertEquals("blu", detected.id)
    }

    @Test
    fun `returns unknown bank when no pattern matches`() {
        val detected = detector.detect(sender = "RANDOM", normalizedBody = "متن نامربوط بدون نام بانک")
        assertEquals("unknown", detected.id)
    }
}
