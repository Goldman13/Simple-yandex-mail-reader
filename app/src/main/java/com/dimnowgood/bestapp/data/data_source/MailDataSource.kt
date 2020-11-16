package com.dimnowgood.bestapp.data.data_source

import com.dimnowgood.bestapp.data.db.MailBodyEntity
import com.dimnowgood.bestapp.data.db.MailEntity
import com.dimnowgood.bestapp.util.Result

interface MailDataSource {
   fun queryMails(login:String, handleTitleMailData: (List<MailEntity>) -> Unit): Result<*>
   fun checkLoginData(param:List<String>): Result<*>
   fun loadMailBody(id: Long,login:String, addMailBodyDb: (MailBodyEntity)->Unit):Result<*>
}