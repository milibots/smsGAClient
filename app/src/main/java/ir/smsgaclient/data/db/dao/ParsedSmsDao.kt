// app/src/main/java/ir/smsgaclient/data/db/dao/ParsedSmsDao.kt
package ir.smsgaclient.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ir.smsgaclient.data.db.entity.ParsedSmsEntity

/**
 * Data Access Object for parsed SMS records.
 */
@Dao
interface ParsedSmsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(parsed: ParsedSmsEntity)

    @Query("SELECT * FROM parsed_sms WHERE message_id = :messageId LIMIT 1")
    suspend fun getByMessageId(messageId: String): ParsedSmsEntity?
}
