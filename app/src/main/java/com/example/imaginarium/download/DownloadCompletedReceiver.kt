package com.example.imaginarium.download

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent


class DownloadCompletedReceiver() : BroadcastReceiver() {

    private var callback: DownloadInterface? = null
    var savedUri: String? = null
    fun registerReceiver(receiver: DownloadInterface?) {
        this.callback = receiver
    }

    interface DownloadInterface {
        fun updateDownloaded() {}
    }

    override fun onReceive(context: Context?, intent: Intent?) {

        if (intent?.action == "android.intent.action.DOWNLOAD_COMPLETE") {
            val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L)

            if (id != -1L) {
                val downloadManager = AndroidDownloader(context!!).downloadManager
                val uri = downloadManager.getUriForDownloadedFile(id)
                savedUri = uri.toString()
                callback?.updateDownloaded()
            }
        }
    }

}
