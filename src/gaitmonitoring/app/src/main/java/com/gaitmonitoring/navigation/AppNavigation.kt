package com.gaitmonitoring.navigation

sealed class AppNavigation(val route:String) {

    data object Login:AppNavigation("login_screen")

    // Normal app navigation ..
    data object Home:AppNavigation("home_screen")
    data object Settings:AppNavigation("settings_screen")
    data object Alarms:AppNavigation("alarms_screen")
}