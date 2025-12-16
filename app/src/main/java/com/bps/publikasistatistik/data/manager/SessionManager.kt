package com.bps.publikasistatistik.data.manager

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor() {
    // SharedFlow untuk memancarkan event "Logout" ke seluruh aplikasi
    private val _logoutEvent = MutableSharedFlow<Boolean>()
    val logoutEvent = _logoutEvent.asSharedFlow()

    suspend fun triggerLogout() {
        _logoutEvent.emit(true)
    }
}