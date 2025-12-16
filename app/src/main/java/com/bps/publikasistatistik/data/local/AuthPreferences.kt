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

// Setup DataStore (seperti SharedPreferences tapi modern)
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_prefs")

@Singleton
class AuthPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val TOKEN_KEY = stringPreferencesKey("jwt_token")

    private val KEY_USER_ROLE = stringPreferencesKey("user_role")

    // Simpan Role
    suspend fun saveUserRole(role: String) {
        context.dataStore.edit { it[KEY_USER_ROLE] = role }
    }

    // Ambil Role
    fun getUserRole(): Flow<String?> = context.dataStore.data.map { it[KEY_USER_ROLE] }

    // Ambil Token (Flow agar reaktif)
    fun getAuthToken(): Flow<String?> = context.dataStore.data.map { it[TOKEN_KEY] }


    // Simpan Token
    suspend fun saveAuthToken(token: String) {
        context.dataStore.edit { it[TOKEN_KEY] = token }
    }

    // Hapus Token (Logout)
    suspend fun clearAuthToken() {
        context.dataStore.edit { preferences ->
            preferences.remove(TOKEN_KEY)
            preferences.remove(KEY_USER_ROLE)
        }
    }
}