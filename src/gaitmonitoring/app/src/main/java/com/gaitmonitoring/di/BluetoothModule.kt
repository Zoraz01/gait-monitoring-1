package com.gaitmonitoring.di

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import com.gaitmonitoring.data.GaitReceiveManager
import com.gaitmonitoring.data.ble.GaitBLEReceiveManager
import org.koin.dsl.module

val bluetoothModule = module {
    single { provideBluetoothAdapter(get()) }
    single { provideGaitReceiveManager(get(), get()) }
}

private fun provideBluetoothAdapter(context: Context): BluetoothAdapter {
    val manager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    return manager.adapter
}

private fun provideGaitReceiveManager(
    context: Context,
    bluetoothAdapter: BluetoothAdapter
): GaitReceiveManager {
    return GaitBLEReceiveManager(bluetoothAdapter,context)
}
