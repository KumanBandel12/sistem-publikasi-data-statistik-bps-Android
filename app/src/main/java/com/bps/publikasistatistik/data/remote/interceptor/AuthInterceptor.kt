package com.bps.publikasistatistik.data.remote.interceptor

import com.bps.publikasistatistik.data.local.AuthPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val authPreferences: AuthPreferences
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        // Ambil token secara blocking (karena Interceptor bekerja synchronous)
        val token = runBlocking {
            authPreferences.getAuthToken().first()
        }

        val requestBuilder = chain.request().newBuilder()

        // Jika token ada, tempelkan ke Header
        if (!token.isNullOrEmpty()) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }

        return chain.proceed(requestBuilder.build())
    }
}