package ir.smsgaclient.util

import java.text.DecimalFormat

object PersianNumberFormatter {

    private val PERSIAN_DIGITS = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')
    private val DECIMAL_FORMAT = DecimalFormat("#,###")

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

    fun formatNumber(number: Long): String {
        val formatted = DECIMAL_FORMAT.format(number)
        return toPersianDigits(formatted).replace(',', '٬')
    }

    fun formatToman(amountToman: Long): String {
        return "${formatNumber(amountToman)} تومان"
    }

    fun formatRial(amountRial: Long): String {
        return "${formatNumber(amountRial)} ریال"
    }
}
