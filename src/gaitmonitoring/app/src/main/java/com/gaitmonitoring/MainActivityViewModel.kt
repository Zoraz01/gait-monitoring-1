package com.gaitmonitoring

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gaitmonitoring.datastores.HomeDatastore
import com.gaitmonitoring.firebase.authentication.FirebaseAuthentication
import com.gaitmonitoring.firebase.firestore.FirestoreUsersActions
import com.gaitmonitoring.navigation.AppDestination
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainActivityViewModel(
    private val firebaseAuthentication: FirebaseAuthentication,
    private val firestoreUsersActions: FirestoreUsersActions,
    private val homeDatastore: HomeDatastore
) : ViewModel() {

    // set the start destination according to user authentication state ..
    private val _startDestination = MutableStateFlow(null as AppDestination?)
    val startDestination = _startDestination.asStateFlow()

    init {
        setStartDestination()
        observeUserDocument()
    }

    /* observe any change in user document and in user data to save it in local storage */
    private fun observeUserDocument() {
        viewModelScope.launch {
            homeDatastore.userFirestoreDocumentId.collectLatest { id ->

                id?.let {
                    firestoreUsersActions.observeUser(id) { user ->
                        viewModelScope.launch {

                            homeDatastore.setCurrentUser(user)
                        }
                    }
                }
            }
        }
    }

    private fun setStartDestination() {
        if (firebaseAuthentication.currentUser == null) {
            // set login as start destination
            _startDestination.update { AppDestination.Login }
        } else {
            _startDestination.update { AppDestination.Home }
        }
    }
}