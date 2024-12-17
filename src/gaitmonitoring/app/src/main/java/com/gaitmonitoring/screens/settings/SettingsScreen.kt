package com.gaitmonitoring.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.gaitmonitoring.R
import com.gaitmonitoring.extensions.toastShort
import com.gaitmonitoring.navigationDrawer.MyModalNavDrawer
import com.gaitmonitoring.screens.settings.uiChilds.SettingItemUI

@Composable
fun SettingsScreen(navHostController: NavHostController) {

    val context = LocalContext.current
    val viewModel: SettingsViewModel = viewModel()
    val items by viewModel.settingsItems.collectAsStateWithLifecycle()

    MyModalNavDrawer(
        navHostController = navHostController,
        topAppBarTitle = stringResource(id = R.string.settings_as_text)
    ) {

        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {

                items.fastForEach { item ->
                    SettingItemUI(
                        settingItem = item,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        onClick = {
                            context.toastShort(msg = item.textRes.toString())
                        }
                    )

                }


            }
        }
    }
}