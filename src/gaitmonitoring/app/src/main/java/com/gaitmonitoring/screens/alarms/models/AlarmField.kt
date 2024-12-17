package com.gaitmonitoring.screens.alarms.models

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.gaitmonitoring.screens.models.OnOffState

data class AlarmField(
    val text: String,
    val state: OnOffState? = null,
    val subText:String? = null,
)