// app/src/main/java/ir/smsgaclient/data/db/entity/ParsedSmsEntity.kt
package ir.smsgaclient.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Structured parsed SMS record.
 */
@Entity(tableName = "parsed_sms")
data class ParsedSmsEntity(
    @PrimaryKey
    @ColumnInfo(name = "message_id")
    val messageId: String,

    @ColumnInfo(name = "bank_id")
    val bankId: String,

    @ColumnInfo(name = "type")
    val type: String, // DEPOSIT, WITHDRAW, UNKNOWN

    @ColumnInfo(name = "amount_rial")
    val amountRial: Long?,

    @ColumnInfo(name = "amount_toman")
    val amountToman: Long?,

    @ColumnInfo(name = "card_last4")
    val cardLast4: String?,

    @ColumnInfo(name = "balance_rial")
    val balanceRial: Long?,

    @ColumnInfo(name = "parse_version")
    val parseVersion: Int
)
