package com.dimnowgood.bestapp.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.dimnowgood.bestapp.LiteMailReaderApp
import com.dimnowgood.bestapp.data.db.MailBodyDao
import com.dimnowgood.bestapp.data.db.MailDao
import com.dimnowgood.bestapp.data.db.MailDataBase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object DatabaseModule {

    @Provides
    fun provideDatabase(@ApplicationContext appContext: Context): MailDataBase {
        return Room.databaseBuilder(
            appContext,
            MailDataBase::class.java,
            "MailDataBase-DB"
        ).build()
    }

    @Provides
    fun provideMailDao(db: MailDataBase): MailDao{
        return db.mailDao()
    }

    @Provides
    fun provideMailBodyDao(db: MailDataBase): MailBodyDao{
        return db.mailBodyDao()
    }
}