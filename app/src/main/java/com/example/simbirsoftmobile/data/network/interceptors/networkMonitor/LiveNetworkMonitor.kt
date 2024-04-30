package com.example.simbirsoftmobile.data.network.interceptors.networkMonitor

import android.content.Context
import android.net.ConnectivityManager

class LiveNetworkMonitor(
    context: Context,
) : NetworkMonitor {
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    override fun isConnected(): Boolean {
        val network = connectivityManager.activeNetwork
        return network != null
    }
}
