package com.gaitmonitoring.bluetooth.permissions

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext

/**
 * A composable function to listen for system-wide broadcast events.
 * This function creates and registers a BroadcastReceiver to react to specific system actions.
 *
 * @param systemAction The action string that the broadcast receiver should listen for.
 * @param onSystemEvent A callback function that is invoked with the intent of the broadcast event.
 */
@Composable
fun SystemBroadcastReceiver(
    systemAction:String, // The Intent action to listen for, e.g., Intent.ACTION_BATTERY_CHANGED
    onSystemEvent:(intent: Intent?)->Unit // Callback function executed when the broadcast event occurs
) {
    // Context from the local Composition
    val context = LocalContext.current
    // Keeps the latest state of the callback to ensure it has the most recent data when called
    val currentOnSystemEvent by rememberUpdatedState(onSystemEvent)

    // Setting up the DisposableEffect to manage the lifecycle of the BroadcastReceiver
    DisposableEffect(context, systemAction){
        val intentFilter = IntentFilter(systemAction)
        val broadcast = object : BroadcastReceiver(){
            override fun onReceive(p0: Context?, intent: Intent?) {
                currentOnSystemEvent(intent)
            }
        }

        context.registerReceiver(broadcast, intentFilter) // Registering the receiver with the specified intent filter

        // Clean up: unregister the BroadcastReceiver when the composable is disposed or reconfigured
        onDispose {
            context.unregisterReceiver(broadcast)
        }

    }
}