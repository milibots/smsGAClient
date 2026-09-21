package ir.smsgaclient.domain.parser

import ir.smsgaclient.domain.model.BankPattern
import ir.smsgaclient.domain.model.ParsedSms
import ir.smsgaclient.domain.model.PatternSet
import ir.smsgaclient.domain.model.SmsType
import java.util.regex.Pattern

data class CandidateEntities(
    val allNumbers: List<String>,
    val suggestedAmounts: List<Long>,
    val suggestedCards: List<String>,
    val detectedBankId: String?,
    val detectedBankNameFa: String?
)

data class InferenceResult(
    val isValid: Boolean,
    val pattern: BankPattern,
    val parsedSms: ParsedSms?,
    val error: String? = null
)

object SmartPatternInferrer {

    private val BANK_KEYWORDS = mapOf(
        "blu" to Pair("بلو", "بلو|blu|blubank"),
        "mellat" to Pair("ملت", "ملت|mellat"),
        "saderat" to Pair("صادرات", "صادرات|saderat"),
        "melli" to Pair("ملی", "ملی|melli"),
        "tejarat" to Pair("تجارت", "تجارت|tejarat"),
        "saman" to Pair("سامان", "سامان|saman"),
        "pasargad" to Pair("پاسارگاد", "پاسارگاد|pasargad"),
        "sepah" to Pair("سپه", "سپه|sepah"),
        "parsian" to Pair("پارسیان", "پارسیان|parsian"),
        "refah" to Pair("رفاه", "رفاه|refah"),
        "shahr" to Pair("شهر", "شهر|shahr"),
        "ayandeh" to Pair("آینده", "آینده|ayandeh"),
        "keshavarzi" to Pair("کشاورزی", "کشاورزی|keshavarzi"),
        "resalat" to Pair("رسالت", "رسالت|resalat"),
        "postbank" to Pair("پست بانک", "پست بانک|postbank")
    )

    fun scanCandidates(rawText: String): CandidateEntities {
        val normalized = SmsNormalizer.normalize(rawText)
        val numberRegex = Regex("\\d+")
        val matches = numberRegex.findAll(normalized).map { it.value }.toList()

        val amounts = matches.mapNotNull { it.toLongOrNull() }.filter { it >= 1000 }
        val cards = matches.filter { it.length == 4 }

        var detectedId: String? = null
        var detectedName: String? = null

        for ((id, pair) in BANK_KEYWORDS) {
            val (nameFa, regexStr) = pair
            if (Pattern.compile(regexStr, Pattern.CASE_INSENSITIVE).matcher(normalized).find()) {
                detectedId = id
                detectedName = nameFa
                break
            }
        }

        return CandidateEntities(
            allNumbers = matches,
            suggestedAmounts = amounts,
            suggestedCards = cards,
            detectedBankId = detectedId,
            detectedBankNameFa = detectedName
        )
    }

    fun inferAndValidate(
        rawText: String,
        sender: String,
        bankId: String,
        bankNameFa: String,
        type: SmsType,
        targetAmountRial: Long,
        cardLast4: String? = null,
        balanceRial: Long? = null
    ): InferenceResult {
        if (rawText.isBlank()) {
            return InferenceResult(
                isValid = false,
                pattern = BankPattern(id = bankId, nameFa = bankNameFa),
                parsedSms = null,
                error = "متن پیامک خالی است"
            )
        }

        val normalized = SmsNormalizer.normalize(rawText)
        val amountStr = targetAmountRial.toString()

        if (!normalized.contains(amountStr)) {
            return InferenceResult(
                isValid = false,
                pattern = BankPattern(id = bankId, nameFa = bankNameFa),
                parsedSms = null,
                error = "مبلغ $amountStr ریال در متن پیامک یافت نشد"
            )
        }

        val detectRegex = if (bankId in BANK_KEYWORDS) {
            "(${BANK_KEYWORDS[bankId]?.second})"
        } else {
            "(${Pattern.quote(bankNameFa)}|${Pattern.quote(bankId)})"
        }

        val amountPattern = buildAmountRegex(normalized, amountStr)
        val cardPattern = if (!cardLast4.isNullOrBlank() && normalized.contains(cardLast4)) {
            buildCardRegex(normalized, cardLast4)
        } else {
            "(?<card>\\*{0,4}\\d{4})"
        }

        val balancePattern = if (balanceRial != null && normalized.contains(balanceRial.toString())) {
            buildBalanceRegex(normalized, balanceRial.toString())
        } else {
            "موجودی[:\\s]*(?<balance>\\d+)"
        }

        val generatedBankPattern = BankPattern(
            id = bankId.ifBlank { "custom_bank" },
            nameFa = bankNameFa.ifBlank { "بانک اختصاصی" },
            senders = if (sender.isNotBlank()) listOf(sender) else emptyList(),
            detectRegex = detectRegex,
            depositRegex = if (type == SmsType.DEPOSIT) amountPattern else null,
            withdrawRegex = if (type == SmsType.WITHDRAW) amountPattern else null,
            cardRegex = cardPattern,
            balanceRegex = balancePattern
        )

        val testPatternSet = PatternSet(
            version = 999,
            updatedAt = "2026-09-21T00:00:00Z",
            banks = listOf(generatedBankPattern)
        )

        val parsed = SmsParser.parse(
            rawSms = rawText,
            sender = sender.ifBlank { bankId },
            receivedAtEpochMilli = System.currentTimeMillis(),
            patternSet = testPatternSet
        )

        val isValid = parsed.type == type && parsed.amountRial == targetAmountRial
        val errorMsg = if (!isValid) {
            "تطبیق الگو ناموفق بود: نوع استخراج شده=${parsed.type}، مبلغ=${parsed.amountRial}"
        } else null

        return InferenceResult(
            isValid = isValid,
            pattern = generatedBankPattern,
            parsedSms = parsed,
            error = errorMsg
        )
    }

    private fun buildAmountRegex(normalized: String, amountStr: String): String {
        val idx = normalized.indexOf(amountStr)
        val prefix = if (idx > 0) {
            val before = normalized.substring(0, idx).trimEnd()
            val lastWord = before.split(" ").lastOrNull()?.trim() ?: ""
            if (lastWord.contains("+")) {
                "\\+"
            } else if (lastWord.contains("-")) {
                "-"
            } else if (lastWord.isNotBlank() && lastWord.length in 2..15) {
                "${Pattern.quote(lastWord)}[:\\s]*"
            } else {
                "(?:واریز|\\+|مبلغ)[:\\s]*"
            }
        } else {
            "(?:واریز|\\+|مبلغ)[:\\s]*"
        }

        val suffix = if (idx + amountStr.length < normalized.length) {
            val after = normalized.substring(idx + amountStr.length).trimStart()
            val nextWord = after.split(" ").firstOrNull()?.trim() ?: ""
            if (nextWord.contains("ریال") || nextWord.contains("تومان")) {
                "\\s*(?:ریال|تومان)?"
            } else {
                "\\s*ریال?"
            }
        } else {
            "\\s*ریال?"
        }

        return "$prefix(?<amount>\\d+)$suffix"
    }

    private fun buildCardRegex(normalized: String, cardStr: String): String {
        val idx = normalized.indexOf(cardStr)
        if (idx > 0) {
            val before = normalized.substring(0, idx).trimEnd()
            val lastWord = before.split(" ").lastOrNull()?.trim() ?: ""
            if (lastWord.contains("*") || lastWord.contains("کارت") || lastWord.contains("حساب")) {
                return "(?:کارت|حساب|\\*+)[:\\s]*(?<card>\\d{4})"
            }
        }
        return "(?<card>\\*{0,4}\\d{4})"
    }

    private fun buildBalanceRegex(normalized: String, balanceStr: String): String {
        val idx = normalized.indexOf(balanceStr)
        if (idx > 0) {
            val before = normalized.substring(0, idx).trimEnd()
            val lastWord = before.split(" ").lastOrNull()?.trim() ?: ""
            if (lastWord.contains("موجودی") || lastWord.contains("مانده")) {
                return "(?:موجودی|مانده)[:\\s]*(?<balance>\\d+)"
            }
        }
        return "موجودی[:\\s]*(?<balance>\\d+)"
    }
}
