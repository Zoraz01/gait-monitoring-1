package com.gaitmonitoring.firebase.firestore


import android.util.Log
import com.gaitmonitoring.data.GaitResult
import com.gaitmonitoring.data.sensors.StepDetector
import com.gaitmonitoring.firebase.TaskCallback
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlin.math.sqrt


// Class for handling Firestore operations related to sensor data from two devices.
class FirestoreSensorsDataActions {

    // Buffers for temporarily storing sensor data until enough data has been collected to batch upload.
    private val device1DataBuffer = mutableListOf<GaitResult>()
    private val device2DataBuffer = mutableListOf<GaitResult>()
    private val BUFFER_LIMIT = 1000
    private val PUBLISH_LIMIT = 100
    private val STEP_COUNT_BUFFER_LIMIT = 100

    private fun gaitResultToMap(result: GaitResult): Map<String, Any> {
        return mapOf(
            "y" to result.y,
            "z" to result.z,
            "x" to result.x,
            "xA" to result.xA,
            "timestamp" to result.timestamp,
            "connectionState" to result.connectionState,
            "deviceName" to result.deviceName
        )
    }




    // Method to add data to Device 1's buffer and publish it to Firestore when the buffer is full.
    fun publishDataForDevice1(data: GaitResult, callback: (TaskCallback<Unit>) -> Unit, stepCountCallback: (Int) -> Unit) {
        Log.d("FirestoreSensorsDataActions", "Publishing data for Device 1: $data")
        device1DataBuffer.add(data) // Add data to buffer for step counting

        // Publish data to Firestore immediately


        // Calculate steps after buffering 1000 data points
        if (device1DataBuffer.size >= STEP_COUNT_BUFFER_LIMIT) {
            val device1Collection = Firebase.firestore.collection("devices").document("device1").collection("data")
            device1Collection.add(data)
                .addOnSuccessListener {
                    Log.d(
                        "FirestoreSensorsDataActions",
                        "Data successfully uploaded to Firestore for Device 1"
                    )
                    callback(TaskCallback.OnSuccess(Unit))
                }
                .addOnFailureListener { exception ->
                    Log.e(
                        "FirestoreSensorsDataActions",
                        "Failed to upload data to Firestore for Device 2",
                        exception
                    )
                    callback(TaskCallback.OnFailure(exception))
                }
            val stepCount = StepDetector.calcStepsAsChunk(device1DataBuffer).toInt()
            Log.d("FirestoreSensorsDataActions", "Step count calculated: $stepCount")
            stepCountCallback(stepCount)
            device1DataBuffer.clear() // Clear buffer after step count calculation
            Log.d("FirestoreSensorsDataActions", "Calculating steps")
        }
    }




    // Similar to Device 1 but for Device 2 without step count calculation.
    fun publishDataForDevice2(data: GaitResult, callback: (TaskCallback<Unit>) -> Unit) {
        Log.d("FirestoreSensorsDataActions", "Adding data to Device 2 buffer")
        device2DataBuffer.add(data)
        Log.d("FirestoreSensorsDataActions", "Device 1 buffer size: ${device1DataBuffer.size}")

        if (device2DataBuffer.size >= STEP_COUNT_BUFFER_LIMIT) {
            val device2Collection = Firebase.firestore.collection("devices").document("device2").collection("data")
            // Attempt to add the buffered data to Firestore.
            device2Collection.add(data)
                .addOnSuccessListener {
                    Log.d(
                        "FirestoreSensorsDataActionsSuccess",
                        "Data successfully uploaded to Firestore for Device 2"
                    )
                    callback(TaskCallback.OnSuccess(Unit))
                }
                .addOnFailureListener { exception ->
                    Log.e(
                        "FirestoreSensorsDataActions",
                        "Failed to upload data to Firestore for Device 2",
                        exception
                    )
                    callback(TaskCallback.OnFailure(exception))
                }
        }
    }
}
