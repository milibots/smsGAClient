// app/src/main/java/ir/smsgaclient/domain/parser/SmsNormalizer.kt
package ir.smsgaclient.domain.parser

/**
 * Normalizes raw Persian and Arabic bank SMS strings before pattern matching.
 * Strict 5-step pipeline as specified in SYSTEM_PROMPT.md §6.2.
 */
object SmsNormalizer {

    private val PERSIAN_DIGITS = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')
    private val ARABIC_DIGITS = charArrayOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')
    private val WHITESPACE_REGEX = Regex("\\s+")

    /**
     * Executes the strict 5-step normalization pipeline:
     * 1. Persian digits -> Latin
     * 2. Arabic digits -> Latin
     * 3. Arabic letters (ي, ك) -> Persian (ی, ک)
     * 4. Remove thousands separators (٬, ,, ،)
     * 5. Collapse whitespace and trim
     *
     * @param input Raw SMS text from provider
     * @return Normalized string ready for regex parsing
     */
    fun normalize(input: String): String {
        if (input.isEmpty()) return ""

        val sb = StringBuilder(input.length)

        for (char in input) {
            when {
                // Step 1: Persian digits -> Latin digits
                char in '۰'..'۹' -> {
                    sb.append((char.code - '۰'.code + '0'.code).toChar())
                }
                // Step 2: Arabic digits -> Latin digits
                char in '٠'..'٩' -> {
                    sb.append((char.code - '٠'.code + '0'.code).toChar())
                }
                // Step 3: Arabic letters -> Persian letters
                char == 'ي' -> sb.append('ی')
                char == 'ك' -> sb.append('ک')
                char == 'ة' -> sb.append('ه')
                // Step 4: Remove thousands separators
                char == '٬' || char == ',' || char == '،' -> {
                    // Skip separator
                }
                else -> {
                    sb.append(char)
                }
            }
        }

        // Step 5: Collapse multiple whitespaces and trim
        return WHITESPACE_REGEX.replace(sb.toString(), " ").trim()
    }
}
