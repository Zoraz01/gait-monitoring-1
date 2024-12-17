package com.gaitmonitoring.di

import com.gaitmonitoring.notification.NotificationUtils
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val notificationModule = module {
    singleOf(::NotificationUtils)

}