package com.dimnowgood.bestapp.data.db

import androidx.room.Entity
import androidx.room.Ignore

@Entity(primaryKeys = arrayOf("id", "login"))
data class MailBodyEntity(
    val id: Long,
    val login: String,
    val content: String){
    @Ignore
    constructor(mailEntity: MailEntity):this(
        id = mailEntity.id,
        login = mailEntity.login,
        content = ""
    )
}