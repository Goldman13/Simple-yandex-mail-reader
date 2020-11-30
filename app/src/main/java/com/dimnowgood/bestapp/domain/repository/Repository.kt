package com.dimnowgood.bestapp.domain.repository

import androidx.lifecycle.LiveData
import com.dimnowgood.bestapp.data.data_source.YandexMailServerDataSource
import com.dimnowgood.bestapp.data.db.MailBodyDao
import com.dimnowgood.bestapp.data.db.MailBodyEntity
import com.dimnowgood.bestapp.data.db.MailDao
import com.dimnowgood.bestapp.data.db.MailEntity
import com.dimnowgood.bestapp.util.Result
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Repository @Inject constructor(
    private val remoteDataSource: YandexMailServerDataSource,
    private val mailDao: MailDao,
    private val mailBodyDao: MailBodyDao){

   suspend fun updateLocalDb(mailItem: MailEntity){
        mailDao.update(mailItem)
   }

   suspend fun deleteLocalDb(list: List<MailEntity>){
        mailDao.complexDelete(list)
   }

    suspend fun queryRemoteDataSource(login:String): Result<*> {
        return remoteDataSource.queryMails(login) { list ->
            if(!list.isNullOrEmpty()) addMailsLocalDb(list)
        }
    }

    suspend fun loadMailBody(id: Long, login:String):Result<*>{
        return remoteDataSource.loadMailBody(id,login){ mailBody ->
            addMailBodyLocalDb(mailBody)
        }
    }

    suspend fun checkLoginData(param: List<String>): Result<*> {
        return remoteDataSource.checkLoginData(param)
    }

    private suspend fun addMailsLocalDb(newMailList: List<MailEntity>) {
        mailDao.insertMails(newMailList)
    }

    private suspend fun addMailBodyLocalDb(body: MailBodyEntity) {
        mailBodyDao.insert(body)
    }

    fun queryAllMailsFromLocalDb(login:String): LiveData<List<MailEntity>> {
        return mailDao.queryAllMails(login)
    }

    suspend fun queryBodyMailFromLocalDb(id: Long, login:String):MailBodyEntity?{
        return mailBodyDao.queryContent(id, login)
    }
}