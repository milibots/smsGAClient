// app/src/test/java/ir/smsgaclient/domain/parser/ParserWithdrawTest.kt
package ir.smsgaclient.domain.parser

import ir.smsgaclient.domain.model.BankPattern
import ir.smsgaclient.domain.model.PatternSet
import ir.smsgaclient.domain.model.SmsType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ParserWithdrawTest {

    private val patternSet = PatternSet(
        version = 1,
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
            )
        )
    )

    @Test
    fun `parses withdrawal transaction accurately`() {
        val rawSms = "-۱۵۰٬۰۰۰ ریال — بلو کارت ****۹۹۲۱ موجودی: ۱٬۲۰۰٬۰۰۰"
        val timestamp = 1790000002000L

        val result = SmsParser.parse(
            rawSms = rawSms,
            sender = "BLUBANK",
            receivedAtEpochMilli = timestamp,
            patternSet = patternSet
        )

        assertEquals("blu", result.bankId)
        assertEquals(SmsType.WITHDRAW, result.type)
        assertEquals(150000L, result.amountRial)
        assertEquals(15000L, result.amountToman)
        assertEquals("9921", result.cardLast4)
        assertEquals(1200000L, result.balanceRial)
    }
}
