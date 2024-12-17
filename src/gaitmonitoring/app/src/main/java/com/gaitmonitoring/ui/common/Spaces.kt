package com.gaitmonitoring.ui.common

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


@Composable
fun ColumnScope.Space(height: Dp, modifier: Modifier = Modifier) {
    HorizontalDivider(
        color = Color.Transparent,
        modifier = modifier
            .fillMaxWidth()
            .align(Alignment.CenterHorizontally),
        thickness = height
    )
}


@Composable
fun RowScope.Space(width: Dp, modifier: Modifier = Modifier) {
    VerticalDivider(
        color = Color.Transparent,
        modifier = modifier
            .then(Modifier.height(1.dp))
            .align(Alignment.CenterVertically),
        thickness = width
    )
}