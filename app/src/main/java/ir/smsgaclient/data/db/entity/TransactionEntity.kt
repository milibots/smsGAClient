package ir.smsgaclient.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "transactions",
    indices = [
        Index(value = ["received_at"]),
        Index(value = ["status"]),
        Index(value = ["order_id"])
    ]
)
data class TransactionEntity(
    @PrimaryKey
    @ColumnInfo(name = "message_id")
    val messageId: String,

    @ColumnInfo(name = "order_id")
    val orderId: String? = null,

    @ColumnInfo(name = "amount_rial")
    val amountRial: Long,

    @ColumnInfo(name = "bank_id")
    val bankId: String,

    @ColumnInfo(name = "card_last4")
    val cardLast4: String?,

    @ColumnInfo(name = "status")
    val status: String,

    @ColumnInfo(name = "received_at")
    val receivedAt: Long,

    @ColumnInfo(name = "note")
    val note: String? = null
)
