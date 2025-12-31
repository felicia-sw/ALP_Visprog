package com.example.alp_visprog.repositories

import kotlinx.coroutines.flow.Flow
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.map

interface UserRepositoryInterface {
    val currentUserToken: Flow<String>
    val currentUsername: Flow<String>
    val currentUserEmail: Flow<String> // [1] Add this

    suspend fun saveUserToken(token: String)
    suspend fun saveUsername(username: String)
    suspend fun saveUserEmail(email: String) // [2] Add this
}

class UserRepository(
    private val userDataStore: DataStore<Preferences>
) : UserRepositoryInterface {

    override val currentUserToken: Flow<String> = userDataStore.data.map { preferences -> preferences[USER_TOKEN] ?: "Unknown" }
    override val currentUsername: Flow<String> = userDataStore.data.map { preferences -> preferences[USERNAME] ?: "Unknown" }

    // [3] Retrieve Email (Default to empty if unknown)
    override val currentUserEmail: Flow<String> = userDataStore.data.map { preferences -> preferences[USER_EMAIL] ?: "" }

    override suspend fun saveUserToken(token: String) {
        userDataStore.edit { preferences -> preferences[USER_TOKEN] = token }
    }

    override suspend fun saveUsername(username: String) {
        userDataStore.edit { preferences -> preferences[USERNAME] = username }
    }

    // [4] Save Email
    override suspend fun saveUserEmail(email: String) {
        userDataStore.edit { preferences -> preferences[USER_EMAIL] = email }
    }

    private companion object {
        val USER_TOKEN = stringPreferencesKey("token")
        val USERNAME = stringPreferencesKey("username")
        val USER_EMAIL = stringPreferencesKey("email") // [5] Key for Email
    }
}