package com.dimnowgood.bestapp.util

import android.net.ConnectivityManager
import android.net.Network
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkStatus @Inject constructor():ConnectivityManager.NetworkCallback() {

    var isConnectNetwork = false

    override fun onAvailable(network: Network) {
        isConnectNetwork = true
    }

    override fun onLost(network: Network) {
        isConnectNetwork = false
    }

}