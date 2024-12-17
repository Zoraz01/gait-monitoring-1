package com.gaitmonitoring.navigationDrawer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gaitmonitoring.R
import com.gaitmonitoring.data.MyUser
import com.gaitmonitoring.datastores.HomeDatastore
import com.gaitmonitoring.navigation.AppDestination
import com.gaitmonitoring.navigationDrawer.models.DrawerItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NavDrawerViewModel(
    private val homeDatastore: HomeDatastore,
    ) : ViewModel() {


    private val _drawerItems = MutableStateFlow(value = emptyList<DrawerItem>())
    val drawerItems = _drawerItems.asStateFlow()

    private val _myUser = MutableStateFlow(null as MyUser?)
    val myUser = _myUser.asStateFlow()

    init {
        loadUserFromPrefs()
        loadDrawerItems()
    }

    private fun loadUserFromPrefs() {
        viewModelScope.launch {
            homeDatastore.myUser.collectLatest { user ->
                _myUser.update { user }
            }
        }
    }

    private fun loadDrawerItems() {
        _drawerItems.update {
                buildList {
                    add(
                        DrawerItem(
                            textRes = R.string.home_As_text, AppDestination.Home
                        )
                    )
                    add(
                        DrawerItem(
                            textRes = R.string.settings_as_text,
                            navigationDestination = AppDestination.Settings
                        )
                    )
                }
            }
    }

}