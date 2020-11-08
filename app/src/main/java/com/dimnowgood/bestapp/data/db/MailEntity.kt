package com.dimnowgood.bestapp.data.db

import android.os.Parcelable
import androidx.room.*
import com.dimnowgood.bestapp.domain.model.MailMessage
import kotlinx.android.parcel.Parcelize
import java.time.LocalDateTime
import java.util.*

@Parcelize
@Entity
data class MailEntity(
    @PrimaryKey val id: Long,
    val title: String?,
    val from: String?,
    val data_receiving: Date
):Parcelable{
    @Ignore
    constructor(mail:MailMessage):this(
        id = mail.id,
        title = mail.title,
        from = mail.from,
        data_receiving = mail.data_receiving
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

