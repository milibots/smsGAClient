package ir.smsgaclient.domain.parser

import ir.smsgaclient.domain.model.BankPattern
import java.util.regex.Pattern

class BankDetector(private val patterns: List<BankPattern>) {

    private val compiledDetectors: List<Pair<BankPattern, Pattern?>> = patterns.map { bank ->
        val regex = bank.detectRegex?.let { Pattern.compile(it, Pattern.CASE_INSENSITIVE) }
        bank to regex
    }

    fun detect(sender: String, normalizedBody: String): BankPattern {
        val trimmedSender = sender.trim()

        for ((bank, _) in compiledDetectors) {
            for (bankSender in bank.senders) {
                if (bankSender.isNotBlank() && bankSender.equals(trimmedSender, ignoreCase = true)) {
                    return bank
                }
            }
        }

        for ((bank, regex) in compiledDetectors) {
            if (regex != null && regex.matcher(normalizedBody).find()) {
                return bank
            }
        }

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
