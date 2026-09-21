// app/src/main/java/ir/smsgaclient/domain/model/SmsType.kt
package ir.smsgaclient.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Types of transaction events represented by bank SMS.
 */
@Serializable
enum class SmsType {
    @SerialName("deposit")
    DEPOSIT,

    @SerialName("withdraw")
    WITHDRAW,

    @SerialName("unknown")
    UNKNOWN
}
