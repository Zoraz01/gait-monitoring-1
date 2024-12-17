package com.gaitmonitoring.screens.alarms

import android.app.Application
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.AndroidViewModel
import com.gaitmonitoring.R
import com.gaitmonitoring.screens.alarms.models.AlarmField
import com.gaitmonitoring.screens.models.OnOffState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AlarmViewModel(private val app: Application) : AndroidViewModel(app) {

    private val _alarmFieldList = MutableStateFlow(value = emptyList<AlarmField>())
    val alarmField = _alarmFieldList.asStateFlow()

    init {

        setAlarmFields()
    }

    private fun setAlarmFields() {
        _alarmFieldList.update {
            buildList {
                add(
                    AlarmField(
                        text = app.applicationContext.getString(R.string.fall_detected),
                        state = OnOffState.ON,
                    )
                )

                add(
                    AlarmField(
                        text = app.applicationContext.getString(R.string.signal_loss),
                        state = OnOffState.ON,
                    )
                )
                add(
                    AlarmField(
                        text = app.applicationContext.getString(R.string.sound_mode),
                        subText = app.applicationContext.getString(R.string.vibrate_as_text),

                        )
                )

            }
        }
    }

}