package com.gaitmonitoring.screens.settings.data

import androidx.annotation.StringRes

data class SettingItem (
    @StringRes val textRes:Int,
    val action: SettingItemAction
    )