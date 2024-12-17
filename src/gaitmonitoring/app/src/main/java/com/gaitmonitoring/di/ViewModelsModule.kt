package com.gaitmonitoring.di

import com.gaitmonitoring.MainActivityViewModel
import com.gaitmonitoring.navigationDrawer.NavDrawerViewModel
import com.gaitmonitoring.screens.home.HomeScreenViewModel
import com.gaitmonitoring.screens.login.LoginScreenViewModel
import com.gaitmonitoring.screens.profile.ProfileScreenViewModel
import com.gaitmonitoring.screens.settings.SettingsViewModel
import com.gaitmonitoring.screens.signup.SignUpViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val viewModelsModule = module {
    viewModelOf(::LoginScreenViewModel)
    viewModelOf(::SignUpViewModel)
    viewModelOf(::HomeScreenViewModel)
    viewModelOf(::ProfileScreenViewModel)
    viewModelOf(::SettingsViewModel)
    viewModelOf(::NavDrawerViewModel)
    viewModelOf(::MainActivityViewModel)

}