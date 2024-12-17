package com.gaitmonitoring.screens.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.gaitmonitoring.R

@Composable
fun LogoUI(
    modifier: Modifier = Modifier
) {
    val isDarkTheme = isSystemInDarkTheme()
    val logoImage = remember(isDarkTheme) {
        if (isDarkTheme) {
            R.drawable.gait_analysis_dark_logo
        } else {
            R.drawable.gait_analysis_logo
        }

    }

    // Title Text
    Image(
        painter = painterResource(id = logoImage),
        contentDescription = "Gait Analysis Logo",
        modifier = modifier
    )


}