package com.example.imaginarium.download

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.system.Os.link
import androidx.core.net.toUri


class AndroidDownloader(
    private val context: Context
):Downloader {

    val downloadManager: DownloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    override fun downLoadFile(url: String): Long {

        val request = DownloadManager.Request(url.toUri())
            .setMimeType("image/jpeg")
            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setTitle("image.jpeg")
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "image.jpg")
        return downloadManager.enqueue(request)

    }
}
