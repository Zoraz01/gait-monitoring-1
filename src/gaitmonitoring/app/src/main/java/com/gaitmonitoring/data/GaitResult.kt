package com.gaitmonitoring.data

data class GaitResult(
    val y: Int,
    val z: Int,
    val x: Int,
    val xA: Int,
    val timestamp: String,
    val connectionState: ConnectionState,
    val deviceName: String // Indicate the source device
)