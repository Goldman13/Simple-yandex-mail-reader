package com.dimnowgood.bestapp.data.data_source

import android.content.SharedPreferences
import android.content.res.Resources
import com.dimnowgood.bestapp.util.ErrorHandler
import com.dimnowgood.bestapp.LiteMailReaderApp
import com.dimnowgood.bestapp.R
import com.dimnowgood.bestapp.data.db.MailBodyEntity
import com.dimnowgood.bestapp.data.db.MailEntity
import com.dimnowgood.bestapp.domain.model.MailMessage
import com.dimnowgood.bestapp.util.Result
import com.sun.mail.imap.IMAPFolder
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton
import javax.mail.*
import javax.mail.internet.InternetAddress

@Singleton
class YandexMailServerDataSource @Inject constructor(
    private val session: Session,
    @Named("Settings")
    private val settingPref: SharedPreferences,
    @Named("Encrypt")
    private val loginPref: SharedPreferences,
    private val app: LiteMailReaderApp
): MailDataSource {

    override suspend fun queryMails(login: String,
        handleTitleMailData: suspend (List<MailEntity>) -> Unit
    ): Result<*> {

        var store: Store? = null
        var inputFolder: IMAPFolder? = null

        try{
            store = session.getStore("imaps")
            store.connect()
            inputFolder = store.getFolder("INBOX") as IMAPFolder
            inputFolder.open(Folder.READ_WRITE)

            if(inputFolder.messageCount == 0)
                return Result.success(null, app.getString(R.string.empty_mailbox))

            val checkVolume =
                settingPref.getString("NumberOfCheckMessages", "25")?.toInt()?:25

            var checkMessage = inputFolder.messageCount - checkVolume
            checkMessage =
                if(checkMessage <= 0)
                    1
                else
                    checkMessage+1

            val listMessages = inputFolder
                .getMessages(checkMessage, inputFolder.messageCount)
                .filter{ message -> !message.flags.contains(Flags.Flag.SEEN)&&!message.from.isNullOrEmpty()}

            if(!listMessages.isEmpty()) {
                val listLocal = listMessages.map {
                    MailEntity(
                        MailMessage(
                            inputFolder.getUID(it),
                            login,
                            it.subject,
                            (it.from[0] as InternetAddress).address,
                            it.receivedDate
                        )
                    )
                }

                handleTitleMailData(listLocal)
                listMessages.forEach { it.setFlag(Flags.Flag.SEEN, true) }
            }
            return Result.success(null, "")
        }
        catch (e:Exception){
            e.printStackTrace()
            return Result.error(null, ErrorHandler.handle(e,app)?:e.toString())
        }
        finally {
            inputFolder?.close()
            store?.close()
        }
    }

    override suspend fun checkLoginData(param: List<String>): Result<*> {

        var store: Store? = null

        return try {
            store = session.getStore("imaps")
            store.connect(param[0], param[1])
            Result.success(null, "")
        }catch(e : Exception){
            Result.error(null, ErrorHandler.handle(e, app)?:e.toString())
        }finally {
            store?.close()
        }
    }

    override suspend fun loadMailBody(
        id: Long,
        login:String,
        addMailBodyDb: suspend (MailBodyEntity)->Unit
    ): Result<*> {

        var store: Store? = null
        var inputFolder: IMAPFolder? = null

        try {
            store = session.getStore("imaps")
            store.connect()
            inputFolder = store.getFolder("INBOX") as IMAPFolder
            inputFolder.open(Folder.READ_WRITE)

            inputFolder.getMessageByUID(id)?.let {message ->
                addMailBodyDb(MailBodyEntity(id,login,handleMessageBody(message)))
            }?: throw java.lang.Exception(app.getString(R.string.messages_miss))

            return Result.success(null, "")
        }
        catch (e:Exception){
            e.printStackTrace()
            return Result.error(ErrorHandler.handle(e, app)?:e.toString(),"")
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