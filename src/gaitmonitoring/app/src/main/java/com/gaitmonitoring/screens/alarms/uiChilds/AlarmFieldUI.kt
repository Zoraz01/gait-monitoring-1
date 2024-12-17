package com.gaitmonitoring.screens.alarms.uiChilds

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gaitmonitoring.screens.alarms.models.AlarmField
import com.gaitmonitoring.screens.models.OnOffState

@Composable
fun AlarmFieldUI(modifier: Modifier = Modifier, alarmField: AlarmField) {

    Surface(
        modifier = modifier,
        shadowElevation = 2.dp,
        shape = RoundedCornerShape(8.dp)
    ) {

        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp, horizontal = 16.dp)) {

            Text(
                text = alarmField.text,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )

            alarmField.subText?.let {
                Text(
                    text = alarmField.subText,
                    style = MaterialTheme.typography.bodyLarge,
                )
            }

            alarmField.state?.let {

                Text(
                    text = alarmField.state.name,
                    style = MaterialTheme.typography.bodyLarge,
                    color = when(alarmField.state){
                        OnOffState.ON -> MaterialTheme.colorScheme.primary
                        OnOffState.OFF -> MaterialTheme.colorScheme.error
                    }
                )

            }

        }
    }

}