package com.gaitmonitoring.bluetooth.permissions

import android.Manifest
import com.gaitmonitoring.utils.isApi31
import com.gaitmonitoring.utils.isApi33

object PermissionsUtils {
    /**
     * PermissionsUtils object to handle dynamic permissions based on Android API level.
     * This utility class provides a list of necessary permissions that the app requires
     * to function correctly, adjusting for changes in permission requirements introduced
     * in different API versions.
     */
    val permissions = if(isApi31){
        buildList {

            add(Manifest.permission.BLUETOOTH_SCAN)
            add(Manifest.permission.BLUETOOTH_CONNECT)
            add(Manifest.permission.ACCESS_FINE_LOCATION)
            add(Manifest.permission.ACCESS_COARSE_LOCATION)
            if (isApi33){
                add(Manifest.permission.POST_NOTIFICATIONS)
            }
            add(Manifest.permission.ACTIVITY_RECOGNITION)
        }
    }else{
        listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }
}