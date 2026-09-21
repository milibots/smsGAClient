// app/src/test/java/ir/smsgaclient/domain/parser/ParserUnknownTest.kt
package ir.smsgaclient.domain.parser

import ir.smsgaclient.domain.model.PatternSet
import ir.smsgaclient.domain.model.SmsType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class ParserUnknownTest {

    private val emptyPatternSet = PatternSet(
        version = 1,
        updatedAt = "2026-09-21T10:00:00Z",
        banks = emptyList()
    )

    @Test
    fun `returns unknown type for non-bank text without throwing`() {
        val rawSms = "تخفیف ویژه آخر هفته برای خرید اینترنتی"
        val result = SmsParser.parse(
            rawSms = rawSms,
            sender = "ADVERTISER",
            receivedAtEpochMilli = 1790000000000L,
            patternSet = emptyPatternSet
        )

        assertEquals("unknown", result.bankId)
        assertEquals(SmsType.UNKNOWN, result.type)
        assertNull(result.amountRial)
        assertNull(result.cardLast4)
    }

    @Test
    fun `handles completely empty or garbage inputs gracefully`() {
        val result = SmsParser.parse(
            rawSms = "",
            sender = "",
            receivedAtEpochMilli = 0L,
            patternSet = emptyPatternSet
        )

        assertEquals("unknown", result.bankId)
        assertEquals(SmsType.UNKNOWN, result.type)
    }
}
