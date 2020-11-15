package com.dimnowgood.bestapp.data.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface MailBodyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(body: MailBodyEntity)

    @Query("SELECT * FROM MailBodyEntity WHERE id = :id and login=:login")
    suspend fun queryContent(id: Long, login:String): MailBodyEntity

}