package ir.smsgaclient.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import ir.smsgaclient.data.db.entity.ForwardAttemptEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ForwardAttemptDao {

    @Insert
    suspend fun insert(attempt: ForwardAttemptEntity): Long

    @Query("SELECT * FROM forward_attempts WHERE message_id = :messageId ORDER BY attempted_at DESC")
    fun getAttemptsForMessage(messageId: String): Flow<List<ForwardAttemptEntity>>

    @Query("DELETE FROM forward_attempts WHERE attempted_at < :olderThanTimestamp")
    suspend fun pruneOlderThan(olderThanTimestamp: Long): Int
}
