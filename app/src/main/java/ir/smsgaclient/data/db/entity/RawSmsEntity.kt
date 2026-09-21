// app/src/main/java/ir/smsgaclient/data/db/entity/RawSmsEntity.kt
package ir.smsgaclient.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Raw SMS record persisted before any parsing or networking.
 * Zero SMS loss guarantee (SYSTEM_PROMPT.md §2.2 Rule 6).
 */
@Entity(
    tableName = "raw_sms",
    indices = [
        Index(value = ["message_id"], unique = true),
        Index(value = ["status"]),
        Index(value = ["received_at"])
    ]
)
data class RawSmsEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "message_id")
    val messageId: String,

    @ColumnInfo(name = "sender")
    val sender: String,

    @ColumnInfo(name = "body")
    val body: String,

    @ColumnInfo(name = "received_at")
    val receivedAt: Long,

    @ColumnInfo(name = "status")
    val status: String // RECEIVED, PARSED, FORWARDED, FAILED
)
