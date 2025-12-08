package com.bps.publikasistatistik.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

// Kita buat file preferensi baru khusus settings
private val Context.dataStoreSettings: DataStore<Preferences> by preferencesDataStore(name = "settings_prefs")

@Singleton
class SettingPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    // Key untuk tema: values bisa "light", "dark", "system"
    private val THEME_KEY = stringPreferencesKey("app_theme")

    fun getTheme(): Flow<String> {
        return context.dataStoreSettings.data.map { preferences ->
            preferences[THEME_KEY] ?: "system" // Default "system"
        }
    }

    suspend fun saveTheme(theme: String) {
        context.dataStoreSettings.edit { preferences ->
            preferences[THEME_KEY] = theme
        }
    }
}