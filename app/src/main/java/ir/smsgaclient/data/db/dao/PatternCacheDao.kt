package ir.smsgaclient.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ir.smsgaclient.data.db.entity.PatternCacheEntity

@Dao
interface PatternCacheDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(pattern: PatternCacheEntity)

    @Query("SELECT * FROM patterns_cache ORDER BY version DESC LIMIT 1")
    suspend fun getLatestPattern(): PatternCacheEntity?

    @Query("SELECT * FROM patterns_cache WHERE version = :version LIMIT 1")
    suspend fun getPatternByVersion(version: Int): PatternCacheEntity?

    @Query("""
        DELETE FROM patterns_cache
        WHERE version NOT IN (
            SELECT version FROM patterns_cache ORDER BY version DESC LIMIT 3
        )
    """)
    suspend fun pruneOldVersions()
}
