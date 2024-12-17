package com.gaitmonitoring.bluetooth

import android.bluetooth.BluetoothGatt
import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow


/*
BleConnectionUtil.updateBleConnectionStatus(context, true) // When connected
BleConnectionUtil.updateBleConnectionStatus(context, false) // When disconnected

*/

/**
 * Utility object for managing Bluetooth connection states.
 * This object encapsulates the functionality to track and update the connection status
 * to a Bluetooth device using the GATT profile.
 */
object BleConnectionUtil {
    // Private mutable state flow to manage the connection status internally
    private val _isBleConnected = MutableStateFlow(false)
    // Publicly accessible state flow to observe the connection status
    val isBleConnected = _isBleConnected.asStateFlow()

    // Optional property to hold a reference to the BluetoothGatt client
    var bluetoothGatt: BluetoothGatt? = null
    /**
     * Updates the connection status of the Bluetooth device.
     * This method should be called to update the connection state within the application,
     * typically in response to BluetoothGatt callbacks indicating connection changes.
     *
     * @param context The context used for possible operations that may require it.
     * @param connected The new connection state; true if connected, false otherwise.
     */
    fun updateBleConnectionStatus(context: Context, connected: Boolean) {
        _isBleConnected.value = connected
        // Additional logic to handle the GATT client can be added here.
    }
}
