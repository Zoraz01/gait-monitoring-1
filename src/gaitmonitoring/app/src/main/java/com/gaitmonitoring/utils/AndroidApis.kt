package com.gaitmonitoring.utils

import android.os.Build


val isApi29 get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
val isApi33 get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
val isApi26 get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
val isApi31 get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S