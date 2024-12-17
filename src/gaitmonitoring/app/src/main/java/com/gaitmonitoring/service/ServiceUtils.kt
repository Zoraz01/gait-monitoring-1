package com.gaitmonitoring.service

import android.content.Context
import android.content.Intent
import com.gaitmonitoring.utils.isApi26

class ServiceUtils(private val context: Context) {

    companion object{
        const val ACTION_KEY = "action_key"
        const val ACTION_RECONNECT = "reconnect"
    }


    fun startBLEService(action: String? = null) {
        val intent =
            Intent(context, GaitBleService::class.java).apply {
                action?.let {
                    this.putExtra(ACTION_KEY, it)
                }
            }
        if (isApi26) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }

    fun stopBLEService() {
        context.stopService(Intent(context, GaitBleService::class.java))
    }

    fun reconnect() {
        startBLEService(action = ACTION_RECONNECT)
    }
}