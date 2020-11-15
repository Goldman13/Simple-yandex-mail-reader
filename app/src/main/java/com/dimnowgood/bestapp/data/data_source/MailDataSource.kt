package com.dimnowgood.bestapp.data.data_source

import com.dimnowgood.bestapp.data.db.MailBodyEntity
import com.dimnowgood.bestapp.data.db.MailEntity
import com.dimnowgood.bestapp.util.Result

interface MailDataSource {
   suspend fun queryMails(login:String, handleTitleMailData: suspend (List<MailEntity>) -> Unit): Result<*>
   suspend fun checkLoginData(param:List<String>): Result<*>
   suspend fun loadMailBody(id: Long,login:String, addMailBodyDb: suspend (MailBodyEntity)->Unit):Result<*>
}