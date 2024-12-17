package com.gaitmonitoring.data.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.gaitmonitoring.data.GaitResult
import com.gaitmonitoring.data.stepsRepository.OfflineStepsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.sqrt


class StepDetector(
    private val context: Context,
    private val stepsRepository: OfflineStepsRepository
) {
/*

    private val sensorManager by lazy { context.getSystemService(Context.SENSOR_SERVICE) as SensorManager }
    private var callback: SensorEventListener? = null

    // for accelerometer ..
    private var mGravity = mutableListOf<Float>()
    private var mAccel = 0.0
    private var mAccelCurrent = 0.0
    private var mAccelLast = 0.0
    private var sensorRegistered = false

    private var hitCount = 0
    private var hitSum = 0.0
    private var hitResult = 0.0

    // change this sample size as you want, higher is more precise but slow measure.
    private val sampleSize = 75

    // change this threshold as you want, higher is more spike movement
    private val threshold = 0.70


    fun detect(
        onStep: () -> Unit
    ) {

        val stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)

        if (stepDetectorSensor != null) {
            callback = object : SensorEventListener {
                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
                override fun onSensorChanged(event: SensorEvent?) {
                    if (event?.sensor?.type == Sensor.TYPE_STEP_DETECTOR) {
                        //val value = event.values[0]
                        onStep()
                    }
                }
            }
            val isRegistered =
                sensorManager.registerListener(callback, stepDetectorSensor, SensorManager.SENSOR_DELAY_NORMAL)
            if (!isRegistered) {     // either the sensor is not exist or the permission was denied ..
                // register accelerometer sensor ..
                val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
                mAccel = 0.00
                mAccelCurrent = SensorManager.GRAVITY_EARTH.toDouble()
                mAccelLast = SensorManager.GRAVITY_EARTH.toDouble()
                callback = object : SensorEventListener {
                    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
                    override fun onSensorChanged(event: SensorEvent?) {
                        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
                            calculateStep(event, onStep)
                        }
                    }
                }
                sensorManager.registerListener(
                    callback, accelerometer,
                    SensorManager.SENSOR_DELAY_NORMAL
                )
            }
        }
    }

    private fun calculateStep(event: SensorEvent, onStep: () -> Unit) {

        mGravity.clear()
        mGravity.addAll(event.values.toList())
        // Shake detection
        val x = mGravity[0].toDouble()
        val y = mGravity[1].toDouble()
        val z = mGravity[2].toDouble()
        mAccelLast = mAccelCurrent
        mAccelCurrent = sqrt(x * x + y * y + z * z)
        val delta = mAccelCurrent - mAccelLast
        mAccel = mAccel * 0.9f + delta

        if (hitCount <= sampleSize) {
            hitCount++
            hitSum += abs(mAccel)
        } else {
            hitResult = hitSum / sampleSize
            if (hitResult > threshold) {
                onStep()
            }
            hitCount = 0
            hitSum = 0.0
            hitResult = 0.0
        }

    }


    fun stop() {
        try {
            sensorManager.unregisterListener(callback)
        } catch (ignore: Exception) {
        }
    }

*/
/*
    private fun calcStepsAsChunk(gaitResults: List<GaitResult>): Double {
        // this is where the data points would go
        val first2ByteList = gaitResults.map { sqrt(it.y.toDouble() * it.y + it.z.toDouble() * it.z) }



        // if set over 256 modifies code for better real world application
        // 119 optimal for lab based data collection
        var maxInStepThreshold = 119
        var predictedStepCount = 0.0
        var maxInStep = Double.NEGATIVE_INFINITY
        var walking = false
        var inStep = false
        var counted = false
        val threshold = 0.0

        for (point in first2ByteList) {
            if (point > threshold) {
                inStep = true
                if (point > maxInStep) {
                    maxInStep = point.toDouble()
                }
            } else {
                inStep = false
                maxInStep = Double.NEGATIVE_INFINITY
            }
            walking = maxInStep > maxInStepThreshold

            if (inStep != counted) {
                counted = inStep
                if (inStep && walking) {
                    predictedStepCount++
                }
            }
        }

        return predictedStepCount


    } */

    companion object {
        fun calcStepsAsChunk(device1DataBuffer: MutableList<GaitResult>): Double {
            val device1Data = device1DataBuffer.map { it.y.toDouble() }



            // if set over 256 modifies code for better real world application
            // 119 optimal more consistent walking pace
            var maxInStepThreshold = 5000
            var predictedStepCount = 0.0
            var maxInStep = Double.NEGATIVE_INFINITY
            var walking = false
            var inStep = false
            var counted = false
            val threshold = 0

            for (point in device1Data) {
                if (point > threshold) {
                    inStep = true
                    if (point > maxInStep) {
                        maxInStep = point.toDouble()
                    }
                } else {
                    inStep = false
                    maxInStep = Double.NEGATIVE_INFINITY
                }
                walking = maxInStep > maxInStepThreshold

                if (inStep != counted) {
                    counted = inStep
                    if (inStep && walking) {
                        predictedStepCount++
                    }
                }
            }


            return predictedStepCount
        }
    }


}