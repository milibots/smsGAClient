package ir.smsgaclient.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ir.smsgaclient.data.db.SmsGaDatabase
import ir.smsgaclient.data.db.dao.CardDao
import ir.smsgaclient.data.db.dao.ForwardAttemptDao
import ir.smsgaclient.data.db.dao.ParsedSmsDao
import ir.smsgaclient.data.db.dao.PatternCacheDao
import ir.smsgaclient.data.db.dao.RawSmsDao
import ir.smsgaclient.data.db.dao.TransactionDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): SmsGaDatabase {
        return Room.databaseBuilder(
            context,
            SmsGaDatabase::class.java,
            SmsGaDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    fun provideRawSmsDao(db: SmsGaDatabase): RawSmsDao = db.rawSmsDao()

    @Provides
    fun provideParsedSmsDao(db: SmsGaDatabase): ParsedSmsDao = db.parsedSmsDao()

    @Provides
    fun provideTransactionDao(db: SmsGaDatabase): TransactionDao = db.transactionDao()

    @Provides
    fun provideCardDao(db: SmsGaDatabase): CardDao = db.cardDao()

    @Provides
    fun provideForwardAttemptDao(db: SmsGaDatabase): ForwardAttemptDao = db.forwardAttemptDao()

    @Provides
    fun providePatternCacheDao(db: SmsGaDatabase): PatternCacheDao = db.patternCacheDao()
}
