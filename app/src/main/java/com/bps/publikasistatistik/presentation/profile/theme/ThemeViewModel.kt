package com.bps.publikasistatistik.presentation.profile.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bps.publikasistatistik.data.local.SettingPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val preferences: SettingPreferences
) : ViewModel() {

    // Mengubah Flow menjadi StateFlow agar bisa diobservasi oleh UI secara efisien
    val currentTheme: StateFlow<String> = preferences.getTheme()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "system"
        )

    fun setTheme(theme: String) {
        viewModelScope.launch {
            preferences.saveTheme(theme)
        }
    }
}