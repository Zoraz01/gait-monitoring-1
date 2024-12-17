package com.gaitmonitoring.screens.settings.uiChilds

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.gaitmonitoring.screens.settings.data.SettingItem

@Composable
fun SettingItemUI(
    modifier: Modifier = Modifier,
    settingItem: SettingItem,
    onClick: () -> Unit
) {

    Surface(
        modifier = modifier.clickable { onClick.invoke() },
        shadowElevation = 2.dp,
        shape = RoundedCornerShape(8.dp)
    ) {

        Text(
            text = stringResource(id = settingItem.textRes),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(vertical = 16.dp, horizontal = 16.dp)
        )
    }

}