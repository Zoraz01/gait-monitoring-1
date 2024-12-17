package com.gaitmonitoring.datastores

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

private val Context.gaitDatastore: DataStore<Preferences> by preferencesDataStore(name = "gait_data_store")

class GaitDatastore(private val context: Context) {

    private val idKey = intPreferencesKey("id_key")
    private val gyroXKey = floatPreferencesKey("gyro_x_key")
    private val gyroYKey = floatPreferencesKey("gyro_y_key")
    private val gyroZKey = floatPreferencesKey("gyro_z_key")
    private val timestampKey = longPreferencesKey("timestamp_key")

    val gaitId = context.gaitDatastore.data.map { prefs -> prefs[idKey] ?: 0 }
    val gyroX = context.gaitDatastore.data.map { prefs -> prefs[gyroXKey] ?: 0f }
    val gyroY = context.gaitDatastore.data.map { prefs -> prefs[gyroYKey] ?: 0f }
    val gyroZ = context.gaitDatastore.data.map { prefs -> prefs[gyroZKey] ?: 0f }
    val timestamp = context.gaitDatastore.data.map { prefs -> prefs[timestampKey] ?: 0L }

    suspend fun setId(newValue: Int) {
        context.gaitDatastore.edit { prefs -> prefs[idKey] = newValue }
    }

    suspend fun setGyroX(newValue: Float) {
        context.gaitDatastore.edit { prefs -> prefs[gyroXKey] = newValue }
    }

    suspend fun setGyroY(newValue: Float) {
        context.gaitDatastore.edit { prefs -> prefs[gyroYKey] = newValue }
    }

    suspend fun setGyroZ(newValue: Float) {
        context.gaitDatastore.edit { prefs -> prefs[gyroZKey] = newValue }
    }

    suspend fun setTimestamp(newValue: Long) {
        context.gaitDatastore.edit { prefs -> prefs[timestampKey] = newValue }
    }
}
