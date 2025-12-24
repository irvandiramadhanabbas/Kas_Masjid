package com.example.frontend.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "session")

class SessionDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val KEY_TOKEN = stringPreferencesKey("token")
        private val KEY_ROLE = stringPreferencesKey("role")
        private val KEY_EMAIL = stringPreferencesKey("email")
        private val KEY_USERNAME = stringPreferencesKey("username")
        private val KEY_USER_ID = stringPreferencesKey("user_id")
        private val KEY_STATUS = stringPreferencesKey("status") // ✅ baru
    }

    val tokenFlow: Flow<String?> = context.dataStore.data.map { it[KEY_TOKEN] }
    val roleFlow: Flow<String?> = context.dataStore.data.map { it[KEY_ROLE] }
    val usernameFlow: Flow<String?> = context.dataStore.data.map { it[KEY_USERNAME] }
    val emailFlow: Flow<String?> = context.dataStore.data.map { it[KEY_EMAIL] }
    val userIdFlow: Flow<Int?> = context.dataStore.data.map { it[KEY_USER_ID]?.toIntOrNull() }
    val statusFlow: Flow<String?> = context.dataStore.data.map { it[KEY_STATUS] } // ✅ baru

    suspend fun saveSession(
        token: String,
        role: String,
        email: String,
        username: String,
        userId: Int,
        status: String
    ) {
        context.dataStore.edit {
            it[KEY_TOKEN] = token
            it[KEY_ROLE] = role
            it[KEY_EMAIL] = email
            it[KEY_USERNAME] = username
            it[KEY_USER_ID] = userId.toString()
            it[KEY_STATUS] = status // ✅ baru
        }
    }

    suspend fun clearSession() {
        android.util.Log.e("SESSION", "clearSession() CALLED", Throwable())
        context.dataStore.edit { it.clear() }
    }


    suspend fun getUsernameOnce(): String? = usernameFlow.first()
    suspend fun getRoleOnce(): String? = roleFlow.first()
    suspend fun getUserIdOnce(): Int? = userIdFlow.first()
    suspend fun getStatusOnce(): String? = statusFlow.first()
}
