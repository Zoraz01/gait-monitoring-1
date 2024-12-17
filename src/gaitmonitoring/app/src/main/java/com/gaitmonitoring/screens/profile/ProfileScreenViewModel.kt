package com.gaitmonitoring.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gaitmonitoring.data.MyUser
import com.gaitmonitoring.datastores.HomeDatastore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileScreenViewModel (
    private val homeDatastore: HomeDatastore
) : ViewModel() {

    private val _user = MutableStateFlow(null as MyUser?)
    val user = _user.asStateFlow()


    init {
        updateUserName()
    }

    private fun updateUserName() {
        viewModelScope.launch {
            homeDatastore.myUser.collectLatest { user ->
                _user.update { user }
            }
        }
    }
}
