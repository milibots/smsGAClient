package ir.smsgaclient.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.smsgaclient.data.prefs.SecurePrefs
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val webhookUrl: String = "",
    val apiTokenMasked: String = "",
    val deviceId: String = "",
    val merchantId: String = "",
    val isPaired: Boolean = false,
    val onlyDeposits: Boolean = true,
    val quietHoursEnabled: Boolean = true,
    val errorMessage: String? = null
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val securePrefs: SecurePrefs
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        SettingsUiState(
            webhookUrl = securePrefs.webhookUrl ?: "",
            apiTokenMasked = securePrefs.mask(securePrefs.apiToken),
            deviceId = securePrefs.deviceId,
            merchantId = securePrefs.merchantId ?: "",
            isPaired = securePrefs.isPaired
        )
    )
    val uiState: StateFlow<SettingsUiState> = _uiState

    fun updateWebhookUrl(newUrl: String) {
        val isValidProtocol = newUrl.startsWith("https://", ignoreCase = true) ||
                newUrl.startsWith("http://", ignoreCase = true)

        if (!isValidProtocol && newUrl.isNotEmpty()) {
            _uiState.value = _uiState.value.copy(
                webhookUrl = newUrl,
                errorMessage = "آدرس وب‌هوک باید با http:// یا https:// شروع شود."
            )
            return
        }

        try {
            securePrefs.webhookUrl = if (newUrl.isEmpty()) null else newUrl
            _uiState.value = _uiState.value.copy(
                webhookUrl = newUrl,
                errorMessage = null
            )
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(errorMessage = e.message)
        }
    }

    fun revokePairing() {
        viewModelScope.launch {
            securePrefs.clearCredentials()
            _uiState.value = _uiState.value.copy(
                webhookUrl = "",
                apiTokenMasked = "",
                isPaired = false
            )
        }
    }
}
