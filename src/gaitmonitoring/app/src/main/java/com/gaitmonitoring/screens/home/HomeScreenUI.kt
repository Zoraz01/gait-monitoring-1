@file:OptIn(
    ExperimentalPermissionsApi::class, ExperimentalPermissionsApi::class,
    ExperimentalPermissionsApi::class
)

package com.gaitmonitoring.screens.home

import android.bluetooth.BluetoothAdapter
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.gaitmonitoring.R
import com.gaitmonitoring.bluetooth.permissions.PermissionsUtils
import com.gaitmonitoring.bluetooth.permissions.SystemBroadcastReceiver
import com.gaitmonitoring.data.ConnectionState
import com.gaitmonitoring.navigation.AppDestination
import com.gaitmonitoring.ui.common.Space
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeScreenUI(
    navHostController: NavHostController,
    onBluetoothStateChanged: () -> Unit,
) {

    val viewModel: HomeScreenViewModel = koinViewModel()
    val myUser by viewModel.myUser.collectAsStateWithLifecycle()
    val graphData by viewModel.graphData.collectAsStateWithLifecycle()
    val stepsCount by viewModel.stepsCount.collectAsStateWithLifecycle()

    SystemBroadcastReceiver(systemAction = BluetoothAdapter.ACTION_STATE_CHANGED) { bluetoothState ->
        val action = bluetoothState?.action ?: return@SystemBroadcastReceiver
        if (action == BluetoothAdapter.ACTION_STATE_CHANGED) {
            onBluetoothStateChanged()
        }
    }

    val permissionState =
        rememberMultiplePermissionsState(permissions = PermissionsUtils.permissions)
    val lifecycleOwner = LocalLifecycleOwner.current
    val bleConnectionState = viewModel.connectionState


    DisposableEffect(
        key1 = lifecycleOwner,
        effect = {
            val observer = LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_START) {
                    permissionState.launchMultiplePermissionRequest()
                    if (permissionState.allPermissionsGranted && bleConnectionState == ConnectionState.Disconnected) {
                        viewModel.reconnect()
                    }
                }
                if (event == Lifecycle.Event.ON_STOP) {
                    // no disconnect  to keep it on background ..
//                    if (bleConnectionState == ConnectionState.Connected) {
//                        viewModel.disconnect()
//                    }
                }
            }
            lifecycleOwner.lifecycle.addObserver(observer)

            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        }
    )

    LaunchedEffect(key1 = permissionState.allPermissionsGranted) {
        if (permissionState.allPermissionsGranted) {
            if (bleConnectionState == ConnectionState.Uninitialized) {
                viewModel.initializeConnection()
            }
        }
    }


    Surface(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {


            Text(
                text = stringResource(id = R.string.welcome_text, myUser?.firstName ?: ""),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Space(height = 64.dp)


            Text(
                text = stringResource(id = R.string.senor_state_title),
                style = MaterialTheme.typography.titleLarge
            )
            Space(height = 16.dp)
            Text(
                text = stringResource(
                    when (bleConnectionState) {
                        ConnectionState.Connected -> R.string.connected
                        ConnectionState.OneDeviceConnected -> R.string.one_connected
                        ConnectionState.BothDevicesConnected -> R.string.two_connected
                        ConnectionState.Disconnected -> R.string.disconnected
                        ConnectionState.CurrentlyUninitialized -> R.string.initializing
                        ConnectionState.Uninitialized -> R.string.uninitialized

                    }
                ),
                style = MaterialTheme.typography.titleLarge,
                color = when (bleConnectionState) {
                    ConnectionState.Connected -> Color.Green
                    ConnectionState.OneDeviceConnected -> Color.Green
                    ConnectionState.BothDevicesConnected -> Color.Green
                    ConnectionState.Disconnected -> Color.Red
                    // Define a default color for other states
                    else -> Color.Black
                }
            )
            if (bleConnectionState == ConnectionState.Uninitialized) { // Display the button only if BLE is not connected
                Button(onClick = {
                    viewModel.initializeConnection()

                }) {
                    Text(text = "Connect to BLE Devices")
                }
            }

            Space(height = 32.dp)
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                StatusUI(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .padding(vertical = 8.dp),
                    bleConnectionState = bleConnectionState,
                    initializingMessage = viewModel.initializingMessage,
                    permissionState = permissionState,
                    errorMessage = viewModel.errorMessage,
                    onInitializeConnection = { viewModel.initializeConnection() }
                )

                Space(height = 16.dp)

                graphData?.let {

                    GraphUI(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(400.dp),
                        data = it
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(
                        16.dp,
                        alignment = Alignment.CenterHorizontally
                    )
                ) {
                    Text(
                        text = stringResource(id = R.string.steps_text),
                        style = MaterialTheme.typography.titleLarge
                    )

                    Crossfade(targetState = stepsCount, label = "") {

                        Text(
                            text = it.toString(),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }

            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun StatusUI(
    modifier: Modifier = Modifier,
    bleConnectionState: ConnectionState,
    initializingMessage: String?,
    permissionState: MultiplePermissionsState,
    onInitializeConnection: () -> Unit,
    errorMessage: String?
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {when (bleConnectionState) {
        ConnectionState.CurrentlyUninitialized -> {
            CircularProgressIndicator()
            initializingMessage?.let {
                Text(text = initializingMessage)
            }
        }
        ConnectionState.Disconnected, ConnectionState.Uninitialized -> {
            Button(onClick = { onInitializeConnection() }) {
                Text(text = "Initialize BLE Device")
            }
        }
        else -> {
            // No action required for other states
        }
    }

    if (!permissionState.allPermissionsGranted) {
        Text(
            text = "Go to the app setting and allow the missing permissions.",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(10.dp),
            textAlign = TextAlign.Center
        )
    } else if (errorMessage != null) {
        Text(text = errorMessage)
        Button(onClick = { onInitializeConnection() }) {
            Text(text = "Try again")
        }
        }
    }
}

