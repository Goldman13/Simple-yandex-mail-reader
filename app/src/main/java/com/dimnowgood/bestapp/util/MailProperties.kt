package com.dimnowgood.bestapp.util

import java.util.*

class MailProperties {
    companion object {
        fun yandexMailProp(): Properties {
            return Properties().apply {
                setProperty("mail.imap.host", "imap.yandex.ru")
                setProperty("mail.imap.port", "993")
                //setProperty("mail.imap.ssl.enable", "true")
            }
        }
    }
}