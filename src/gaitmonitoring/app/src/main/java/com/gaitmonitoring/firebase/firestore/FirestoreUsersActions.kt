package com.gaitmonitoring.firebase.firestore

import com.gaitmonitoring.data.MyUser
import com.gaitmonitoring.firebase.TaskCallback
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class FirestoreUsersActions {

    private val gson by lazy { Gson() }

    fun addUser(myUser: MyUser, callback: (TaskCallback<String>) -> Unit) {
        val document = FirestoreConstants.usersCollection.document(myUser.uid)
        document.set(myUser).addOnSuccessListener {
            callback(TaskCallback.OnSuccess(document.id))
        }.addOnFailureListener {
            callback(TaskCallback.OnFailure(it))
        }

    }

    fun getUser(uid: String, callback: (TaskCallback<MyUser>) -> Unit) {
        FirestoreConstants.usersCollection
            .document(uid)
            .get()
            .addOnSuccessListener {
                val data = it.data
                data?.let {
                    val myUser = getUserFromFirestoreDataMap(data)
                    callback(TaskCallback.OnSuccess(myUser))
                } ?: run {
                    callback(
                        TaskCallback.OnFailure(
                            RuntimeException("Can not get user data map from firestore")
                        )
                    )
                }
            }.addOnFailureListener {
                callback(TaskCallback.OnFailure(it))

            }
    }


    private fun getUserFromFirestoreDataMap(data: MutableMap<String, Any>): MyUser {
        val type = object : TypeToken<MyUser>() {}.type
        val asJson = gson.toJson(data)
        return gson.fromJson(asJson, type)
    }

    fun observeUser(id: String, callback: (MyUser) -> Unit) {

        FirestoreConstants.usersCollection.document(id).addSnapshotListener { value, error ->
            if (error == null) {
                val data = value?.data
                data?.let {
                    val user = getUserFromFirestoreDataMap(data)
                    callback(user)
                }
            }
        }
    }
}