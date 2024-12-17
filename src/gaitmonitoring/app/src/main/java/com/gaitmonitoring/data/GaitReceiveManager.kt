package com.gaitmonitoring.data

import com.gaitmonitoring.utils.Resource
import kotlinx.coroutines.flow.MutableSharedFlow

interface GaitReceiveManager {

    val data: MutableSharedFlow<Resource<GaitResult>>

    fun reconnect()

    fun disconnect()

    fun startReceiving()

    fun closeConnection()

}