package com.gaitmonitoring.firebase.authentication

import com.gaitmonitoring.firebase.TaskCallback
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

// Class that encapsulates Firebase Authentication functionality.
class FirebaseAuthentication {

    // Property to get the currently logged-in Firebase user.
    val currentUser get() = Firebase.auth.currentUser


    // Method to sign in a user using email and password. Callbacks handle success or failure.
    fun signIn(
        email: String,
        password: String,
        callback: (TaskCallback<FirebaseUser>) -> Unit
    ) {
        Firebase.auth
            .signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->


                if (task.isSuccessful) {
                    callback(TaskCallback.OnSuccess(task.result.user!!))
                }
            }.addOnFailureListener {
                callback(
                    TaskCallback.OnFailure(it)
                )
            }

    }

    // Method to register a new user with email and password. Callbacks handle success or failure.
    fun signUp(email: String, password: String, callback: (TaskCallback<FirebaseUser>) -> Unit) {
        Firebase.auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = task.result.user!!
                    callback(TaskCallback.OnSuccess(user))
                }
            }.addOnFailureListener {
                callback(TaskCallback.OnFailure(it))
            }
    }

    // Method to delete the current Firebase user from authentication records.
    fun deleteUser(firebaseUser: FirebaseUser) {
        firebaseUser.delete()
    }

    // Method to sign out the current user from Firebase authentication.
    fun signOut() {
        Firebase.auth.signOut()
    }


}