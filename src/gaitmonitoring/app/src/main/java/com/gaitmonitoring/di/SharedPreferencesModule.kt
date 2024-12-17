package com.gaitmonitoring.di

import android.content.Context
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val sharedPreferencesModule = module {
    single {
        androidContext().getSharedPreferences("YourPreferenceName", Context.MODE_PRIVATE)
    }
}