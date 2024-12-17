package com.gaitmonitoring.di

import com.gaitmonitoring.firebase.authentication.FirebaseAuthentication
import com.gaitmonitoring.firebase.firestore.FirestoreSensorsDataActions
import com.gaitmonitoring.firebase.firestore.FirestoreUsersActions
import com.gaitmonitoring.firebase.storage.MyFirebaseStorage
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module


val firebaseModule = module {
    singleOf(::FirebaseAuthentication)
    singleOf(::FirestoreUsersActions)
    factoryOf(::MyFirebaseStorage)
    singleOf(::FirestoreSensorsDataActions)
}
