package com.gaitmonitoring.data.ble

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.util.Log
import com.gaitmonitoring.data.ConnectionState
import com.gaitmonitoring.data.GaitReceiveManager
import com.gaitmonitoring.data.GaitResult
import com.gaitmonitoring.utils.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import kotlin.reflect.KMutableProperty0



@SuppressLint("MissingPermission")
class GaitBLEReceiveManager(
    private val bluetoothAdapter: BluetoothAdapter,
    private val context: Context
) : GaitReceiveManager {



    private val STANDARD_CCCD_UUID = "00002902-0000-1000-8000-00805f9b34fb"

    private var DEVICE_NAME = "ShankMonitor"
    private var GAIT_SERVICE_UIID = "0000180f-0000-1000-8000-00805f9b34fb"
    private var GAIT_CHARACTERISTICS_UUID = "00002a19-0000-1000-8000-00805f9b34fb"

    private var SECOND_DEVICE_NAME = "CaneMonitor"
    private var SECOND_GAIT_SERVICE_UUID = "00001808-0000-1000-8000-00805f9b34fb"
    private var SECOND_GAIT_CHARACTERISTICS_UUID = "00002a18-0000-1000-8000-00805f9b34fb"

    override val data: MutableSharedFlow<Resource<GaitResult>> = MutableSharedFlow()

    private val bleScanner by lazy {
        bluetoothAdapter.bluetoothLeScanner

    }

    private val scanSettings = ScanSettings.Builder()
        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
        .build()

    /*Here the objects that represent teh connection with the BLe devices are initilaized*/
    // Keeps track of the GATT connections for the first and second BLE devices.
    private var firstDeviceGatt: BluetoothGatt? = null
    private var secondDeviceGatt: BluetoothGatt? = null


    // Flag to keep track of the BLE scanning status.
    private var isScanning = false

    // Coroutine scope for asynchronous operations.
    private val coroutineScope = CoroutineScope(Dispatchers.Default)
    private var isFirstDeviceConnected = false
    private var isSecondDeviceConnected = false

    fun getCurrentFormattedDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())
        return dateFormat.format(Date())
    }

    // Checks if both BLE devices are connected.
    // Returns true if both firstDeviceGatt and secondDeviceGatt are in a bonded state, false otherwise.
    private fun areBothDevicesConnected(): Boolean {
        return isFirstDeviceConnected && isSecondDeviceConnected
    }

    // Determines the current connection state of the BLE devices.
    // Returns a ConnectionState enum value based on whether none, one, or both devices are connected.
    private fun getCurrentConnectionState(): ConnectionState {
        return when {
            isFirstDeviceConnected && isSecondDeviceConnected -> ConnectionState.BothDevicesConnected
            isFirstDeviceConnected || isSecondDeviceConnected -> ConnectionState.OneDeviceConnected
            else -> ConnectionState.Disconnected
        }
    }

    // Callback for BLE scan results.
    // Triggers connectDevice function when a BLE device with a matching name is found.
    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            when (result.device.name) {
                DEVICE_NAME -> connectDevice(result, ::firstDeviceGatt)
                SECOND_DEVICE_NAME -> connectDevice(result, ::secondDeviceGatt)
            }
        }
    }

    // Initiates a connection to a BLE device and sets the corresponding BluetoothGatt object.
    // If the device is not already connected, it initiates a GATT connection.
    // After initiating a connection, it checks if both devices are connected to decide whether to stop scanning.
    private fun connectDevice(result: ScanResult, gattRef: KMutableProperty0<BluetoothGatt?>) {
        if (gattRef.get() == null) {
            coroutineScope.launch {
                data.emit(Resource.Loading(message = "Connecting to ${result.device.name}..."))
            }
            Log.d("BLE_DEBUG", "Attempting to connect to ${result.device.name}")
            gattRef.set(result.device.connectGatt(context, true, gattCallback, BluetoothDevice.TRANSPORT_LE))
        }

        // Check if both devices are connected and stop scanning if true
        if (areBothDevicesConnected()) {
            stopScanning()
        }
    }


    // Stops the BLE device scanning process if it's currently active.
    // Updates the UI with the current connection state after stopping the scan.
    private fun stopScanning() {
        if (isScanning) {
            bleScanner.stopScan(scanCallback)
            isScanning = false
            updateUIWithConnectionState()
            Log.d("BLE_DEBUG", "Stopping BLE scan")
        }
    }


    // Updates the UI with the current connection state of the BLE devices.
    // Emits a Resource.Success with a GaitResult containing the updated connection state.
    private fun updateUIWithConnectionState() {
        val connectionState = getCurrentConnectionState()
        coroutineScope.launch {
            data.emit(Resource.Success(GaitResult(0, 0, 0,0,getCurrentFormattedDate(),connectionState, "")))
        }
    }


    private var firstDeviceConnectionAttempt = 1
    private var secondDeviceConnectionAttempt = 1
    private var MAXIMUM_CONNECTION_ATTEMPTS = 5

    private fun resetConnectionAttempts() {
        firstDeviceConnectionAttempt = 1
        secondDeviceConnectionAttempt = 1
    }

    private val gattCallback = object : BluetoothGattCallback() {
        /**
         * Handles changes in connection state between the app and a BLE device.
         * This callback is invoked when there are changes in the connection state between the app's GATT client
         * and a BLE device. It handles both successful connections, disconnections, and unsuccessful connection attempts.
         *
         * @param gatt The BluetoothGatt client that you can use to conduct GATT operations.
         * @param status The result of the connection change operation. GATT_SUCCESS if the operation succeeded.
         * @param newState The new connection state. Can be either STATE_CONNECTED or STATE_DISCONNECTED from BluetoothProfile.
         */
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {

            val device = gatt.device
            val deviceName = device.name
            Log.d("BLE_DEBUG", "onConnectionStateChange: Device=${device.name}, Status=$status, NewState=$newState")
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    // Logs and handle the state when the device is successfully connected.
                    Log.d("BLE_DEBUG", "Connected to GATT server. Device: $deviceName")
                    coroutineScope.launch {
                        data.emit(Resource.Loading(message = "Discovering Services on $deviceName..."))
                    }
                    // Start service discovery on the connected GATT server.
                    gatt.discoverServices()
                    Log.d("BLE_DEBUG", "Service discovery started for $deviceName")
                    // Update internal flags and references based on which device was connected.
                    if (deviceName == DEVICE_NAME) {
                        isFirstDeviceConnected = true

                        firstDeviceGatt = gatt
                    } else if (deviceName == SECOND_DEVICE_NAME) {
                        isSecondDeviceConnected = true
                        secondDeviceGatt = gatt
                    }
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    // Handle the state when the device is disconnected.
                    Log.d("BLE_DEBUG", "Disconnected from GATT server. Device: $deviceName")
                    if (deviceName == DEVICE_NAME) {
                        isFirstDeviceConnected = false
                        firstDeviceGatt = null
                    } else if (deviceName == SECOND_DEVICE_NAME) {
                        isSecondDeviceConnected = false
                        secondDeviceGatt = null
                    }
                    // Emit the current connection state after disconnection.

                    coroutineScope.launch {
                        data.emit(Resource.Success(GaitResult(0, 0, 0,0,getCurrentFormattedDate(),getCurrentConnectionState(), deviceName)))
                    }
                    // Close the GATT client.
                    gatt.close()
                }
                // Update the UI with the current connection state.
                updateUIWithConnectionState()
                // If both devices are connected, stop scanning.
                if (areBothDevicesConnected()) {
                    Log.d("BLE_DEBUG", "Both devices are connected. Stopping BLE scan.")
                    stopScanning()
                }
            } else { // This else branch handles the unsuccessful connection attempts
                Log.d("BLE_DEBUG", "Connection failed. Status=$status, Device=$deviceName")
                gatt.close()
                // Retry connecting to the devices based on predefined maximum attempts.
                if (deviceName == DEVICE_NAME) {
                    if (firstDeviceConnectionAttempt <= MAXIMUM_CONNECTION_ATTEMPTS) {
                        Log.d("BLE_DEBUG", "Retrying connection to $DEVICE_NAME: Attempt $firstDeviceConnectionAttempt")
                        firstDeviceConnectionAttempt++
                        coroutineScope.launch {
                            data.emit(Resource.Loading(message = "Retrying connection to $DEVICE_NAME $firstDeviceConnectionAttempt/$MAXIMUM_CONNECTION_ATTEMPTS"))
                        }
                        startReceiving()
                    } else {
                        Log.d("BLE_DEBUG", "Failed to connect to $DEVICE_NAME after multiple attempts.")
                        coroutineScope.launch {
                            data.emit(Resource.Error(errorMessage = "Could not connect to $DEVICE_NAME"))
                        }
                    }
                } else if (deviceName == SECOND_DEVICE_NAME) {
                    if (secondDeviceConnectionAttempt <= MAXIMUM_CONNECTION_ATTEMPTS) {
                        Log.d("BLE_DEBUG", "Retrying connection to $DEVICE_NAME: Attempt $firstDeviceConnectionAttempt")
                        secondDeviceConnectionAttempt++
                        coroutineScope.launch {
                            data.emit(Resource.Loading(message = "Retrying connection to $SECOND_DEVICE_NAME $secondDeviceConnectionAttempt/$MAXIMUM_CONNECTION_ATTEMPTS"))
                        }
                        startReceiving()
                    } else {
                        Log.d("BLE_DEBUG", "Failed to connect to $DEVICE_NAME after multiple attempts.")
                        coroutineScope.launch {
                            data.emit(Resource.Error(errorMessage = "Could not connect to $SECOND_DEVICE_NAME"))
                        }
                    }
                }
            }
        }

        /**
         * Callback triggered when BLE services have been discovered on the connected device.
         * This method handles the initial setup after a successful connection and services discovery,
         * including initiating a request to adjust the MTU (Maximum Transmission Unit) size.
         *
         * @param gatt The BluetoothGatt client that you can use to conduct GATT operations on the connected device.
         * @param status The status of the service discovery operation. GATT_SUCCESS if the operation succeeded.
         */
        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            with(gatt) {
                printGattTable()
                // Notify the UI layer or state management that the application is about to adjust MTU sizes.
                coroutineScope.launch {
                    data.emit(Resource.Loading(message = "Adjusting MTU space..."))
                }
                // Log and initiate a request to change the MTU size to 23, which could optimize BLE communication.
                Log.d("GaitBLEReceiveManager", "Requesting MTU change to 23")
                gatt.requestMtu(23)
                Log.d("GaitBLEReceiveManager", "MTU request sent")
                // Log the completion of service discovery, indicating that the device is now ready for further configuration or interaction.
                Log.d("BLE_DEBUG", "Service discovery completed on ${gatt.device.name}")
            }
        }
        /**
         * Callback triggered when a request to change the MTU (Maximum Transmission Unit) size has been completed.
         * This method proceeds with further BLE configuration based on the new MTU size, such as setting up notifications
         * for a specific characteristic that's crucial for the application's functionality.
         *
         * @param gatt The BluetoothGatt client involved in this MTU change.
         * @param mtu The new MTU size that has been negotiated between the device and the GATT client.
         * @param status The status of the MTU change operation. GATT_SUCCESS if the operation succeeded.
         */
        override fun onMtuChanged(gatt: BluetoothGatt, mtu: Int, status: Int) {
            // Log the outcome of the MTU request to help in debugging and ensuring the operation was successful.
            Log.d("GaitBLEReceiveManager", "onMtuChanged called with MTU: $mtu and status: $status")

            // Determine which service and characteristic UUIDs to interact with based on the connected device's name.
            val (serviceUUID, characteristicUUID) = when (gatt.device.name) {
                DEVICE_NAME -> Pair(GAIT_SERVICE_UIID, GAIT_CHARACTERISTICS_UUID)
                SECOND_DEVICE_NAME -> Pair(SECOND_GAIT_SERVICE_UUID, SECOND_GAIT_CHARACTERISTICS_UUID)
                else -> return // Exit if the device is not recognized, preventing further actions on unknown devices.
            }

            // Find the characteristic that matches the provided UUIDs. If not found, emit an error state.
            val characteristic = findCharacteristics(gatt, serviceUUID, characteristicUUID)
            if (characteristic == null) {
                coroutineScope.launch {
                    data.emit(Resource.Error(errorMessage = "Could not find gait publisher for ${gatt.device.name}"))
                }
                return
            }

            // If the characteristic is found, proceed to enable notifications on it to receive continuous data updates.
            enableNotification(gatt, characteristic)
        }
        /**
         * Callback triggered when a characteristic's value changes, which indicates that new data is available from the BLE device.
         * This method processes the received data, specifically handling the Gait-related data characterized by its UUIDs.
         * It extracts the necessary information, converts it to a meaningful format, and emits it to be handled by the application.
         *
         * @param gatt The BluetoothGatt client through which the characteristic change was notified.
         * @param characteristic The characteristic that has changed, containing the new data.
         */
        override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
            try {
                // Retrieve the device name for logging and identification.
                val deviceName = gatt.device.name
                // Process the characteristic change based on its UUID.
                when (characteristic.uuid) {
                    // Match the UUIDs that are relevant to the Gait data processing.
                    UUID.fromString(GAIT_CHARACTERISTICS_UUID),
                    UUID.fromString(SECOND_GAIT_CHARACTERISTICS_UUID) -> {
                        // Extract byte arrays from the characteristic's value, which represent different data points.
                        val buffer = ByteBuffer.wrap(characteristic.value).order(ByteOrder.LITTLE_ENDIAN)

                        val y = buffer.short.toInt() // Now reads first 2 bytes as a little-endian short
                        val z = buffer.short.toInt() // Reads next 2 bytes
                        val x = buffer.short.toInt() // And so forth
                        val xA = buffer.short.toInt()
                        // Construct a GaitResult object to encapsulate the gathered data.
                        val gaitresult = GaitResult(
                            y = y,
                            z = z,
                            x = x,
                            xA = xA,
                            timestamp = getCurrentFormattedDate(),
                            connectionState = getCurrentConnectionState(),
                            deviceName = deviceName
                        )
                        // Log and emit the processed data for further application use.
                        Log.d("CharacteristicChanged", "GaitResult being sent: $gaitresult")
                        coroutineScope.launch {
                            data.emit(Resource.Success(data = gaitresult))
                        }
                        // Ignore any other characteristic changes that do not match the expected UUIDs.
                    }
                    else -> Unit
                }
            } catch (e: Exception) {
                Log.e("CharacteristicChanged", "Error in onCharacteristicChanged: ${e.message}")
            }
            Log.d("CharacteristicChanged", "Characteristic changed: ${characteristic.uuid} on ${gatt.device.name}")
        }

    }
    /**
     * Enables notifications or indications for a specific BLE characteristic.
     * This method sets up the necessary configuration to allow the app to receive updates from a BLE device
     * whenever the specified characteristic's value changes.
     *
     * @param gatt The BluetoothGatt connection to the BLE device.
     * @param characteristic The BluetoothGattCharacteristic for which notifications or indications are to be enabled.
     */
    private fun enableNotification(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic)  {
        // Log the UUID of the characteristic for which notifications are being enabled.
        Log.d(
            "BLEReceiveManager",
            "Enabling notification for characteristic: ${characteristic.uuid}"
        )
        // Define the UUID for the Client Characteristic Configuration Descriptor (CCCD) which controls notifications and indications.
        val cccdUuid = UUID.fromString(STANDARD_CCCD_UUID)
        // Determine whether the characteristic supports notifications or indications and prepare the corresponding payload.
        val payload = when {

            characteristic.isIndicatable() -> {
                // Log if the characteristic supports indications.
                Log.d("BLEReceiveManager", "Characteristic is indicatable")
                BluetoothGattDescriptor.ENABLE_INDICATION_VALUE
            }

            characteristic.isNotifiable() -> {
                // Log if the characteristic supports notifications.
                Log.d("BLEReceiveManager", "Characteristic is notifiable")
                BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
            }

            else -> {
                // Log and exit if the characteristic supports neither, as no further action can be taken.
                Log.d("BLEReceiveManager", "Characteristic is neither indicatable nor notifiable")
                return
            }
        }

        // Retrieve the CCCD based on its UUID. This descriptor is needed to enable notifications/indications.
        val cccdDescriptor = characteristic.getDescriptor(cccdUuid)
        if (cccdDescriptor == null) {
            // Log an error and return if the CCCD was not found, as notifications or indications cannot be enabled.
            Log.d(
                "BLEReceiveManager",
                "CCCD Descriptor not found for characteristic: ${characteristic.uuid}"
            )
            return
        }

        // Enable notifications or indications on the device.
        val notificationSet = gatt?.setCharacteristicNotification(characteristic, true)
        if (notificationSet == false) {
            // Log an error if enabling the notification or indication failed.
            Log.d(
                "BLEReceiveManager",
                "Failed to set characteristic notification for: ${characteristic.uuid}"
            )
            return
        }

        // Write the payload to the CCCD to actually enable notifications or indications.
        writeDescription(gatt, cccdDescriptor, payload)
        Log.d("BLEReceiveManager", "Descriptor write initiated for: ${characteristic.uuid}")

    }

    /**
     * Writes a descriptor value to a BLE device.
     * This function is specifically used to update the Client Characteristic Configuration Descriptor (CCCD)
     * to enable notifications or indications on a BLE characteristic. This is a critical step in setting up
     * real-time communication between the device and the app.
     *
     * @param gatt The BluetoothGatt connection to the BLE device, used to communicate with the BLE hardware.
     * @param descriptor The descriptor to which the payload will be written. Typically, this is the CCCD.
     * @param payload The byte array that contains the value to be written to the descriptor. This value
     *        determines whether notifications or indications are enabled or disabled.
     */
    private fun writeDescription(gatt: BluetoothGatt, descriptor: BluetoothGattDescriptor, payload: ByteArray) {
        descriptor.value = payload
        if (!gatt.writeDescriptor(descriptor)) {
            Log.e("GaitBLEReceiveManager", "Failed to write descriptor for: ${descriptor.uuid}")
        }
    }


    /**
     * Searches for a specific characteristic within a specified service on a BLE device.
     * This method is used to locate a BLE characteristic by its UUID within a particular service, also identified by UUID.
     * This is crucial for interacting with the BLE device where operations like reading, writing, or setting notifications
     * are performed on specific characteristics.
     *
     * @param gatt The BluetoothGatt connection to the BLE device, representing the GATT client.
     * @param serviceUUID The UUID of the BLE service as a string. This UUID should uniquely identify the service
     *        containing the characteristic of interest.
     * @param characteristicsUUID The UUID of the BLE characteristic as a string. This UUID should uniquely identify
     *        the characteristic within the service that is needed for further operations.
     * @return BluetoothGattCharacteristic? The characteristic if found, or null if either the service or the
     *         characteristic does not exist.
     */
    private fun findCharacteristics(gatt: BluetoothGatt, serviceUUID: String, characteristicsUUID: String): BluetoothGattCharacteristic? {
        return gatt.services?.find { service ->
            service.uuid.toString() == serviceUUID
        }?.characteristics?.find { characteristic ->
            characteristic.uuid.toString() == characteristicsUUID
        }
    }

    /**
     * Initiates the BLE scanning process if not all target devices are currently connected.
     * This method checks the connection status of the designated BLE devices and starts scanning
     * if either of the devices is not connected. Scanning is critical for establishing connections
     * with BLE devices that are not yet connected but are necessary for the application's functionality.
     */
    override fun startReceiving() {
        if (!areBothDevicesConnected()) {
            coroutineScope.launch {
                data.emit(Resource.Loading(message = "Scanning BLE devices..."))
            }
            Log.d("BLE_DEBUG", "Starting BLE scan")
            isScanning = true
            bleScanner.startScan(null, scanSettings, scanCallback)
        }
    }

    /**
     * Attempts to reconnect to BLE devices if the BluetoothGatt connections are still available.
     * This method is typically used in scenarios where the connection may have dropped or been
     * interrupted and there is a need to re-establish the connection without a full re-discovery
     * process via scanning.
     */
    override fun reconnect() {
        firstDeviceGatt?.connect()
        secondDeviceGatt?.connect()
    }

    override fun disconnect() {
        firstDeviceGatt?.disconnect()
        secondDeviceGatt?.disconnect()
    }


    override fun closeConnection() {
        // Stop scanning for BLE devices
        bleScanner.stopScan(scanCallback)

        // Disconnect and close the first device's GATT connection
        firstDeviceGatt?.let { gatt ->
            val characteristic = findCharacteristics(gatt, GAIT_SERVICE_UIID, GAIT_CHARACTERISTICS_UUID)
            characteristic?.let { disconnectCharacteristic(gatt, it) }
            gatt.close()
            firstDeviceGatt = null
        }

        // Disconnect and close the second device's GATT connection
        secondDeviceGatt?.let { gatt ->
            val characteristic = findCharacteristics(gatt, SECOND_GAIT_SERVICE_UUID, SECOND_GAIT_CHARACTERISTICS_UUID)
            characteristic?.let { disconnectCharacteristic(gatt, it) }
            gatt.close()
            secondDeviceGatt = null
        }
    }

    private fun disconnectCharacteristic(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
        val cccdUuid = UUID.fromString(CCCD_DESCRIPTOR_UUID)
        characteristic.getDescriptor(cccdUuid)?.let { cccdDescriptor ->
            if (gatt.setCharacteristicNotification(characteristic, false)) {
                writeDescription(gatt, cccdDescriptor, BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE)
            } else {
                Log.d("GaitReceiveManager", "Failed to disable characteristic notification")
            }
        }
    }

}