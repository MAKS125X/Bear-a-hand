package com.example.simbirsoftmobile.data.network.interceptors.networkMonitor

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class NetworkMonitorInterceptor(
    private val liveNetworkMonitor: NetworkMonitor
) : Interceptor {
    @Throws(ConnectionException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()

        if (liveNetworkMonitor.isConnected()) {
            return chain.proceed(request)
        } else {
            throw ConnectionException("Network Error")
        }
    }
}
