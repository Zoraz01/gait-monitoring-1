package com.gaitmonitoring.di

import com.gaitmonitoring.datastores.BLEConnectionDatastore
import com.gaitmonitoring.datastores.GaitDatastore
import com.gaitmonitoring.datastores.HomeDatastore
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val datastoreModule = module {

    singleOf(::HomeDatastore)
    singleOf(::GaitDatastore)
    singleOf(::BLEConnectionDatastore)

}