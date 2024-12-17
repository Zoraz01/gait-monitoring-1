package com.gaitmonitoring.domain

import android.content.Context
import android.util.Log
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

class GraphDataBuilder(private val context: Context) {



    /** Function that takes the Z data and graphs it */
    fun getGraphData(zDataDevice1: List<Float>, zDataDevice2: List<Float>): LineData {
        Log.d("GaitMonitoringApp", "Graphing Z Data: Device1 - $zDataDevice1, Device2 - $zDataDevice2")
        val zAxisDataSetDevice1 = createDataSet(zDataDevice1, "Z Axis Data Device 1", android.graphics.Color.parseColor("#F8F8F8"))
        val zAxisDataSetDevice2 = createDataSet(zDataDevice2, "Z Axis Data Device 2", android.graphics.Color.parseColor("#FF0000")) // Different color for second device
        return LineData(zAxisDataSetDevice1, zAxisDataSetDevice2)
    }

    /** Where the data set and style is applied to the lines */
    private fun createDataSet(data: List<Float>, label: String, color: Int): LineDataSet {
        val entries = data.mapIndexed { index, value -> Entry(index.toFloat(), value) }
        return LineDataSet(entries, label).apply {
            lineWidth = 2f
            this.color = color
            setDrawValues(false)
            // Other styling options...
        }
    }



}