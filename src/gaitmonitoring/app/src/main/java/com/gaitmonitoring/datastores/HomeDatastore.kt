package com.gaitmonitoring.datastores

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.gaitmonitoring.data.MyUser
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.map

private val Context.homeDatastore: DataStore<Preferences> by preferencesDataStore(name = "home_data_store")

class HomeDatastore(private val context: Context) {

    private val gson by lazy { Gson() }

    private val userFirestoreDocumentIdKey = stringPreferencesKey("user_firestore_document_id_key")
    private val myUserObjectKey = stringPreferencesKey("my_user_object_key")

    val myUser = context.homeDatastore.data.map { prefs ->
        val asString = prefs[myUserObjectKey]
        asString?.let {
            val type = object : TypeToken<MyUser>() {}.type
            val user: MyUser = gson.fromJson(it, type)

            user
        }
    }

    suspend fun setCurrentUser(myUser: MyUser) {

        context.homeDatastore.edit { prefs ->
            val asString = gson.toJson(myUser)
            prefs[myUserObjectKey] = asString
        }
    }

    val userFirestoreDocumentId = context.homeDatastore.data.map { prefs ->
        prefs[userFirestoreDocumentIdKey]
    }

    suspend fun setUserFirestoreDocumentId(documentId: String) {
        context.homeDatastore.edit { it[userFirestoreDocumentIdKey] = documentId }
    }

    suspend fun clearData() {
        context.homeDatastore.edit {
            it.remove(userFirestoreDocumentIdKey)
            it.remove(myUserObjectKey)
        }
    }

}