package com.gaitmonitoring.screens.scan

import androidx.lifecycle.ViewModel
import com.gaitmonitoring.screens.scan.models.BluetoothDeviceItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow


class ScanScreenViewModel : ViewModel() {
    private val _bleDevices = MutableStateFlow(value = emptyList<BluetoothDeviceItem>())
    val bleDevices = _bleDevices.asStateFlow()

    init {
        setFakeBluetoothDevices()
    }

    private fun setFakeBluetoothDevices() {
        _bleDevices.value = listOf(
            BluetoothDeviceItem("Device 1", "00:11:22:33:44:55"),
            BluetoothDeviceItem("Device 2", "66:77:88:99:AA:BB"),
            BluetoothDeviceItem("Device 3", "CC:DD:EE:FF:00:11")
            // Add more fake devices here...
        )
    }
}
