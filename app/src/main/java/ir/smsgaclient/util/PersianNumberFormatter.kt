// app/src/main/java/ir/smsgaclient/util/PersianNumberFormatter.kt
package ir.smsgaclient.util

import java.text.DecimalFormat

/**
 * Persian number and currency formatting utilities.
 */
object PersianNumberFormatter {

    private val PERSIAN_DIGITS = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')
    private val DECIMAL_FORMAT = DecimalFormat("#,###")

    /**
     * Converts an ASCII integer/long or string to Persian digits.
     */
    fun toPersianDigits(input: String): String {
        val sb = StringBuilder(input.length)
        for (ch in input) {
            if (ch in '0'..'9') {
                sb.append(PERSIAN_DIGITS[ch - '0'])
            } else {
                sb.append(ch)
            }
        }
        return sb.toString()
    }

    /**
     * Formats a number with thousand separators and Persian digits.
     * Example: 1250000 -> "۱٬۲۵۰٬۰۰۰"
     */
    fun formatNumber(number: Long): String {
        val formatted = DECIMAL_FORMAT.format(number)
        return toPersianDigits(formatted).replace(',', '٬')
    }

    /**
     * Formats amount in Toman with Persian digits.
     * Example: 450000 -> "۴۵۰٬۰۰۰ تومان"
     */
    fun formatToman(amountToman: Long): String {
        return "${formatNumber(amountToman)} تومان"
    }

    /**
     * Formats amount in Rial with Persian digits.
     * Example: 4500000 -> "۴٬۵۰۰٬۰۰۰ ریال"
     */
    fun formatRial(amountRial: Long): String {
        return "${formatNumber(amountRial)} ریال"
    }
}
