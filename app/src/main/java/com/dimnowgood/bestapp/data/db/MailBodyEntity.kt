package com.dimnowgood.bestapp.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class MailBodyEntity(
    @PrimaryKey
    val id: Long,
    val content: String
)