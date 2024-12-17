package com.gaitmonitoring.navigation

sealed class AppDestination(val route: String) {

    data object Login : AppDestination("login_screen")
    data object SignUp:AppDestination("sign_up_screen")

    // Normal app navigation ..
    data object Home : AppDestination("home_screen")
    data object Settings : AppDestination("settings_screen")   // title settings_as_text
    data object Alarms : AppDestination("alarms_screen")

    data object Scan : AppDestination("scan_screen")     // title R.string.scan_as_text
    data object Profile : AppDestination("profile_screen")   // title "Profile"


    companion object {

        private val screensWithoutTopAppbar:List<String>
            get() {
                return listOf(Login.route, SignUp.route, Profile.route)
            }

        // hide top app bar if sign in or sign up ..
        fun showTopAppBar(route: String): Boolean {
            return route !in screensWithoutTopAppbar
        }
    }
}