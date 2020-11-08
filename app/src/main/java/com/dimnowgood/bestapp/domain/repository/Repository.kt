package com.dimnowgood.bestapp.domain.repository

import androidx.lifecycle.LiveData
import com.dimnowgood.bestapp.data.data_source.YandexMailServerDataSource
import com.dimnowgood.bestapp.data.db.MailBodyDao
import com.dimnowgood.bestapp.data.db.MailBodyEntity
import com.dimnowgood.bestapp.data.db.MailDao
import com.dimnowgood.bestapp.data.db.MailEntity
import com.dimnowgood.bestapp.util.Status
import javax.inject.Inject
import com.dimnowgood.bestapp.util.Result
import javax.inject.Singleton

@Singleton
class Repository @Inject constructor(
    private val remoteDataSource: YandexMailServerDataSource,
    private val mailDao: MailDao,
    private val mailBodyDao: MailBodyDao
) {
    suspend fun queryRemoteDataSource(): Result<*> {
        return remoteDataSource.queryMails { list ->
            if(!list.isNullOrEmpty()) addMailsLocalDb(list)
        }
    }

    suspend fun loadMailBody(id: Long):Result<*>{
        return remoteDataSource.loadMailBody(id){ mailBody ->
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

    fun queryAllMailsFromLocalDb(): LiveData<List<MailEntity>> {
        return mailDao.queryAllMails()
    }

    suspend fun queryBodyMailFromLocalDb(id: Long):MailBodyEntity?{
        return mailBodyDao.queryContent(id)
    }
}