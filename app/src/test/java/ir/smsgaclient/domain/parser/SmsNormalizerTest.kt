// app/src/test/java/ir/smsgaclient/domain/parser/SmsNormalizerTest.kt
package ir.smsgaclient.domain.parser

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class SmsNormalizerTest {

    @Test
    fun `converts persian digits to latin digits`() {
        val input = "مبلغ: ۰۱۲۳۴۵۶۷۸۹ ریال"
        val expected = "مبلغ: 0123456789 ریال"
        assertEquals(expected, SmsNormalizer.normalize(input))
    }

    @Test
    fun `converts arabic digits to latin digits`() {
        val input = "مبلغ: ٠١٢٣٤٥٦٧٨٩ ریال"
        val expected = "مبلغ: 0123456789 ریال"
        assertEquals(expected, SmsNormalizer.normalize(input))
    }

    @Test
    fun `converts arabic letters to persian letters`() {
        val input = "بانك ملي ايران"
        val expected = "بانک ملی ایران"
        assertEquals(expected, SmsNormalizer.normalize(input))
    }

    @Test
    fun `removes thousand separators`() {
        val input = "واریز: ۱٬۵۰۰٬۰۰۰ ریال و 2,350,000 و ۳،۴۰۰،۰۰۰"
        val expected = "واریز: 1500000 ریال و 2350000 و 3400000"
        assertEquals(expected, SmsNormalizer.normalize(input))
    }

    @Test
    fun `collapses multiple whitespaces and trims`() {
        val input = "   واریز   مبلغ   ۵۰۰۰۰   ریال   \n   بلو   "
        val expected = "واریز مبلغ 50000 ریال بلو"
        assertEquals(expected, SmsNormalizer.normalize(input))
    }

    @Test
    fun `handles empty and blank strings gracefully`() {
        assertEquals("", SmsNormalizer.normalize(""))
        assertEquals("", SmsNormalizer.normalize("    "))
    }
}
