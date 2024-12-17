package com.gaitmonitoring

import android.app.Application
import com.gaitmonitoring.di.bluetoothModule
import com.gaitmonitoring.di.databaseModule
import com.gaitmonitoring.di.datastoreModule
import com.gaitmonitoring.di.firebaseModule
import com.gaitmonitoring.di.graphDataModule
import com.gaitmonitoring.di.notificationModule
import com.gaitmonitoring.di.sensorsModule
import com.gaitmonitoring.di.serviceModule
import com.gaitmonitoring.di.viewModelsModule
import com.gaitmonitoring.di.sharedPreferencesModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class BLEApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // initialize DI
        startKoin {
            androidContext(this@BLEApplication)
            modules(
                sensorsModule,
                firebaseModule,
                viewModelsModule,
                datastoreModule,
                graphDataModule,
                serviceModule,
                notificationModule,
                databaseModule,
                sharedPreferencesModule
            )
        }
    }
}