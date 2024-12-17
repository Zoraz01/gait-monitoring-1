package com.gaitmonitoring.screens.alarms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.gaitmonitoring.R
import com.gaitmonitoring.navigationDrawer.MyModalNavDrawer
import com.gaitmonitoring.screens.alarms.uiChilds.AlarmFieldUI

@Composable
fun AlarmsScreen(navHostController: NavHostController) {

    val viewModel: AlarmViewModel = viewModel()
    val alarmFields by viewModel.alarmField.collectAsStateWithLifecycle()

    MyModalNavDrawer(
        navHostController = navHostController,
        topAppBarTitle = stringResource(id = R.string.alarms_as_text)
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

                alarmFields.fastForEach { item ->
                    AlarmFieldUI(
                        alarmField = item, modifier = Modifier
                            .padding(horizontal = 24.dp)
                    )
                }

            }
        }
    }
}