// app/src/main/java/ir/smsgaclient/data/remote/dto/PairingDto.kt
package ir.smsgaclient.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PairPollRequest(
    @SerialName("device_id")
    val deviceId: String,

    @SerialName("pairing_code")
    val pairingCode: String,

    @SerialName("app_version")
    val appVersion: String
)

@Serializable
data class PairPollResponse(
    @SerialName("status")
    val status: String, // "paired" or "pending"

    @SerialName("merchant_id")
    val merchantId: String? = null,

    @SerialName("webhook_url")
    val webhookUrl: String? = null,

    @SerialName("api_token")
    val apiToken: String? = null,

    @SerialName("hmac_secret")
    val hmacSecret: String? = null,

    @SerialName("retry_after_seconds")
    val retryAfterSeconds: Int? = null
)
