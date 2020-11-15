package com.dimnowgood.bestapp.domain.model

import java.time.LocalDateTime
import java.util.*

data class MailMessage(
    val id: Long,
    val login: String,
    val title: String,
    val from: String,
    val data_receiving: Date
)