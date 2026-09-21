// app/src/main/java/ir/smsgaclient/domain/model/BankPattern.kt
package ir.smsgaclient.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Pattern definition for matching and parsing bank SMS messages.
 */
@Serializable
data class BankPattern(
    @SerialName("id")
    val id: String,

    @SerialName("name_fa")
    val nameFa: String,

    @SerialName("senders")
    val senders: List<String> = emptyList(),

    @SerialName("detect_regex")
    val detectRegex: String? = null,

    @SerialName("deposit_regex")
    val depositRegex: String? = null,

    @SerialName("withdraw_regex")
    val withdrawRegex: String? = null,

    @SerialName("card_regex")
    val cardRegex: String? = null,

    @SerialName("balance_regex")
    val balanceRegex: String? = null
)

/**
 * Set of bank patterns fetched from remote server or loaded from cache.
 */
@Serializable
data class PatternSet(
    @SerialName("version")
    val version: Int,

    @SerialName("updated_at")
    val updatedAt: String,

    @SerialName("banks")
    val banks: List<BankPattern>
)
