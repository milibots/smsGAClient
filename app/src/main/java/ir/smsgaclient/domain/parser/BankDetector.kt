// app/src/main/java/ir/smsgaclient/domain/parser/BankDetector.kt
package ir.smsgaclient.domain.parser

import ir.smsgaclient.domain.model.BankPattern
import java.util.regex.Pattern

/**
 * Detects which bank sent the SMS by evaluating sender address and regex patterns.
 */
class BankDetector(private val patterns: List<BankPattern>) {

    private val compiledDetectors: List<Pair<BankPattern, Pattern?>> = patterns.map { bank ->
        val regex = bank.detectRegex?.let { Pattern.compile(it, Pattern.CASE_INSENSITIVE) }
        bank to regex
    }

    /**
     * Detects bank from sender address or message body.
     *
     * @param sender The SMS sender address (e.g. "BLUBANK", "983000...")
     * @param normalizedBody The 5-step normalized SMS text
     * @return Matching [BankPattern], or fallback unknown pattern
     */
    fun detect(sender: String, normalizedBody: String): BankPattern {
        val trimmedSender = sender.trim()

        // 1. Try exact sender matching (case-insensitive)
        for ((bank, _) in compiledDetectors) {
            for (bankSender in bank.senders) {
                if (bankSender.isNotBlank() && bankSender.equals(trimmedSender, ignoreCase = true)) {
                    return bank
                }
            }
        }

        // 2. Try regex matching on normalized body
        for ((bank, regex) in compiledDetectors) {
            if (regex != null && regex.matcher(normalizedBody).find()) {
                return bank
            }
        }

        // Fallback to unknown bank pattern
        return UNKNOWN_BANK
    }

    companion object {
        val UNKNOWN_BANK = BankPattern(
            id = "unknown",
            nameFa = "بانک نامشخص",
            senders = emptyList(),
            detectRegex = null,
            depositRegex = null,
            withdrawRegex = null,
            cardRegex = null,
            balanceRegex = null
        )
    }
}
