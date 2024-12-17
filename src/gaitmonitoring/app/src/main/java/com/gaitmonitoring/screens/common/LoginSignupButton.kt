package com.gaitmonitoring.screens.common

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun LoginSignupButton(
    modifier: Modifier = Modifier,
    isLoading:Boolean,
    onClick: () -> Unit,
    isEnabled:Boolean,
    text:String
) {

    Crossfade(
        targetState = isLoading,
        label = "",
        modifier = modifier
    ) {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            if (it) {
                CircularProgressIndicator()
            } else {
                Button(
                    onClick = onClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateContentSize(),
                    enabled = isEnabled
                ) {
                    Text(
                        text = text,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            }
        }


    }


}