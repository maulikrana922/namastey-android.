package com.namastey.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log

class InternetConnectionMonitor(val context: Context) : ConnectivityManager.NetworkCallback() {

    //Can define internet connection state with this class.

    private var networkRequest: NetworkRequest =
        NetworkRequest.Builder().addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI).build()

    fun enable() {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.registerNetworkCallback(networkRequest, this)
    }

//    override fun onAvailable(network: Network?) {
//        super.onAvailable(network)
//        Log.w("InternetMonitor", "onAvailable")
//    }
//
//    override fun onLost(network: Network?) {
//        super.onLost(network)
//        Log.w("InternetMonitor", "onLost")
//    }

    override fun onUnavailable() {
        super.onUnavailable()
        Log.w("InternetMonitor", "onUnavailable")
    }

}