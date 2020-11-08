package com.dimnowgood.bestapp.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MailDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMails(mails: List<MailEntity>)

    @Query("SELECT * FROM MailEntity ORDER BY data_receiving DESC")
    fun queryAllMails(): LiveData<List<MailEntity>>
}