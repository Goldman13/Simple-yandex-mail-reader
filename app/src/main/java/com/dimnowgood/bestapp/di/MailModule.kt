package com.dimnowgood.bestapp.di

import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.preference.PreferenceManager
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.dimnowgood.bestapp.LiteMailReaderApp
import com.dimnowgood.bestapp.util.LOGIN
import com.dimnowgood.bestapp.util.PASSWORD
import dagger.Module
import dagger.Provides
import java.util.Properties
import javax.inject.Named
import javax.inject.Singleton
import javax.mail.Authenticator
import javax.mail.PasswordAuthentication
import javax.mail.Session

@Module
class MailModule {

    @Singleton
    @Provides
    fun provide_ConnectivityManager(app:LiteMailReaderApp): ConnectivityManager{
        return app.applicationContext
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    @Singleton
    @Provides
    fun provide_NetworkRequest(): NetworkRequest{
        return NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .build()
    }

    @Singleton
    @Provides
    @Named("Encrypt")
    fun provide_encryptSharedPref(app: LiteMailReaderApp): SharedPreferences{
        return EncryptedSharedPreferences.create(
            app,
            "auth_pref",
            MasterKey.Builder(app.applicationContext).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build(),
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    @Singleton
    @Provides
    @Named("Settings")
    fun provide_settingSharedPref(app: LiteMailReaderApp): SharedPreferences{
       return PreferenceManager.getDefaultSharedPreferences(app.applicationContext)
    }

    @Singleton
    @Provides
    fun provide_YandexMailSession(@Named("Encrypt") sharPref: SharedPreferences): Session {

        val props = Properties().apply {
            setProperty("mail.imaps.host", "imap.yandex.ru")
            setProperty("mail.imaps.port", "993")
            setProperty("mail.imaps.ssl.enable", "true")
        }

        return Session.getDefaultInstance(props, getAuthenticator(sharPref))
    }

    private fun getAuthenticator(sharPref: SharedPreferences): Authenticator{
        return object:Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(
                    sharPref.getString(LOGIN,""),
                    sharPref.getString(PASSWORD,"")
                )
            }
        }
    }
}