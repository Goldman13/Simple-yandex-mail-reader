package com.dimnowgood.bestapp.workers

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.dimnowgood.bestapp.R
import com.dimnowgood.bestapp.ui.MainActivity
import com.dimnowgood.bestapp.util.COMMON_STORE
import com.dimnowgood.bestapp.util.LAST_TIME_CHECK
import com.sun.mail.imap.IMAPFolder
import java.time.LocalDate
import java.time.ZoneId
import javax.mail.Flags
import javax.mail.Folder
import javax.mail.Session
import javax.mail.Store
import javax.mail.search.FlagTerm

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
            val lastCheckTime = appCommonStore.getLong(
                LAST_TIME_CHECK,
                LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
            )
            store = session.getStore("imaps").also{it.connect()}
            inputFolder = store.getFolder("INBOX") as IMAPFolder
            inputFolder.open(Folder.READ_ONLY)

            inputFolder
                .search(FlagTerm(Flags(Flags.Flag.SEEN), false))
                .takeLastWhile {it.receivedDate.time >= lastCheckTime}
                .apply{
                    if(isNotEmpty()){
                        appCommonStore.edit().putLong(LAST_TIME_CHECK, last().receivedDate.time+1000).apply()
                        NotificationManagerCompat.from(applicationContext).also{
                            createNotificationChannel(it)
                            it.notify(NOTIFICATION_ID, createNotification(size))
                        }
                    }
                }

            return Result.success()
        }
        catch (e:Exception){
            return Result.failure()
        }
        finally {
            inputFolder?.close()
            store?.close()
        }
    }

    private fun createNotification(value: Int): Notification {

        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        return NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.notification_icon)
            .setContentTitle("Number of new emails $value")
            .setContentIntent(PendingIntent.getActivity(
                applicationContext,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            ))
            .setAutoCancel(true)
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