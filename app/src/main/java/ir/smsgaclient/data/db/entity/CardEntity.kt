package ir.smsgaclient.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cards")
data class CardEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "bank_id")
    val bankId: String,

    @ColumnInfo(name = "last4")
    val last4: String,

    @ColumnInfo(name = "holder_name")
    val holderName: String,

    @ColumnInfo(name = "daily_limit_rial")
    val dailyLimitRial: Long,

    @ColumnInfo(name = "is_active")
    val isActive: Boolean = true,

    @ColumnInfo(name = "priority")
    val priority: Int = 0,

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "pending_sync")
    val pendingSync: Boolean = false
)
