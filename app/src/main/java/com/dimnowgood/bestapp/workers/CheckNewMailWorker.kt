package com.dimnowgood.bestapp.workers

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.dimnowgood.bestapp.R
import com.sun.mail.imap.IMAPFolder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import java.lang.Exception
import javax.inject.Inject
import javax.mail.Folder
import javax.mail.Session
import javax.mail.Store
import kotlin.coroutines.suspendCoroutine

class CheckNewMailWorker constructor(
    val session:Session,
    context: Context,
    workerParams: WorkerParameters
): CoroutineWorker(context, workerParams){

    override suspend fun doWork(): Result {

        return withContext(Dispatchers.IO){
            var store: Store? = null
            var inputFolder: IMAPFolder? = null

            try {
                store = session.getStore("imaps").also{it.connect()}
                inputFolder = store.getFolder("INBOX") as IMAPFolder

                val count = inputFolder.unreadMessageCount

                NotificationManagerCompat.from(applicationContext).also{
                    createNotificationChannel(it)
                    it.notify(NOTIFICATION_ID, createNotification(count))
                }

                Result.success()
            }
            catch (e:Exception){
                Result.failure()
            }
            finally {
                inputFolder?.close()
                store?.close()
            }
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