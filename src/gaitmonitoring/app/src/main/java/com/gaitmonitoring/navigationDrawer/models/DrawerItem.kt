package com.gaitmonitoring.navigationDrawer.models

import androidx.annotation.StringRes
import com.gaitmonitoring.navigation.AppDestination

data class DrawerItem (@StringRes val textRes:Int, val navigationDestination:AppDestination? )