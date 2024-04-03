package com.example.simbirsoftmobile.presentation.screens.help

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.simbirsoftmobile.presentation.models.category.Category
import com.example.simbirsoftmobile.presentation.screens.help.thread.Downloader

class DownloadCategoriesService : Service() {
    private val downloader: Downloader = Downloader(this) { sendResultToActivity(it) }

    override fun onBind(intent: Intent): IBinder? = null

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int,
    ): Int {
        downloader.load()

        return START_REDELIVER_INTENT
    }

    private fun sendResultToActivity(list: List<Category>) {
        val intent = Intent(DOWNLOAD_ACTION).putParcelableArrayListExtra(LIST_KEY, ArrayList(list))
        val localBroadcastManager = LocalBroadcastManager.getInstance(this)
        localBroadcastManager.sendBroadcast(intent)
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()

        downloader.stop()
    }

    companion object {
        const val DOWNLOAD_ACTION = "categoryListAction"
        const val LIST_KEY = "categoryListServiceKey"
    }
}
