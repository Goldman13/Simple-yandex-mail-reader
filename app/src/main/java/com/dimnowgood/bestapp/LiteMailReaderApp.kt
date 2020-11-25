package com.dimnowgood.bestapp

import android.app.Activity
import android.app.Application
import android.net.ConnectivityManager
import android.net.NetworkRequest
import android.os.Bundle
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.dimnowgood.bestapp.util.NetworkStatus
import com.dimnowgood.bestapp.workers.AppWorkerFactories
import com.facebook.stetho.Stetho
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class LiteMailReaderApp: Application(), Configuration.Provider{

    @Inject
    lateinit var connectivityManager:ConnectivityManager
    @Inject
    lateinit var networkRequest: NetworkRequest
    @Inject
    lateinit var networkStatus: NetworkStatus
    @Inject
    lateinit var workerFactory: HiltWorkerFactory


    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            Stetho.initializeWithDefaults(this)
        }

        registerActivityLifecycleCallbacks(object:ActivityLifecycleCallbacks{
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            }

            override fun onActivityStarted(activity: Activity) {
                try{
                    connectivityManager.unregisterNetworkCallback(networkStatus)
                }catch(e:Exception){
                    connectivityManager.registerNetworkCallback(networkRequest,networkStatus)
                }
            }

            override fun onActivityResumed(activity: Activity) {

            }

            override fun onActivityPaused(activity: Activity) {

            }

            override fun onActivityStopped(activity: Activity) {
                connectivityManager.unregisterNetworkCallback(networkStatus)
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

            }

            override fun onActivityDestroyed(activity: Activity) {

            }

        })

    }

    override fun getWorkManagerConfiguration(): Configuration {
       return Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .setWorkerFactory(workerFactory)
            .build()
    }
}