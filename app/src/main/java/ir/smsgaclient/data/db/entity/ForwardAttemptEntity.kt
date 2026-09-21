// app/src/main/java/ir/smsgaclient/data/db/entity/ForwardAttemptEntity.kt
package ir.smsgaclient.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Log of webhook forward HTTP attempts.
 */
@Entity(
    tableName = "forward_attempts",
    indices = [
        Index(value = ["message_id"]),
        Index(value = ["attempted_at"])
    ]
)
data class ForwardAttemptEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "message_id")
    val messageId: String,

    @ColumnInfo(name = "attempted_at")
    val attemptedAt: Long,

    @ColumnInfo(name = "http_status")
    val httpStatus: Int?,

    @ColumnInfo(name = "error")
    val error: String?,

    @ColumnInfo(name = "response_body")
    val responseBody: String?
)
