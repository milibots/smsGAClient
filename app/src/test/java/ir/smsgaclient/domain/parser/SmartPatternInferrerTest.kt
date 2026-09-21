package ir.smsgaclient.domain.parser

import ir.smsgaclient.domain.model.SmsType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class SmartPatternInferrerTest {

    @Test
    fun `scans sample text and extracts candidate numbers and bank`() {
        val sample = "واریز مبلغ ۵۰۰,۰۰۰ ریال به کارت ۶۰۳۷...۴۸۲۱ بانک صادرات موجودی: ۱۰,۰۰۰,۰۰۰ ریال"
        val candidates = SmartPatternInferrer.scanCandidates(sample)

        assertEquals("saderat", candidates.detectedBankId)
        assertEquals("صادرات", candidates.detectedBankNameFa)
        assertTrue(candidates.suggestedAmounts.contains(500000L))
        assertTrue(candidates.suggestedCards.contains("4821"))
        assertTrue(candidates.suggestedAmounts.contains(10000000L))
    }

    @Test
    fun `infers valid deposit pattern and verifies parsing`() {
        val sample = "واریز ۲,۴۰۰,۰۰۰ ریال به حساب ****۹۸۲۱ بلوبانک موجودی: ۵۰,۰۰۰,۰۰۰ ریال"
        
        val result = SmartPatternInferrer.inferAndValidate(
            rawText = sample,
            sender = "BLUBANK",
            bankId = "blu",
            bankNameFa = "بلو",
            type = SmsType.DEPOSIT,
            targetAmountRial = 2400000L,
            cardLast4 = "9821",
            balanceRial = 50000000L
        )

        assertTrue(result.isValid, "Inference result should be valid")
        assertNotNull(result.parsedSms)
        assertEquals("blu", result.parsedSms?.bankId)
        assertEquals(SmsType.DEPOSIT, result.parsedSms?.type)
        assertEquals(2400000L, result.parsedSms?.amountRial)
        assertEquals(240000L, result.parsedSms?.amountToman)
        assertEquals("9821", result.parsedSms?.cardLast4)
        assertEquals(50000000L, result.parsedSms?.balanceRial)
    }

    @Test
    fun `infers custom bank format with plus sign`() {
        val sample = "+۳,۵۰۰,۰۰۰ ریال کارت ۱۲۳۴ بانک دلخواه موجودی: ۴,۰۰۰,۰۰۰ ریال"

        val result = SmartPatternInferrer.inferAndValidate(
            rawText = sample,
            sender = "CUSTOM_BANK",
            bankId = "custom",
            bankNameFa = "بانک دلخواه",
            type = SmsType.DEPOSIT,
            targetAmountRial = 3500000L,
            cardLast4 = "1234",
            balanceRial = 4000000L
        )

        assertTrue(result.isValid)
        assertEquals(3500000L, result.parsedSms?.amountRial)
        assertEquals("1234", result.parsedSms?.cardLast4)
    }
}
