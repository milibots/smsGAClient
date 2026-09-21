package ir.smsgaclient.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ir.smsgaclient.data.db.entity.ParsedSmsEntity

@Dao
interface ParsedSmsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(parsed: ParsedSmsEntity)

    @Query("SELECT * FROM parsed_sms WHERE message_id = :messageId LIMIT 1")
    suspend fun getByMessageId(messageId: String): ParsedSmsEntity?
}
