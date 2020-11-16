package com.dimnowgood.bestapp.data.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface MailDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMails(mails: List<MailEntity>)

    @Query("SELECT * FROM MailEntity WHERE login=:login ORDER BY data_receiving DESC")
    fun queryAllMails(login:String): LiveData<List<MailEntity>>

    @Update
    fun update(mainEntity: MailEntity)

    @Transaction
    fun complexDelete(list: List<MailEntity>){
        deleteMailEntity(list)
        deleteBodyMailEntity(list.map { it.id }, list[0].login)
    }
    @Delete
    fun deleteMailEntity(list: List<MailEntity>)

    @Query("DELETE FROM MailBodyEntity WHERE id IN(:list1) AND login=:login")
    fun deleteBodyMailEntity(list1: List<Long>, login: String)
}