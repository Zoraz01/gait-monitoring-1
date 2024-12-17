package com.gaitmonitoring.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.gaitmonitoring.extensions.toastShort
import com.gaitmonitoring.screens.settings.uiChilds.SettingItemUI
import com.gaitmonitoring.ui.common.Space
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingsScreenUI(navHostController: NavHostController) {

    val context = LocalContext.current
    val viewModel: SettingsViewModel = koinViewModel()
    val items by viewModel.settingsItems.collectAsStateWithLifecycle()
    val toastMessage by viewModel.toastMessage.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = toastMessage, block = {
        toastMessage?.let {
            context.toastShort(msg = it.getValue(context))
            viewModel.clearToast()
        }
    })

    Surface(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {

            Space(height = 24.dp)

            LazyColumn(
                content = {
                    items(items) { item ->
                        SettingItemUI(
                            settingItem = item,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp),
                            onClick = {
                                viewModel.onItemClicked(navHostController, item)
                            }
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                contentPadding = PaddingValues(all = 16.dp)
            )


        }
    }
}
