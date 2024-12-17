package com.gaitmonitoring.data.ble

/**
 * Singleton object for managing BLE device configurations.
 * This manager holds the mappings of predefined BLE devices and their service and characteristic UUIDs,
 * facilitating quick setup and interaction with specific BLE devices by their names.
 * Not in use in this version of the app but could be helpful when it comes to having multiple diffrent versions of the BLE devices.
 * Allows for all the compatible BLE devices to be stored in here
 */
object DeviceManager {
    var currentServiceUUID: String? = null
    var currentCharacteristicUUID: String? = null

    // Predefined devices mapped by their names
    val devices = mapOf(
        "Device1" to Pair("0000aa20-0000-1000-8000-00805f9b34fb", "0000aa21-0000-1000-8000-00805f9b34fb"),
        "GSRMONITOR" to Pair("0000180F-0000-1000-8000-00805F9B34FB", "00002A19-0000-1000-8000-00805F9B34FB"),
        "Device3" to Pair("ServiceUUID3", "CharacteristicUUID3")

    )

    fun setDeviceByDeviceName(deviceName: String) {
        devices[deviceName]?.let { (serviceUUID, characteristicUUID) ->
            currentServiceUUID = serviceUUID
            currentCharacteristicUUID = characteristicUUID
        }
    }
}