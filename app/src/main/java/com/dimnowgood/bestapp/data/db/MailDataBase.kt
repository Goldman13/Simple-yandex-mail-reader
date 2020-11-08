package com.dimnowgood.bestapp.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [MailEntity::class, MailBodyEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class MailDataBase: RoomDatabase() {
    abstract fun mailDao(): MailDao
    abstract fun mailBodyDao(): MailBodyDao
}