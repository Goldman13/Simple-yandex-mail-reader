package com.dimnowgood.bestapp.domain.usecase

import com.dimnowgood.bestapp.domain.repository.Repository
import com.dimnowgood.bestapp.util.Result
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CheckLoginData @Inject constructor(val repo: Repository) {
    suspend fun checkLog(param: List<String>): Result<*> {
        return repo.checkLoginData(param)
    }
}