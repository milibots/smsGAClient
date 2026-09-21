// app/src/test/java/ir/smsgaclient/util/PersianNumberFormatterTest.kt
package ir.smsgaclient.util

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class PersianNumberFormatterTest {

    @Test
    fun `converts latin digits to persian digits`() {
        assertEquals("۰۱۲۳۴۵۶۷۸۹", PersianNumberFormatter.toPersianDigits("0123456789"))
    }

    @Test
    fun `formats number with thousand separators in persian numerals`() {
        assertEquals("۱٬۲۵۰٬۰۰۰", PersianNumberFormatter.formatNumber(1250000L))
        assertEquals("۵۰۰", PersianNumberFormatter.formatNumber(500L))
    }

    @Test
    fun `formats toman amount accurately`() {
        assertEquals("۴۵۰٬۰۰۰ تومان", PersianNumberFormatter.formatToman(450000L))
    }

    @Test
    fun `formats rial amount accurately`() {
        assertEquals("۴٬۵۰۰٬۰۰۰ ریال", PersianNumberFormatter.formatRial(4500000L))
    }
}
