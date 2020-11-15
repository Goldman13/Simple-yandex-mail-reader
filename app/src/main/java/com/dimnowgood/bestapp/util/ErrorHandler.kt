package com.dimnowgood.bestapp.util

import android.content.res.Resources
import com.dimnowgood.bestapp.LiteMailReaderApp
import com.dimnowgood.bestapp.R
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Singleton
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