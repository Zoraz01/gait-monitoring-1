package com.gaitmonitoring.screens.scan

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.gaitmonitoring.screens.scan.uiChilds.BluetoothDeviceItemUI

@Composable
fun ScanScreenUI(navHostController: NavHostController) {
    val viewModel: ScanScreenViewModel = viewModel()
    val bleDevices by viewModel.bleDevices.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            bleDevices.forEach { device ->
                BluetoothDeviceItemUI(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    deviceItem = device,
                    onClick = {
                        // Handle the click event, e.g., initiate a connection
                    }
                )
            }
        }
    }
}
