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
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Properties
import javax.inject.Named
import javax.inject.Qualifier
import javax.inject.Singleton
import javax.mail.Authenticator
import javax.mail.PasswordAuthentication
import javax.mail.Session

@Module
@InstallIn(ApplicationComponent::class)
object MailModule {

    @Qualifier
    annotation class EncryptSharedPref

    @Qualifier
    annotation class SettingsSharedPref


    @Provides
    fun provide_ConnectivityManager(@ApplicationContext appContext:Context): ConnectivityManager{
        return appContext
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    @Provides
    fun provide_NetworkRequest(): NetworkRequest{
        return NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .build()
    }

    @Provides
    @EncryptSharedPref
    fun provide_encryptSharedPref(@ApplicationContext appContext:Context): SharedPreferences{
        return EncryptedSharedPreferences.create(
            appContext,
            "auth_pref",
            MasterKey.Builder(appContext).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build(),
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }


    @Provides
    @SettingsSharedPref
    fun provide_settingSharedPref(@ApplicationContext appContext:Context): SharedPreferences{
       return PreferenceManager.getDefaultSharedPreferences(appContext)
    }


    @Provides
    fun provide_YandexMailSession(@EncryptSharedPref sharPref: SharedPreferences): Session {

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