package com.gaitmonitoring.navigation

import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.gaitmonitoring.screens.home.HomeScreenUI
import com.gaitmonitoring.screens.login.LoginScreenUI
import com.gaitmonitoring.screens.profile.ProfileScreenUI
import com.gaitmonitoring.screens.scan.ScanScreenUI
import com.gaitmonitoring.screens.settings.SettingsScreenUI
import com.gaitmonitoring.screens.signup.SignUpScreenUI

/* This Composable function is the main navigation graph for the application. It sets up a navigation host for managing navigation between different screens. */
@Composable
fun MainNavigationGraph(
    modifier: Modifier,
    navHostController: NavHostController,
    startDestination: AppDestination,
    onBluetoothStateChanged:()->Unit
) {
// The NavHost composable that contains all the navigable screens.
    NavHost(
        modifier = modifier,
        navController = navHostController,
        startDestination = startDestination.route,
        enterTransition = { slideInVertically { -it }},
        exitTransition = { slideOutVertically { -it }},
    ) {


        composable(AppDestination.Login.route){
            LoginScreenUI(navHostController = navHostController)
        }

        composable(route = AppDestination.SignUp.route){
            SignUpScreenUI(navHostController = navHostController)
        }


        composable(AppDestination.Home.route){
            HomeScreenUI(navHostController = navHostController, onBluetoothStateChanged)
        }
        
        composable(AppDestination.Settings.route){
            SettingsScreenUI(navHostController = navHostController)
        }

        composable(AppDestination.Scan.route) {
            ScanScreenUI(navHostController = navHostController)
        }

        composable(AppDestination.Profile.route) {
            ProfileScreenUI(navHostController = navHostController)
        }





    }

}