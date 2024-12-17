package com.gaitmonitoring.datastores

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.gaitmonitoring.data.ConnectionState
import com.gaitmonitoring.data.GaitResult
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.bLEConnectionDatastore: DataStore<Preferences> by preferencesDataStore("ble_connection_data_store")

class BLEConnectionDatastore(private val context: Context) {

    private val connectionStateKey = stringPreferencesKey("connection_state_key")
    private val zValueKey = intPreferencesKey("z_value_key")
    private val emgValueKey = intPreferencesKey("emg_value_key")
    private val zValueDevice2Key = intPreferencesKey("z_value_device2_key")
    private val emgValueDevice2Key = intPreferencesKey("emg_value_device2_key")
    private val timestampDevice1Key = stringPreferencesKey("timestamp_device1_key")
    private val timestampDevice2Key = stringPreferencesKey("timestamp_device2_key")
    private val deviceNameKey = stringPreferencesKey("device_name_key")
    private val initializationMessageKey = stringPreferencesKey("initialization_message_key")
    private val errorMessageKey = stringPreferencesKey("error_message_key")
    private val yValueKey = intPreferencesKey("y_value_key")
    private val yValueDevice2Key = intPreferencesKey("y_value_key")
    private val xValueKey = intPreferencesKey("x_value_key")
    private val xValueDevice2Key = intPreferencesKey("x_value_key")
    private val xAValueKey = intPreferencesKey("xA_value_key")
    private val xAValueDevice2Key = intPreferencesKey("xA_value_key")

    val errorMessage = context.bLEConnectionDatastore.data.map { it[errorMessageKey] }
    val connectionState = context.bLEConnectionDatastore.data.map { prefs ->
        val value = prefs[connectionStateKey]
        value?.let {
            ConnectionState.getState(it)
        } ?: ConnectionState.Uninitialized
    }
    val zValue = context.bLEConnectionDatastore.data.map { it[zValueKey] ?: 0 }
    val emgValue = context.bLEConnectionDatastore.data.map { it[emgValueKey] ?: 0 }
    val zValueDevice2 = context.bLEConnectionDatastore.data.map { it[zValueDevice2Key] ?: 0 }
    val emgValueDevice2 = context.bLEConnectionDatastore.data.map { it[emgValueDevice2Key] ?: 0 }
    val yValue = context.bLEConnectionDatastore.data.map { it[yValueKey] ?: 0 }
    val yValueDevice2 = context.bLEConnectionDatastore.data.map { it[yValueDevice2Key] ?: 0 }
    val xValue = context.bLEConnectionDatastore.data.map { it[xValueKey] ?: 0 }
    val xValueDevice2 = context.bLEConnectionDatastore.data.map { it[xValueDevice2Key] ?: 0 }
    val xAValue = context.bLEConnectionDatastore.data.map { it[xAValueKey] ?: 0 }
    val xAValueDevice2 = context.bLEConnectionDatastore.data.map { it[xAValueDevice2Key] ?: 0 }
    val timestampDevice1 = context.bLEConnectionDatastore.data.map { it[timestampDevice1Key] ?: "" }
    val timestampDevice2 = context.bLEConnectionDatastore.data.map { it[timestampDevice2Key] ?: "" }
    val deviceName = context.bLEConnectionDatastore.data.map { it[deviceNameKey] ?: "" }
    val initializationMessage = context.bLEConnectionDatastore.data.map { it[initializationMessageKey] }

    suspend fun setErrorMessage(message: String?) {
        context.bLEConnectionDatastore.edit { prefs ->
            message?.let { prefs[errorMessageKey] = it } ?: prefs.remove(errorMessageKey)
        }
    }

    suspend fun setConnectionState(connectionState: ConnectionState) {
        context.bLEConnectionDatastore.edit { prefs ->
            prefs[connectionStateKey] = connectionState.stateName
        }
    }

    suspend fun setZ(value: Int) {
        Log.d("GaitMonitoringApp", "Setting Z value: $value")
        context.bLEConnectionDatastore.edit { prefs ->
            prefs[zValueKey] = value
        }
    }

    suspend fun setEmg(value: Int) {
        Log.d("GaitMonitoringApp", "Setting EMG value: $value")
        context.bLEConnectionDatastore.edit { prefs ->
            prefs[emgValueKey] = value
        }
    }
    suspend fun setZDevice2(value: Int) {
        Log.d("GaitMonitoringApp", "Setting Z value for Device 2: $value")
        context.bLEConnectionDatastore.edit { prefs ->
            prefs[zValueDevice2Key] = value
        }
    }

    suspend fun setEmgDevice2(value: Int) {
        Log.d("GaitMonitoringApp", "Setting EMG value for Device 2: $value")
        context.bLEConnectionDatastore.edit { prefs ->
            prefs[emgValueDevice2Key] = value
        }
    }
    suspend fun setY(value: Int) {
        context.bLEConnectionDatastore.edit { prefs -> prefs[yValueKey] = value }
    }
    suspend fun setYDevice2(value: Int) {
        context.bLEConnectionDatastore.edit { prefs -> prefs[yValueDevice2Key] = value }
    }

    suspend fun setX(value: Int) {
        context.bLEConnectionDatastore.edit { prefs -> prefs[xValueKey] = value }
    }
    suspend fun setXDevice2(value: Int) {
        context.bLEConnectionDatastore.edit { prefs -> prefs[xValueDevice2Key] = value }
    }

    suspend fun setXA(value: Int) {
        context.bLEConnectionDatastore.edit { prefs -> prefs[xAValueKey] = value }
    }
    suspend fun setXADevice2(value: Int) {
        context.bLEConnectionDatastore.edit { prefs -> prefs[xAValueDevice2Key] = value }
    }

    suspend fun setTimestampDevice1(timestamp: String) {
        context.bLEConnectionDatastore.edit { prefs ->
            prefs[timestampDevice1Key] = timestamp
        }
    }
    suspend fun setTimestampDevice2(timestamp: String) {
        context.bLEConnectionDatastore.edit { prefs ->
            prefs[timestampDevice2Key] = timestamp
        }
    }

    suspend fun setDeviceName(deviceName: String) {
        context.bLEConnectionDatastore.edit { prefs ->
            prefs[deviceNameKey] = deviceName
        }
    }

    suspend fun setInitializationMessage(message: String?) {
        context.bLEConnectionDatastore.edit { prefs ->
            message?.let { prefs[initializationMessageKey] = it } ?: prefs.remove(initializationMessageKey)
        }
    }


    suspend fun getLatestSensorValues(): Pair<GaitResult, GaitResult> {
        val yValueDevice1 = context.bLEConnectionDatastore.data.map { it[yValueKey] ?: 0 }.first()
        val xValueDevice1 = context.bLEConnectionDatastore.data.map { it[xValueKey] ?: 0 }.first()
        val zValueDevice1 = context.bLEConnectionDatastore.data.map { it[zValueKey] ?: 0 }.first()
        val xAValueDevice1 = context.bLEConnectionDatastore.data.map { it[xAValueKey] ?: 0 }.first()

        val timestampDevice1 = context.bLEConnectionDatastore.data.map { it[timestampDevice1Key] ?: "" }.first()

        val yValueDevice2 = context.bLEConnectionDatastore.data.map { it[yValueDevice2Key] ?: 0 }.first()
        val xValueDevice2 = context.bLEConnectionDatastore.data.map { it[xValueDevice2Key] ?: 0 }.first()
        val zValueDevice2 = context.bLEConnectionDatastore.data.map { it[zValueDevice2Key] ?: 0 }.first()
        val xAValueDevice2 = context.bLEConnectionDatastore.data.map { it[xAValueDevice2Key] ?: 0 }.first()
        val timestampDevice2 = context.bLEConnectionDatastore.data.map { it[timestampDevice2Key] ?: "" }.first()

        val deviceName1 = "Device1" // Replace with actual device name if needed
        val deviceName2 = "Device2" // Replace with actual device name if needed

        val gaitResultDevice1 = GaitResult(yValueDevice1,zValueDevice1, xValueDevice1,xAValueDevice1, timestampDevice1, ConnectionState.Connected, deviceName1)
        val gaitResultDevice2 = GaitResult(yValueDevice2,zValueDevice2, xValueDevice2,xAValueDevice2, timestampDevice2, ConnectionState.Connected, deviceName2)

        return Pair(gaitResultDevice1, gaitResultDevice2)
    }
}
