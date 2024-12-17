package com.gaitmonitoring.firebase.firestore

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object FirestoreConstants {


    private const val USERS_COLLECTION_NAME = "users"
    val usersCollection get() = Firebase.firestore.collection(USERS_COLLECTION_NAME)


}