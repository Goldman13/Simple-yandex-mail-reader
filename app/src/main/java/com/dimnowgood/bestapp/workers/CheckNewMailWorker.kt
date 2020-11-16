package com.dimnowgood.bestapp.workers

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.icu.util.TimeZone
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.dimnowgood.bestapp.R
import com.dimnowgood.bestapp.util.COMMON_STORE
import com.dimnowgood.bestapp.util.LAST_TIME_CHECK
import com.sun.mail.imap.IMAPFolder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import java.lang.Exception
import java.sql.Date
import java.time.*
import java.time.temporal.ChronoField
import java.util.*
import javax.inject.Inject
import javax.mail.Flags
import javax.mail.Folder
import javax.mail.Session
import javax.mail.Store
import javax.mail.search.AndTerm
import javax.mail.search.ComparisonTerm
import javax.mail.search.FlagTerm
import javax.mail.search.ReceivedDateTerm
import java.util.TimeZone.*
import javax.inject.Singleton

class CheckNewMailWorker constructor(
    val session:Session,
    context: Context,
    workerParams: WorkerParameters
): CoroutineWorker(context, workerParams){

    override suspend fun doWork(): Result {
        val appCommonStore = applicationContext.getSharedPreferences(COMMON_STORE,Context.MODE_PRIVATE)
        var store: Store? = null
        var inputFolder: IMAPFolder? = null

        try {
            store = session.getStore("imaps").also{it.connect()}
            inputFolder = store.getFolder("INBOX") as IMAPFolder
            inputFolder.open(Folder.READ_ONLY)

            val currentVolumeNewMessages = inputFolder.search(AndTerm(
                FlagTerm(Flags(Flags.Flag.SEEN), false),
                ReceivedDateTerm(
                    ComparisonTerm.GE,
                    Date(appCommonStore.getLong(
                        LAST_TIME_CHECK,
                        LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())))
            )).size

            if( currentVolumeNewMessages>0){
                NotificationManagerCompat.from(applicationContext).also{
                    createNotificationChannel(it)
                    it.notify(NOTIFICATION_ID, createNotification(currentVolumeNewMessages))
                }
            }

            return Result.success()
        }
        catch (e:Exception){
            return Result.failure()
        }
        finally {
            appCommonStore.edit().putLong(
                LAST_TIME_CHECK,
                LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()).apply()
            inputFolder?.close()
            store?.close()
        }
    }

    private fun createNotification(value: Int): Notification {
        return NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.notification_icon)
            .setContentTitle("Number of new emails $value")
            .build()
    }

    @SuppressLint("WrongConstant")
    private fun createNotificationChannel(notificationManager: NotificationManagerCompat){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManagerCompat.IMPORTANCE_HIGH
            )

            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object{
        const val CHANNEL_NAME = "MailChannel"
        const val CHANNEL_ID = "1"
        const val NOTIFICATION_ID = 555
    }
}