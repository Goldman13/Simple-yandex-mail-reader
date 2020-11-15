package com.dimnowgood.bestapp.domain.usecase

import com.dimnowgood.bestapp.data.db.MailEntity
import com.dimnowgood.bestapp.domain.repository.Repository
import com.dimnowgood.bestapp.util.Result
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ModifyMailItemDbUseCase @Inject constructor(val repo: Repository) {
    suspend fun updateDb(mailItem: MailEntity) = repo.updateLocalDb(mailItem)
}