package com.bps.publikasistatistik.data.remote.interceptor

import com.bps.publikasistatistik.data.local.AuthPreferences
import com.bps.publikasistatistik.data.manager.SessionManager
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class TokenExpirationInterceptor @Inject constructor(
    private val sessionManager: SessionManager,
    private val authPreferences: AuthPreferences
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())

        // Cek apakah server menolak token (401 Unauthorized)
        if (response.code == 401) {
            runBlocking {
                // 1. Hapus token dari HP
                authPreferences.clearAuthToken()

                // 2. Beritahu aplikasi untuk Logout
                sessionManager.triggerLogout()
            }
        }

        return response
    }
}