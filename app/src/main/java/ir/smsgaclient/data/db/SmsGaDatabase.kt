package ir.smsgaclient.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import ir.smsgaclient.data.db.dao.CardDao
import ir.smsgaclient.data.db.dao.ForwardAttemptDao
import ir.smsgaclient.data.db.dao.ParsedSmsDao
import ir.smsgaclient.data.db.dao.PatternCacheDao
import ir.smsgaclient.data.db.dao.RawSmsDao
import ir.smsgaclient.data.db.dao.TransactionDao
import ir.smsgaclient.data.db.entity.CardEntity
import ir.smsgaclient.data.db.entity.ForwardAttemptEntity
import ir.smsgaclient.data.db.entity.ParsedSmsEntity
import ir.smsgaclient.data.db.entity.PatternCacheEntity
import ir.smsgaclient.data.db.entity.RawSmsEntity
import ir.smsgaclient.data.db.entity.TransactionEntity

@Database(
    entities = [
        RawSmsEntity::class,
        ParsedSmsEntity::class,
        TransactionEntity::class,
        CardEntity::class,
        ForwardAttemptEntity::class,
        PatternCacheEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class SmsGaDatabase : RoomDatabase() {

    abstract fun rawSmsDao(): RawSmsDao
    abstract fun parsedSmsDao(): ParsedSmsDao
    abstract fun transactionDao(): TransactionDao
    abstract fun cardDao(): CardDao
    abstract fun forwardAttemptDao(): ForwardAttemptDao
    abstract fun patternCacheDao(): PatternCacheDao

    companion object {
        const val DATABASE_NAME = "smsga_db"
    }
}
