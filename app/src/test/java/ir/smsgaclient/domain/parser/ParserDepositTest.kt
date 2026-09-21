// app/src/test/java/ir/smsgaclient/domain/parser/ParserDepositTest.kt
package ir.smsgaclient.domain.parser

import ir.smsgaclient.domain.model.BankPattern
import ir.smsgaclient.domain.model.PatternSet
import ir.smsgaclient.domain.model.SmsType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class ParserDepositTest {

    private val patternSet = PatternSet(
        version = 41,
        updatedAt = "2026-09-21T10:00:00Z",
        banks = listOf(
            BankPattern(
                id = "blu",
                nameFa = "بلو",
                senders = listOf("BLUBANK"),
                detectRegex = "(بلو|blu)",
                depositRegex = "\\+(?<amount>\\d+)\\s*ریال",
                withdrawRegex = "-(?<amount>\\d+)\\s*ریال",
                cardRegex = "(?<card>\\*{0,4}\\d{4})",
                balanceRegex = "موجودی[:\\s]*(?<balance>\\d+)"
            ),
            BankPattern(
                id = "mellat",
                nameFa = "ملت",
                senders = listOf("MELLAT"),
                detectRegex = "(ملت|mellat)",
                depositRegex = "واریز[:\\s]*(?<amount>\\d+)\\s*ریال",
                withdrawRegex = "برداشت[:\\s]*(?<amount>\\d+)\\s*ریال",
                cardRegex = "کارت[:\\s]*(?<card>\\d{4})",
                balanceRegex = "موجودی[:\\s]*(?<balance>\\d+)"
            )
        )
    )

    @Test
    fun `parses blu deposit with persian numerals and thousand separators`() {
        val rawSms = "+۴٬۵۰۰٬۲۸۰ ریال — بلو کارت ****۱۲۳۴ موجودی: ۱۲۵٬۰۰۰٬۰۰۰ ریال"
        val timestamp = 1790000000000L

        val result = SmsParser.parse(
            rawSms = rawSms,
            sender = "BLUBANK",
            receivedAtEpochMilli = timestamp,
            patternSet = patternSet
        )

        assertEquals("blu", result.bankId)
        assertEquals(SmsType.DEPOSIT, result.type)
        assertEquals(4500280L, result.amountRial)
        assertEquals(450028L, result.amountToman)
        assertEquals("1234", result.cardLast4)
        assertEquals(125000000L, result.balanceRial)
        assertNotNull(result.messageId)
        assertEquals(41, result.parseVersion)
    }

    @Test
    fun `parses mellat deposit successfully`() {
        val rawSms = "بانک ملت\nواریز: ۲۵۰,۰۰۰ ریال\nکارت: ۵۶۷۸\nموجودی: ۱۰,۰۰۰,۰۰۰"
        val timestamp = 1790000001000L

        val result = SmsParser.parse(
            rawSms = rawSms,
            sender = "MELLAT",
            receivedAtEpochMilli = timestamp,
            patternSet = patternSet
        )

        assertEquals("mellat", result.bankId)
        assertEquals(SmsType.DEPOSIT, result.type)
        assertEquals(250000L, result.amountRial)
        assertEquals(25000L, result.amountToman)
        assertEquals("5678", result.cardLast4)
        assertEquals(10000000L, result.balanceRial)
    }
}
