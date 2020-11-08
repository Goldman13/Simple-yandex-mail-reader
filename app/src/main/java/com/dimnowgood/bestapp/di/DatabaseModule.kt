package com.dimnowgood.bestapp.di

import androidx.room.Room
import com.dimnowgood.bestapp.LiteMailReader
import com.dimnowgood.bestapp.data.db.MailBodyDao
import com.dimnowgood.bestapp.data.db.MailDao
import com.dimnowgood.bestapp.data.db.MailDataBase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(app: LiteMailReader): MailDataBase {
        return Room.databaseBuilder(
            app.applicationContext,
            MailDataBase::class.java,
            "MailDataBase-DB"
        ).build()
    }

    @Singleton
    @Provides
    fun provideMailDao(db: MailDataBase): MailDao{
        return db.mailDao()
    }

    @Singleton
    @Provides
    fun provideMailBodyDao(db: MailDataBase): MailBodyDao{
        return db.mailBodyDao()
    }
}