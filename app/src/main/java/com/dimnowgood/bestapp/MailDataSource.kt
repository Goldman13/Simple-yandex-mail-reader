package com.dimnowgood.bestapp

import com.dimnowgood.bestapp.data.db.MailBodyEntity
import com.dimnowgood.bestapp.data.db.MailEntity
import com.dimnowgood.bestapp.util.Result

interface MailDataSource {
   suspend fun queryMails(handleTitleMailData: suspend (List<MailEntity>) -> Unit): Result<*>
   suspend fun checkLoginData(param:List<String>): Result<*>
   suspend fun loadMailBody(id: Long, addMailBodyDb: suspend (MailBodyEntity)->Unit):Result<String>
}