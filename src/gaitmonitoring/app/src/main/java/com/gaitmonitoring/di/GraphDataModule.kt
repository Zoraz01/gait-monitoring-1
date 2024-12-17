package com.gaitmonitoring.di

import com.gaitmonitoring.domain.GraphDataBuilder
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val graphDataModule = module {
    singleOf(::GraphDataBuilder)
}