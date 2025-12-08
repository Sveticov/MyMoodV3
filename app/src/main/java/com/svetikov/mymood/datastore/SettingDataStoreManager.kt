package com.svetikov.mymood.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SettingDataStoreManager @Inject constructor(@ApplicationContext private val context: Context) {

    private val Context.dataStore: DataStore<Preferences>
    by preferencesDataStore(name = "notification_settings")

    private object PreferencesKeys{
        val INTERVAL_HOURS = intPreferencesKey("notification_interval_hours")
    }

    suspend fun saveInterval(hours:Int){
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.INTERVAL_HOURS]=hours
        }
    }
}