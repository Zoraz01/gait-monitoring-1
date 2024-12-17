package com.gaitmonitoring.di

import com.gaitmonitoring.service.ServiceUtils
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val serviceModule = module {
    singleOf(::ServiceUtils)
}