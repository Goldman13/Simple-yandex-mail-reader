package com.dimnowgood.bestapp.domain.usecase

import com.dimnowgood.bestapp.domain.repository.Repository
import com.dimnowgood.bestapp.util.Result
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetMailBodyUseCase@Inject constructor(val repo: Repository) {
    fun queryBodyDb(id: Long, login:String) = repo.queryBodyMailFromLocalDb(id, login)
    fun loadMailBody(id: Long, login:String): Result<*> = repo.loadMailBody(id, login)
}