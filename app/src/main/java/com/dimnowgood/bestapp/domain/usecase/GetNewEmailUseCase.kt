package com.dimnowgood.bestapp.domain.usecase

import com.dimnowgood.bestapp.domain.repository.Repository
import com.dimnowgood.bestapp.util.Result
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetNewEmailUseCase @Inject constructor(val repo: Repository){
    fun query(login:String): Result<*> = repo.queryRemoteDataSource(login)
    fun queryMails(login:String) = repo.queryAllMailsFromLocalDb(login)
}