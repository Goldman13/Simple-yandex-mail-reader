package com.dimnowgood.bestapp.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MailBodyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(body: MailBodyEntity)

    @Query("SELECT * FROM MailBodyEntity WHERE id = :id and login=:login")
    suspend fun queryContent(id: Long, login:String): MailBodyEntity

}