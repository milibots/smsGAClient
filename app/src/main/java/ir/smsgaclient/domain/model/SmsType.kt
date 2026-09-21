package ir.smsgaclient.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class SmsType {
    @SerialName("deposit")
    DEPOSIT,

    @SerialName("withdraw")
    WITHDRAW,

    @SerialName("unknown")
    UNKNOWN
}
