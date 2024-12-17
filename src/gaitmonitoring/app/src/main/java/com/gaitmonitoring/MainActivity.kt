package com.gaitmonitoring

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.gaitmonitoring.navigation.MainNavigationGraph
import com.gaitmonitoring.navigationDrawer.MyModalNavDrawer
import com.gaitmonitoring.ui.theme.GaitMonitoringTheme
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {


    private val bluetoothAdapter: BluetoothAdapter by inject()
    private val mainActivityViewModel: MainActivityViewModel by viewModel()


    override fun onCreate(savedInstanceState: Bundle?) {

        installSplashScreen().setKeepOnScreenCondition {
            // keep if true
            mainActivityViewModel.startDestination.value == null
        }


        super.onCreate(savedInstanceState)



        setContent {

            GaitMonitoringTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navHostController = rememberNavController()
                    val startDestination by mainActivityViewModel.startDestination.collectAsStateWithLifecycle()
                    startDestination?.let { destination ->

                        MyModalNavDrawer(navHostController = navHostController) { paddingValues ->
                            MainNavigationGraph(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(paddingValues),
                                navHostController = navHostController,
                                startDestination = destination,
                                onBluetoothStateChanged = {
                                    showBluetoothDialog()
                                }
                            )
                        }
                    }


                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        showBluetoothDialog()
    }

    private var isBluetoothDialogAlreadyShown = false
    private fun showBluetoothDialog() {
        if (!bluetoothAdapter.isEnabled) {
            if (!isBluetoothDialogAlreadyShown) {
                val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startBluetoothIntentForResult.launch(enableBluetoothIntent)
                isBluetoothDialogAlreadyShown = true
            }
        }
    }

    private val startBluetoothIntentForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            isBluetoothDialogAlreadyShown = false
            if (result.resultCode != Activity.RESULT_OK) {
                showBluetoothDialog()
            }
        }


}