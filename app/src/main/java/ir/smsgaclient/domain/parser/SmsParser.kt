package ir.smsgaclient.domain.parser

import ir.smsgaclient.domain.model.BankPattern
import ir.smsgaclient.domain.model.ParsedSms
import ir.smsgaclient.domain.model.PatternSet
import ir.smsgaclient.domain.model.SmsType
import java.security.MessageDigest
import java.util.regex.Matcher
import java.util.regex.Pattern

object SmsParser {

    fun parse(
        rawSms: String,
        sender: String,
        receivedAtEpochMilli: Long,
        patternSet: PatternSet
    ): ParsedSms {
        val messageId = generateMessageId(rawSms, receivedAtEpochMilli)

        return try {
            val normalized = SmsNormalizer.normalize(rawSms)
            val detector = BankDetector(patternSet.banks)
            val bank = detector.detect(sender, normalized)

            if (bank.id == BankDetector.UNKNOWN_BANK.id) {
                return ParsedSms(
                    messageId = messageId,
                    bankId = "unknown",
                    type = SmsType.UNKNOWN,
                    amountRial = null,
                    amountToman = null,
                    cardLast4 = null,
                    balanceRial = null,
                    rawSms = rawSms,
                    sender = sender,
                    receivedAtEpochMilli = receivedAtEpochMilli,
                    parseVersion = patternSet.version
                )
            }

            val depositMatcher = bank.depositRegex?.let {
                Pattern.compile(it, Pattern.CASE_INSENSITIVE).matcher(normalized)
            }

            if (depositMatcher != null && depositMatcher.find()) {
                val amount = extractNamedGroup(depositMatcher, "amount")?.toLongOrNull()
                val card = extractCardLast4(bank, normalized)
                val balance = extractBalance(bank, normalized)

                return ParsedSms(
                    messageId = messageId,
                    bankId = bank.id,
                    type = SmsType.DEPOSIT,
                    amountRial = amount,
                    amountToman = amount?.div(10),
                    cardLast4 = card,
                    balanceRial = balance,
                    rawSms = rawSms,
                    sender = sender,
                    receivedAtEpochMilli = receivedAtEpochMilli,
                    parseVersion = patternSet.version
                )
            }

            val withdrawMatcher = bank.withdrawRegex?.let {
                Pattern.compile(it, Pattern.CASE_INSENSITIVE).matcher(normalized)
            }

            if (withdrawMatcher != null && withdrawMatcher.find()) {
                val amount = extractNamedGroup(withdrawMatcher, "amount")?.toLongOrNull()
                val card = extractCardLast4(bank, normalized)
                val balance = extractBalance(bank, normalized)

                return ParsedSms(
                    messageId = messageId,
                    bankId = bank.id,
                    type = SmsType.WITHDRAW,
                    amountRial = amount,
                    amountToman = amount?.div(10),
                    cardLast4 = card,
                    balanceRial = balance,
                    rawSms = rawSms,
                    sender = sender,
                    receivedAtEpochMilli = receivedAtEpochMilli,
                    parseVersion = patternSet.version
                )
            }

            ParsedSms(
                messageId = messageId,
                bankId = bank.id,
                type = SmsType.UNKNOWN,
                amountRial = null,
                amountToman = null,
                cardLast4 = extractCardLast4(bank, normalized),
                balanceRial = extractBalance(bank, normalized),
                rawSms = rawSms,
                sender = sender,
                receivedAtEpochMilli = receivedAtEpochMilli,
                parseVersion = patternSet.version
            )
        } catch (e: Throwable) {

            ParsedSms(
                messageId = messageId,
                bankId = "unknown",
                type = SmsType.UNKNOWN,
                amountRial = null,
                amountToman = null,
                cardLast4 = null,
                balanceRial = null,
                rawSms = rawSms,
                sender = sender,
                receivedAtEpochMilli = receivedAtEpochMilli,
                parseVersion = patternSet.version
            )
        }
    }

    private fun extractCardLast4(bank: BankPattern, normalized: String): String? {
        val regex = bank.cardRegex ?: return null
        val matcher = Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(normalized)
        while (matcher.find()) {
            val rawCard = extractNamedGroup(matcher, "card") ?: matcher.group(0)
            val digitsOnly = rawCard?.filter { it.isDigit() } ?: continue

            if (rawCard.contains('*')) {
                return if (digitsOnly.length >= 4) digitsOnly.takeLast(4) else digitsOnly
            }

            if (digitsOnly.length == 4) {
                val start = matcher.start()
                val end = matcher.end()
                val prevChar = if (start > 0) normalized[start - 1] else ' '
                val nextChar = if (end < normalized.length) normalized[end] else ' '
                if (!prevChar.isDigit() && !nextChar.isDigit()) {
                    return digitsOnly
                }
            }
        }
        return null
    }

    private fun extractBalance(bank: BankPattern, normalized: String): Long? {
        val regex = bank.balanceRegex ?: return null
        val matcher = Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(normalized)
        if (matcher.find()) {
            return extractNamedGroup(matcher, "balance")?.toLongOrNull()
        }
        return null
    }

    private fun extractNamedGroup(matcher: Matcher, groupName: String): String? {
        return try {
            matcher.group(groupName)
        } catch (e: IllegalArgumentException) {
            null
        }
    }

    fun generateMessageId(rawSms: String, receivedAtEpochMilli: Long): String {
        val payload = "$rawSms:$receivedAtEpochMilli"
        val digest = MessageDigest.getInstance("SHA-256").digest(payload.toByteArray(Charsets.UTF_8))
        return "sha256:" + digest.joinToString("") { "%02x".format(it) }
    }
}
