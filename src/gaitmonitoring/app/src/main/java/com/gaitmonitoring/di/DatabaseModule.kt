package com.gaitmonitoring.di

import android.content.Context
import com.gaitmonitoring.data.database.AppDatabase
import com.gaitmonitoring.data.database.StepDao
import com.gaitmonitoring.data.stepsRepository.IStepsRepository
import com.gaitmonitoring.data.stepsRepository.OfflineStepsRepository
import org.koin.dsl.bind
import org.koin.dsl.module

val databaseModule = module {
    single { getDatabase(get()) }
    single { getStepsDao(get()) } bind StepDao::class
    single { OfflineStepsRepository(get()) }.bind<IStepsRepository>()
}

private fun getDatabase(context: Context) = AppDatabase.getDatabase(context)
private fun getStepsDao(appDatabase: AppDatabase) = appDatabase.getStepsDAO()
