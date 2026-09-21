package ir.smsgaclient.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ir.smsgaclient.data.db.entity.RawSmsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RawSmsDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(sms: RawSmsEntity): Long

    @Query("SELECT * FROM raw_sms WHERE message_id = :messageId LIMIT 1")
    suspend fun getByMessageId(messageId: String): RawSmsEntity?

    @Query("SELECT * FROM raw_sms WHERE status = :status ORDER BY received_at ASC")
    fun getPendingSms(status: String = "RECEIVED"): Flow<List<RawSmsEntity>>

    @Query("UPDATE raw_sms SET status = :newStatus WHERE message_id = :messageId")
    suspend fun updateStatus(messageId: String, newStatus: String)

    @Query("DELETE FROM raw_sms WHERE received_at < :olderThanTimestamp")
    suspend fun pruneOlderThan(olderThanTimestamp: Long): Int
}
