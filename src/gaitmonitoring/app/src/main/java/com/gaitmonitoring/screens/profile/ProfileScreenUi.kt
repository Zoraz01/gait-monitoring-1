package com.gaitmonitoring.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.gaitmonitoring.R
import com.gaitmonitoring.ui.common.Space
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProfileScreenUI(
    navHostController: NavHostController,
    viewModel: ProfileScreenViewModel = koinViewModel()
) {
    val user by viewModel.user.collectAsStateWithLifecycle()

    Surface(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(id = R.string.welcome_text, user?.fullName ?: ""),
                style = MaterialTheme.typography.titleLarge
            )

            Space(height = 32.dp)

            // Placeholder for Total Steps
            Text(
                text = "Total Steps: Coming Soon!",
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}
