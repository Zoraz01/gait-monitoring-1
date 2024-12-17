package com.gaitmonitoring.extensions

import androidx.navigation.NavHostController
import com.gaitmonitoring.navigation.AppDestination

fun NavHostController.navigateToSignUp() {
    navigate(AppDestination.SignUp.route) {
        launchSingleTop = true
    }
}

fun NavHostController.navigateToHomeScreen() {
    navigate(route = AppDestination.Home.route) {
        launchSingleTop = true
        popUpTo(0)
    }
}


fun NavHostController.navigateToLoginScreen() {
    this.navigate(route = AppDestination.Login.route) {
        launchSingleTop = true
        popUpTo(0)
    }
}
