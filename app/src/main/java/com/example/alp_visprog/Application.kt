package com.example.alp_visprog

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

private val Context.datastore: DataStore<Preferences> by preferencesDataStore(
    name = "user_data"
)

class App : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        // Backend URL for API calls (use 10.0.2.2 for Android emulator to access localhost)
        val backendURL = "http://10.0.2.2:3000/"
        container = AppContainer(datastore, backendURL)
    }
}