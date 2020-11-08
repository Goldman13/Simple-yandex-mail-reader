package com.dimnowgood.bestapp.data.data_source

import android.util.Log
import com.dimnowgood.bestapp.MailDataSource
import com.dimnowgood.bestapp.data.db.MailBodyEntity
import com.dimnowgood.bestapp.data.db.MailEntity
import com.dimnowgood.bestapp.domain.model.MailMessage
import com.dimnowgood.bestapp.util.Result
import com.sun.mail.imap.IMAPFolder
import kotlinx.coroutines.*
import timber.log.Timber
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton
import javax.mail.*
import javax.mail.internet.InternetAddress
import kotlin.coroutines.coroutineContext

@Singleton
class YandexMailServerDataSource @Inject constructor(
    private val session: Session
): MailDataSource {

    override suspend fun queryMails(
        handleTitleMailData: suspend (List<MailEntity>) -> Unit
    ): Result<*> {

        Timber.tag("Timber").d("start")
        var store: Store? = null
        var inputFolder: IMAPFolder? = null

        try {
            store = session.getStore("imaps")
            store.connect()
            inputFolder = store.getFolder("INBOX") as IMAPFolder
            inputFolder.open(Folder.READ_ONLY)

            val listMessages = inputFolder
                .getMessages(inputFolder.messageCount - 50, inputFolder.messageCount)
                .filter { message -> message.flags.systemFlags[0] != Flags.Flag.SEEN }
                .map{MailEntity(MailMessage(
                    inputFolder.getUID(it),
                    it.subject,
                    (it.from[0] as InternetAddress).address,
                    it.receivedDate))}

            handleTitleMailData(listMessages)
            return Result.success(null)
        }
        catch (e:Exception){
            e.printStackTrace()
            return Result.error(e.toString()?:"ERROR!!!", null)
        } finally {
            inputFolder?.close()
            store?.close()
        }
    }

    override suspend fun checkLoginData(param: List<String>): Result<*> {

        var store: Store? = null

        return try {
            store = session.getStore("imaps")
            store.connect(param[0], param[1])
            Result.success(null)
        }catch(e : Exception){
            Result.error(e.message?:"Error", null)
        }finally {
            store?.close()
        }
    }

    override suspend fun loadMailBody(
        id: Long,
        addMailBodyDb: suspend (MailBodyEntity)->Unit
    ): Result<String> {

        var store: Store? = null
        var inputFolder: IMAPFolder? = null

        try {
            store = session.getStore("imaps")
            store.connect()
            inputFolder = store.getFolder("INBOX") as IMAPFolder
            inputFolder.open(Folder.READ_ONLY)

            inputFolder.getMessageByUID(id)?.let {message ->
                addMailBodyDb(MailBodyEntity(id, handleMessageBody(message)))
            }?: throw java.lang.Exception("The message is missing on the server")

            return Result.success(null)
        }
        catch (e:Exception){
            e.printStackTrace()
            return Result.error(e.message?:"ERROR!!!", null)
        } finally {
            inputFolder?.close()
            store?.close()
        }

    }

    private fun handleMessageBody(mes: Part): String{

        try {
            if (mes.isMimeType("text/*")) {
                return mes.getContent().toString()
            }

            if (mes.isMimeType("multipart/alternative")) {
                // prefer html text over plain text
                val mp = mes.getContent() as Multipart
                var text = ""

                for (i in 0 until mp.getCount()) {
                    val bp = mp.getBodyPart(i)
                    if (bp.isMimeType("text/plain")) {
                        if (text == "")
                            text = handleMessageBody(bp)
                        continue
                    } else if (bp.isMimeType("text/html")) {
                        val s = handleMessageBody(bp)
                        return s
                    } else {
                        return handleMessageBody(bp);
                    }
                }
                return text
            } else if (mes.isMimeType("multipart/*")) {
                val mp = mes.getContent() as Multipart

                for (i in 0 until mp.getCount()) {
                    val s = handleMessageBody(mp.getBodyPart(i))
                    return s
                }
            }
        } catch(e:Exception){
            e.printStackTrace()
        }

        return ""
    }
}