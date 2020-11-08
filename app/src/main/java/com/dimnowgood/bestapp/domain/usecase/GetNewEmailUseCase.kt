package com.dimnowgood.bestapp.domain.usecase

import com.dimnowgood.bestapp.domain.repository.Repository
import com.dimnowgood.bestapp.util.Result
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetNewEmailUseCase @Inject constructor(val repo: Repository){

    suspend fun query(): Result<*> = repo.queryRemoteDataSource()
    fun queryMails() = repo.queryAllMailsFromLocalDb()
    suspend fun queryBody(id: Long) = repo.queryBodyMailFromLocalDb(id)
    suspend fun loadMailBody(id: Long): Result<*> = repo.loadMailBody(id)


}