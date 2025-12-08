package com.bps.publikasistatistik.presentation.auth.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bps.publikasistatistik.data.local.AuthPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val preferences: AuthPreferences
) : ViewModel() {

    private val _isUserLoggedIn = MutableStateFlow<Boolean?>(null) // null = loading/checking
    val isUserLoggedIn: StateFlow<Boolean?> = _isUserLoggedIn

    init {
        checkLoginStatus()
    }

    private fun checkLoginStatus() {
        viewModelScope.launch {
            // Beri sedikit delay agar logo splash screen sempat terlihat (estetika)
            delay(2000)

            preferences.getAuthToken().collect { token ->
                _isUserLoggedIn.value = !token.isNullOrEmpty()
            }
        }
    }
}