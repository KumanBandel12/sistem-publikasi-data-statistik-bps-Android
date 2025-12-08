package com.bps.publikasistatistik.data.manager

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import com.bps.publikasistatistik.data.local.AuthPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.io.File
import javax.inject.Inject

class AndroidDownloader @Inject constructor(
    @ApplicationContext private val context: Context,
    private val authPreferences: AuthPreferences
) {
    // Return String path file yang akan disimpan
    fun downloadFile(url: String, fileName: String): String? {
        return try {
            val token = runBlocking { authPreferences.getAuthToken().first() }

            // Tentukan lokasi file
            val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName)

            val request = DownloadManager.Request(Uri.parse(url))
                .setMimeType("application/pdf")
                .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setTitle(fileName)
                // Simpan ke folder khusus aplikasi agar mudah diakses tanpa izin storage rumit
                .setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, fileName)

            if (!token.isNullOrEmpty()) {
                request.addRequestHeader("Authorization", "Bearer $token")
            }

            val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            downloadManager.enqueue(request)

            Toast.makeText(context, "Mulai mengunduh...", Toast.LENGTH_SHORT).show()

            // Kembalikan absolute path agar bisa disimpan di DB
            file.absolutePath

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Gagal download: ${e.message}", Toast.LENGTH_SHORT).show()
            null
        }
    }
}