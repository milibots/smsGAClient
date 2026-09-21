package ir.smsgaclient.domain.parser

object SmsNormalizer {

    private val PERSIAN_DIGITS = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')
    private val ARABIC_DIGITS = charArrayOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')
    private val WHITESPACE_REGEX = Regex("\\s+")

    fun normalize(input: String): String {
        if (input.isEmpty()) return ""

        val sb = StringBuilder(input.length)

        for (char in input) {
            when {

                char in '۰'..'۹' -> {
                    sb.append((char.code - '۰'.code + '0'.code).toChar())
                }

                char in '٠'..'٩' -> {
                    sb.append((char.code - '٠'.code + '0'.code).toChar())
                }

                char == 'ي' -> sb.append('ی')
                char == 'ك' -> sb.append('ک')
                char == 'ة' -> sb.append('ه')

                char == '٬' || char == ',' || char == '،' -> {

                }
                else -> {
                    sb.append(char)
                }
            }
        }

        return WHITESPACE_REGEX.replace(sb.toString(), " ").trim()
    }
}
