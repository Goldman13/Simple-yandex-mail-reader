package com.dimnowgood.bestapp.util

import android.app.Application
import com.dimnowgood.bestapp.LiteMailReaderApp
import com.dimnowgood.bestapp.R
import javax.mail.AuthenticationFailedException

class ErrorHandler {
    companion object {
        fun handle(e: Exception, app:Application): String? {
            return when (e) {
                is AuthenticationFailedException ->
                    app.getString(R.string.login_failed)
                else -> e.message
            }
        }
    }
}