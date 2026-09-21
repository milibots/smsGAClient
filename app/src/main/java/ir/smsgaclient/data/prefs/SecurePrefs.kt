package ir.smsgaclient.data.prefs

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SecurePrefs @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val masterKey: MasterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        PREFS_NAME,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    var webhookUrl: String?
        get() = prefs.getString(KEY_WEBHOOK_URL, null)
        set(value) {
            if (value != null) {
                require(
                    value.startsWith("https://", ignoreCase = true) ||
                    value.startsWith("http://", ignoreCase = true)
                ) {
                    "Webhook URL must use HTTP or HTTPS protocol."
                }
            }
            prefs.edit().putString(KEY_WEBHOOK_URL, value).apply()
        }

    var apiToken: String?
        get() = prefs.getString(KEY_API_TOKEN, null)
        set(value) = prefs.edit().putString(KEY_API_TOKEN, value).apply()

    var hmacSecret: String?
        get() = prefs.getString(KEY_HMAC_SECRET, null)
        set(value) = prefs.edit().putString(KEY_HMAC_SECRET, value).apply()

    var merchantId: String?
        get() = prefs.getString(KEY_MERCHANT_ID, null)
        set(value) = prefs.edit().putString(KEY_MERCHANT_ID, value).apply()

    var isPaired: Boolean
        get() = prefs.getBoolean(KEY_IS_PAIRED, false)
        set(value) = prefs.edit().putBoolean(KEY_IS_PAIRED, value).apply()

    private val _themeModeFlow = MutableStateFlow(prefs.getString(KEY_THEME_MODE, "SYSTEM") ?: "SYSTEM")
    val themeModeFlow: StateFlow<String> = _themeModeFlow.asStateFlow()

    var themeMode: String
        get() = prefs.getString(KEY_THEME_MODE, "SYSTEM") ?: "SYSTEM"
        set(value) {
            prefs.edit().putString(KEY_THEME_MODE, value).apply()
            _themeModeFlow.value = value
        }

    val deviceId: String
        get() {
            var id = prefs.getString(KEY_DEVICE_ID, null)
            if (id == null) {
                id = UUID.randomUUID().toString()
                prefs.edit().putString(KEY_DEVICE_ID, id).apply()
            }
            return id
        }

    fun clearCredentials() {
        prefs.edit()
            .remove(KEY_WEBHOOK_URL)
            .remove(KEY_API_TOKEN)
            .remove(KEY_HMAC_SECRET)
            .remove(KEY_MERCHANT_ID)
            .putBoolean(KEY_IS_PAIRED, false)
            .apply()
    }

    fun mask(secret: String?): String {
        if (secret.isNullOrEmpty()) return ""
        return if (secret.length <= 8) {
            "***"
        } else {
            "${secret.take(4)}...${secret.takeLast(4)}"
        }
    }

    companion object {
        private const val PREFS_NAME = "smsga_secure"
        private const val KEY_WEBHOOK_URL = "webhook_url"
        private const val KEY_API_TOKEN = "api_token"
        private const val KEY_HMAC_SECRET = "hmac_secret"
        private const val KEY_MERCHANT_ID = "merchant_id"
        private const val KEY_DEVICE_ID = "device_id"
        private const val KEY_IS_PAIRED = "is_paired"
        private const val KEY_THEME_MODE = "theme_mode"
    }
}
