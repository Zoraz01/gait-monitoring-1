package com.gaitmonitoring.screens.home

import android.content.SharedPreferences
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gaitmonitoring.bluetooth.BleConnectionUtil
import com.gaitmonitoring.data.ConnectionState
import com.gaitmonitoring.data.MyUser
import com.gaitmonitoring.data.stepsRepository.OfflineStepsRepository
import com.gaitmonitoring.datastores.BLEConnectionDatastore
import com.gaitmonitoring.datastores.HomeDatastore
import com.gaitmonitoring.domain.GraphDataBuilder
import com.gaitmonitoring.firebase.firestore.FirestoreSensorsDataActions
import com.gaitmonitoring.screens.models.OnOffState
import com.gaitmonitoring.service.ServiceUtils
import com.github.mikephil.charting.data.LineData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant

class HomeScreenViewModel(
    private val homeDatastore: HomeDatastore,
    private val bleConnectionDatastore: BLEConnectionDatastore,
    private val firestoreSensorsDataActions: FirestoreSensorsDataActions,
    private val serviceUtils: ServiceUtils,
    private val graphDataBuilder: GraphDataBuilder,
    private val offlineStepsRepository: OfflineStepsRepository,
    private val stepsRepository: OfflineStepsRepository,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    private val _myUser = MutableStateFlow(value = null as MyUser?)
    val myUser = _myUser.asStateFlow()

    var initializingMessage by mutableStateOf<String?>(null)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set



    private val maxDataPoints = 30

    private val zDataPointsDevice1 = mutableListOf<Float>()
    private val zDataPointsDevice2 = mutableListOf<Float>()
    private val emgDataPointsDevice1 = mutableListOf<Float>()
    private val emgDataPointsDevice2 = mutableListOf<Float>()



    var connectionState by mutableStateOf<ConnectionState>(ConnectionState.Uninitialized)

    val isBleConnected = BleConnectionUtil.isBleConnected

    private val _onOffState = MutableStateFlow(value = OnOffState.OFF)
    val sensorState = _onOffState.asStateFlow()

    private val _graphData = MutableStateFlow(null as LineData?)
    val graphData = _graphData.asStateFlow()

    private val _stepsCount = MutableStateFlow(0)
    val stepsCount = _stepsCount.asStateFlow()

    init {
        updateUserName()
        //getChartData()
        //updateStepsCount(0)
        checkResetAfterInactivity()
    }

    private fun checkResetAfterInactivity() {
        viewModelScope.launch {
            val lastActiveTime = sharedPreferences.getLong("last_active_time", 0L)
            val currentTime = Instant.now().toEpochMilli()
            if ((currentTime - lastActiveTime) > 86400000) {  // 86400000 ms = 24 hours
                stepsRepository.resetSteps()  // Reset the steps count
                sharedPreferences.edit().putLong("last_active_time", currentTime).apply()
            }
        }
    }
    private fun updateStepsCount(additionalSteps: Int) {
        _stepsCount.value += additionalSteps  // Directly update the MutableStateFlow
    }

    private fun subscribeToChanges() {
        Log.d("GaitMonitoringApp", "Subscribing to changes in BLE data")

        viewModelScope.launch {
            bleConnectionDatastore.errorMessage.collectLatest { message ->
                errorMessage = message
            }
        }

        viewModelScope.launch {
            bleConnectionDatastore.connectionState.collectLatest { state ->
                connectionState = state
            }
        }

        viewModelScope.launch {
            bleConnectionDatastore.zValue.collectLatest { zValue ->
                zDataPointsDevice1.add(zValue.toFloat())
                updateGraphDataAndPublish()
            }
        }

        viewModelScope.launch {
            bleConnectionDatastore.emgValue.collectLatest { emgValue ->
                emgDataPointsDevice1.add(emgValue.toFloat())
            }
        }

        viewModelScope.launch {
            bleConnectionDatastore.zValueDevice2.collectLatest { zValueDevice2 ->
                zDataPointsDevice2.add(zValueDevice2.toFloat())
                updateGraphDataAndPublish()
            }
        }

        viewModelScope.launch {
            bleConnectionDatastore.emgValueDevice2.collectLatest { emgValueDevice2 ->
                emgDataPointsDevice2.add(emgValueDevice2.toFloat())
            }
        }
    }


    fun reconnect() {
        serviceUtils.reconnect()
    }

    public fun initializeConnection() {
        // Clear data points for both devices
        zDataPointsDevice1.clear()
        zDataPointsDevice2.clear()
        emgDataPointsDevice1.clear()
        emgDataPointsDevice2.clear()
        subscribeToChanges()
        serviceUtils.startBLEService()
    }


    private fun updateGraphDataAndPublish() {
        Log.d("GaitMonitoringApp", "Updating graph data and publishing")
        viewModelScope.launch {
            updateGraphData()

            val (dataPointDevice1, dataPointDevice2) = bleConnectionDatastore.getLatestSensorValues()

            firestoreSensorsDataActions.publishDataForDevice1(
                dataPointDevice1,
                { result ->
                    // Handle success or failure
                },
                { stepCount ->
                    updateStepsCount(stepCount)
                }
            )
            if (connectionState == ConnectionState.BothDevicesConnected) {
                firestoreSensorsDataActions.publishDataForDevice2(dataPointDevice2) { /* Handle Callback */ }
            }
        }
    }

    private fun updateGraphData() {
        // Ensure this method handles multiple datasets
        val lineData = graphDataBuilder.getGraphData(zDataPointsDevice1, zDataPointsDevice2)
        _graphData.update { lineData }
        trimDataPoints()
    }

    private fun trimDataPoints() {
        // Trim data points for both Z and EMG data of both devices
        if (zDataPointsDevice1.size > maxDataPoints) zDataPointsDevice1.removeFirst()
        if (zDataPointsDevice2.size > maxDataPoints) zDataPointsDevice2.removeFirst()
        if (emgDataPointsDevice1.size > maxDataPoints) emgDataPointsDevice1.removeFirst()
        if (emgDataPointsDevice2.size > maxDataPoints) emgDataPointsDevice2.removeFirst()
    }


    /*
    private fun getChartData() {

        firestoreSensorsDataActions.observeSensorData("" /* todo user uid */) { result ->
            when (result) {
                is TaskCallback.OnFailure -> {}
                is TaskCallback.OnSuccess -> {
                    val sensorsData = result.data
                    val lineData = graphDataBuilder.getGraphData(sensorsData)
                    _graphData.update { lineData }
                }
            }
        }
    } */


    private fun updateUserName() {
        viewModelScope.launch {
            homeDatastore.myUser.collectLatest { savedUser ->
                _myUser.update { savedUser }
            }
        }
    }

}