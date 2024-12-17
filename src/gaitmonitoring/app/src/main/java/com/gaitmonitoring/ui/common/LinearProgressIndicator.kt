package com.gaitmonitoring.ui.common


import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun ProgressDialog(
    portion: Float,
    text: String
) {

    Dialog(
        onDismissRequest = { },
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        Surface(
            tonalElevation = 6.dp, shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth()
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                ProgressBarAnimated(
                    portion = portion,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                )

                Text(text = text, style = MaterialTheme.typography.bodyMedium)

            }
        }
    }
}

@Composable
fun ProgressBarAnimated(modifier: Modifier = Modifier, portion: Float) {

    var progress by remember { mutableStateOf(0f) }
    val progressAnimDuration = 1500
    val progressAnimation by animateFloatAsState(
        targetValue = portion,
        animationSpec = tween(durationMillis = progressAnimDuration, easing = FastOutSlowInEasing),
        label = ""
    )

    LaunchedEffect(portion) {
        progress = portion
    }


    if (progressAnimation == 0f) {

        LinearProgressIndicator(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
        )
    } else {
        LinearProgressIndicator(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp)), // Rounded edges
            progress = { progressAnimation }
        )
    }

}