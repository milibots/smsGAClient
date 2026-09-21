package ir.smsgaclient.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "patterns_cache")
data class PatternCacheEntity(
    @PrimaryKey
    @ColumnInfo(name = "version")
    val version: Int,

    @ColumnInfo(name = "json")
    val json: String,

    @ColumnInfo(name = "fetched_at")
    val fetchedAt: Long
)
