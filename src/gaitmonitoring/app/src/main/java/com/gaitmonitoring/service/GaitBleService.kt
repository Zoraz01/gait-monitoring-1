package com.gaitmonitoring.service

import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.IBinder
import android.util.Log
import androidx.core.app.ServiceCompat
import com.gaitmonitoring.data.ConnectionState
import com.gaitmonitoring.data.GaitReceiveManager
import com.gaitmonitoring.datastores.BLEConnectionDatastore
import com.gaitmonitoring.notification.NotificationUtils
import com.gaitmonitoring.utils.Resource
import com.gaitmonitoring.utils.isApi29
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class GaitBleService : Service() {

    // notification
    private val notificationUtils: NotificationUtils by inject()
    private val notificationId = 155

    // coroutine
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    private val gaitReceiveManager: GaitReceiveManager by inject()
    private val bleConnectionDatastore: BLEConnectionDatastore by inject()

    private var DEVICE_NAME = "ShankMonitor"
    private var SECOND_DEVICE_NAME = "CaneMonitor"
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("BLE_SERVICE", "GaitBleService started")

        // post notification ..
        startAsForeground()

        val extra = intent?.extras?.getString(ServiceUtils.ACTION_KEY)

        if (extra == ServiceUtils.ACTION_RECONNECT) {
            gaitReceiveManager.reconnect()
        } else {
            initializeConnection()
        }

        return START_STICKY
    }

    private fun startAsForeground() {
        notificationUtils.createNotificationChannel()
        val notification = notificationUtils.getNotification()
        ServiceCompat.startForeground(
            this,
            notificationId,
            notification,
            if (isApi29) ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC else 0
        )
    }

    private fun initializeConnection() {
        Log.d("BLE_SERVICE", "Initializing BLE Connection")
        scope.launch {
            bleConnectionDatastore.setErrorMessage(null)
        }
        subscribeToChanges()
        gaitReceiveManager.startReceiving()
    }


    private fun subscribeToChanges() {
        Log.d("BLE_SERVICE", "Subscribing to BLE data changes")
        scope.launch {
            gaitReceiveManager.data.collect { result ->

                when (result) {
                    is Resource.Success -> {
                        val gaitResult = result.data
                        with(bleConnectionDatastore) {
                            setConnectionState(gaitResult.connectionState)
                            setDeviceName(gaitResult.deviceName)

                            if (gaitResult.deviceName == DEVICE_NAME) {
                                setTimestampDevice1(gaitResult.timestamp)
                                setZ(gaitResult.z)
                                setY(gaitResult.y)
                                setX(gaitResult.x)
                                setXA(gaitResult.xA)
                            } else if (gaitResult.deviceName == SECOND_DEVICE_NAME) {
                                setTimestampDevice2(gaitResult.timestamp)
                                setZDevice2(gaitResult.z)
                                setYDevice2(gaitResult.y)
                                setXDevice2(gaitResult.x)
                                setXADevice2(gaitResult.xA)
                            }
                        }
                    }

                    is Resource.Loading -> {
                        with(bleConnectionDatastore) {
                            setInitializationMessage(result.message)
                            setConnectionState(ConnectionState.CurrentlyUninitialized)
                        }
                    }

                    is Resource.Error -> {
                        with(bleConnectionDatastore) {
                            setErrorMessage(result.errorMessage)
                            setConnectionState(ConnectionState.Uninitialized)
                        }

                    }
                }
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
        gaitReceiveManager.disconnect()
    }
}