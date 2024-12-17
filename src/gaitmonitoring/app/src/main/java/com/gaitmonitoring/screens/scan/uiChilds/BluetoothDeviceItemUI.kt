package com.gaitmonitoring.screens.scan.uiChilds

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gaitmonitoring.screens.scan.models.BluetoothDeviceItem

@Composable
fun BluetoothDeviceItemUI(
    modifier: Modifier = Modifier,
    deviceItem: BluetoothDeviceItem,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier.clickable { onClick.invoke() },
        shadowElevation = 2.dp,
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = deviceItem.deviceName,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = deviceItem.deviceAddress,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}
