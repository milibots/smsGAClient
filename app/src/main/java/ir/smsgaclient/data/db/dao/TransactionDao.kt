package ir.smsgaclient.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ir.smsgaclient.data.db.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: TransactionEntity)

    @Query("SELECT * FROM transactions WHERE message_id = :messageId LIMIT 1")
    suspend fun getByMessageId(messageId: String): TransactionEntity?

    @Query("SELECT * FROM transactions ORDER BY received_at DESC")
    fun getAllTransactionsFlow(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions ORDER BY received_at DESC LIMIT :limit")
    fun getRecentTransactions(limit: Int = 3): Flow<List<TransactionEntity>>

    @Query("SELECT SUM(amount_rial) FROM transactions WHERE received_at >= :startOfDayTimestamp")
    fun getTodayTotalSalesRial(startOfDayTimestamp: Long): Flow<Long?>

    @Query("SELECT COUNT(*) FROM transactions WHERE received_at >= :startOfDayTimestamp")
    fun getTodayTransactionCount(startOfDayTimestamp: Long): Flow<Int>

    @Query("SELECT COUNT(*) FROM transactions WHERE status = 'PENDING'")
    fun getPendingQueueCount(): Flow<Int>

    @Query("UPDATE transactions SET status = :status WHERE message_id = :messageId")
    suspend fun updateStatus(messageId: String, status: String)

    @Query("DELETE FROM transactions WHERE received_at < :olderThanTimestamp")
    suspend fun pruneOlderThan(olderThanTimestamp: Long): Int
}
