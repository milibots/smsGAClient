// app/src/main/java/ir/smsgaclient/data/db/entity/PatternCacheEntity.kt
package ir.smsgaclient.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Cached remote patterns ruleset.
 * Stores up to 3 versions for offline parsing (SYSTEM_PROMPT.md §2.4 Rule 17).
 */
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
