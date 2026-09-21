// app/src/main/java/ir/smsgaclient/data/db/dao/CardDao.kt
package ir.smsgaclient.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import ir.smsgaclient.data.db.entity.CardEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for merchant receiving cards and daily limit calculations.
 */
@Dao
interface CardDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(card: CardEntity): Long

    @Update
    suspend fun update(card: CardEntity)

    @Query("SELECT * FROM cards ORDER BY priority DESC, id ASC")
    fun getAllCards(): Flow<List<CardEntity>>

    @Query("SELECT * FROM cards WHERE is_active = 1 ORDER BY priority DESC")
    fun getActiveCards(): Flow<List<CardEntity>>

    @Query("SELECT * FROM cards WHERE id = :cardId LIMIT 1")
    suspend fun getCardById(cardId: Long): CardEntity?

    @Query("""
        SELECT COALESCE(SUM(amount_rial), 0)
        FROM transactions
        WHERE card_last4 = :last4
          AND status != 'FAILED'
          AND received_at >= :startOfDayTimestamp
    """)
    fun getCardDailyUsageRial(last4: String, startOfDayTimestamp: Long): Flow<Long>

    @Query("DELETE FROM cards WHERE id = :cardId")
    suspend fun deleteCard(cardId: Long)
}
