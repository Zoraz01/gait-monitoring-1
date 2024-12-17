package com.gaitmonitoring.data

// Defines a sealed class to represent various connection states for a device.
sealed class ConnectionState(val stateName: String) {
    object Connected : ConnectionState(CONNECTED_STATE_NAME)
    object Disconnected : ConnectionState(DISCONNECTED_STATE_NAME)
    object Uninitialized : ConnectionState(UNINITIALIZED_STATE_NAME)
    object CurrentlyUninitialized : ConnectionState(CURRENTLY_UNINITIALIZED_STATE_NAME)
    object OneDeviceConnected : ConnectionState(ONE_DEVICE_CONNECTED_STATE_NAME)
    object BothDevicesConnected : ConnectionState(BOTH_DEVICES_CONNECTED_STATE_NAME)

    companion object {
        private const val CONNECTED_STATE_NAME = "connected"
        private const val DISCONNECTED_STATE_NAME = "disconnected"
        private const val UNINITIALIZED_STATE_NAME = "uninitialized"
        private const val CURRENTLY_UNINITIALIZED_STATE_NAME = "currently_uninitialized"
        private const val ONE_DEVICE_CONNECTED_STATE_NAME = "one_device_connected"
        private const val BOTH_DEVICES_CONNECTED_STATE_NAME = "both_devices_connected"

        // Map associating state names with their corresponding ConnectionState instances.
        private val statesMap = mapOf(
            CONNECTED_STATE_NAME to Connected,
            DISCONNECTED_STATE_NAME to Disconnected,
            UNINITIALIZED_STATE_NAME to Uninitialized,
            CURRENTLY_UNINITIALIZED_STATE_NAME to CurrentlyUninitialized,
            ONE_DEVICE_CONNECTED_STATE_NAME to OneDeviceConnected,
            BOTH_DEVICES_CONNECTED_STATE_NAME to BothDevicesConnected
        )

        fun getState(stateName: String): ConnectionState {
            return statesMap.getOrDefault(stateName, Uninitialized)
        }
    }
}
