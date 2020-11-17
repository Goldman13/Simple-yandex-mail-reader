package com.dimnowgood.bestapp.util

import com.dimnowgood.bestapp.LiteMailReaderApp
import com.dimnowgood.bestapp.R
import javax.mail.AuthenticationFailedException

class ErrorHandler {
    companion object {
        fun handle(e: Exception, app:LiteMailReaderApp): String? {
            return when (e) {
                is AuthenticationFailedException ->
                    app.getString(R.string.login_failed)
                else -> e.message
            }
        }
    }
}