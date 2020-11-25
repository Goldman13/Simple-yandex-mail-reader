package com.dimnowgood.bestapp.data.db

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.TypeConverter
import com.dimnowgood.bestapp.domain.model.MailMessage
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
@Entity(primaryKeys = arrayOf("id", "login"))
data class MailEntity(
    val id: Long,
    val login:String,
    val title: String?,
    val from: String?,
    val data_receiving: Date,
    var newMail: Boolean
):Parcelable{
    @Ignore
    constructor(mail:MailMessage):this(
        id = mail.id,
        login = mail.login,
        title = mail.title,
        from = mail.from,
        data_receiving = mail.data_receiving,
        newMail = true
    )
}

object Converters {
    @TypeConverter
    @JvmStatic
    fun dateToLong(date: Date): Long = date.time

    @TypeConverter
    @JvmStatic
    fun longToDate(value: Long): Date = Date(value)
}

