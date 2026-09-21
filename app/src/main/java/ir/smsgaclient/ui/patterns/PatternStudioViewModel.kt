package ir.smsgaclient.ui.patterns

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.smsgaclient.data.db.dao.PatternCacheDao
import ir.smsgaclient.data.db.entity.PatternCacheEntity
import ir.smsgaclient.domain.model.BankPattern
import ir.smsgaclient.domain.model.PatternSet
import ir.smsgaclient.domain.model.SmsType
import ir.smsgaclient.domain.parser.CandidateEntities
import ir.smsgaclient.domain.parser.InferenceResult
import ir.smsgaclient.domain.parser.SmartPatternInferrer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class PatternStudioUiState(
    val rawSms: String = "",
    val sender: String = "",
    val bankId: String = "blu",
    val bankNameFa: String = "بلو",
    val transactionType: SmsType = SmsType.DEPOSIT,
    val amountRialStr: String = "",
    val cardLast4Str: String = "",
    val balanceRialStr: String = "",
    val candidates: CandidateEntities? = null,
    val inferenceResult: InferenceResult? = null,
    val isSaving: Boolean = false,
    val saveSuccessMessage: String? = null,
    val errorMessage: String? = null
)

@HiltViewModel
class PatternStudioViewModel @Inject constructor(
    private val patternCacheDao: PatternCacheDao,
    private val json: Json
) : ViewModel() {

    private val _uiState = MutableStateFlow(PatternStudioUiState())
    val uiState: StateFlow<PatternStudioUiState> = _uiState.asStateFlow()

    fun onRawSmsChanged(newText: String) {
        val candidates = if (newText.isNotBlank()) SmartPatternInferrer.scanCandidates(newText) else null
        _uiState.update { current ->
            val detectedBankId = candidates?.detectedBankId ?: current.bankId
            val detectedBankName = candidates?.detectedBankNameFa ?: current.bankNameFa
            val suggestedAmount = candidates?.suggestedAmounts?.firstOrNull()?.toString() ?: current.amountRialStr
            val suggestedCard = candidates?.suggestedCards?.firstOrNull() ?: current.cardLast4Str

            current.copy(
                rawSms = newText,
                candidates = candidates,
                bankId = detectedBankId,
                bankNameFa = detectedBankName,
                amountRialStr = if (current.amountRialStr.isBlank()) suggestedAmount else current.amountRialStr,
                cardLast4Str = if (current.cardLast4Str.isBlank()) suggestedCard else current.cardLast4Str,
                saveSuccessMessage = null,
                errorMessage = null
            )
        }
        testAndInfer()
    }

    fun onBankSelected(id: String, nameFa: String) {
        _uiState.update { it.copy(bankId = id, bankNameFa = nameFa) }
        testAndInfer()
    }

    fun onCustomBankChanged(id: String, nameFa: String) {
        _uiState.update { it.copy(bankId = id, bankNameFa = nameFa) }
        testAndInfer()
    }

    fun onTransactionTypeChanged(type: SmsType) {
        _uiState.update { it.copy(transactionType = type) }
        testAndInfer()
    }

    fun onAmountChanged(amountRial: String) {
        val clean = amountRial.filter { it.isDigit() }
        _uiState.update { it.copy(amountRialStr = clean) }
        testAndInfer()
    }

    fun onCardLast4Changed(card: String) {
        val clean = card.filter { it.isDigit() }.take(4)
        _uiState.update { it.copy(cardLast4Str = clean) }
        testAndInfer()
    }

    fun onBalanceChanged(balanceRial: String) {
        val clean = balanceRial.filter { it.isDigit() }
        _uiState.update { it.copy(balanceRialStr = clean) }
        testAndInfer()
    }

    fun onSenderChanged(sender: String) {
        _uiState.update { it.copy(sender = sender) }
        testAndInfer()
    }

    fun testAndInfer() {
        val state = _uiState.value
        val amount = state.amountRialStr.toLongOrNull()

        if (state.rawSms.isBlank() || amount == null) {
            _uiState.update { it.copy(inferenceResult = null) }
            return
        }

        val result = SmartPatternInferrer.inferAndValidate(
            rawText = state.rawSms,
            sender = state.sender,
            bankId = state.bankId,
            bankNameFa = state.bankNameFa,
            type = state.transactionType,
            targetAmountRial = amount,
            cardLast4 = state.cardLast4Str.ifBlank { null },
            balanceRial = state.balanceRialStr.toLongOrNull()
        )

        _uiState.update { it.copy(inferenceResult = result) }
    }

    fun savePatternToApp() {
        val result = _uiState.value.inferenceResult ?: return
        if (!result.isValid) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            try {
                val latestCache = patternCacheDao.getLatestPattern()
                val currentSet = if (latestCache != null) {
                    try {
                        json.decodeFromString<PatternSet>(latestCache.json)
                    } catch (e: Exception) {
                        DEFAULT_FALLBACK_PATTERNS
                    }
                } else {
                    DEFAULT_FALLBACK_PATTERNS
                }

                val updatedBanks = currentSet.banks.filter { it.id != result.pattern.id } + result.pattern
                val newVersion = (currentSet.version + 1).coerceAtLeast(100)
                val isoDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).format(Date())

                val newPatternSet = currentSet.copy(
                    version = newVersion,
                    updatedAt = isoDate,
                    banks = updatedBanks
                )

                val encodedJson = json.encodeToString(newPatternSet)
                patternCacheDao.insert(
                    PatternCacheEntity(
                        version = newVersion,
                        json = encodedJson,
                        fetchedAt = System.currentTimeMillis()
                    )
                )

                _uiState.update {
                    it.copy(
                        isSaving = false,
                        saveSuccessMessage = "الگوی جدید با موفقیت ذخیره و فعال شد! پیامک‌های بعدی این فرمت آنی پردازش می‌شوند."
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = "خطا در ذخیره‌سازی الگو: ${e.message}"
                    )
                }
            }
        }
    }

    fun getPatternJson(): String? {
        val pattern = _uiState.value.inferenceResult?.pattern ?: return null
        return try {
            json.encodeToString(pattern)
        } catch (e: Exception) {
            null
        }
    }

    companion object {
        private val DEFAULT_FALLBACK_PATTERNS = PatternSet(
            version = 1,
            updatedAt = "2026-09-21T00:00:00Z",
            banks = emptyList()
        )
    }
}
