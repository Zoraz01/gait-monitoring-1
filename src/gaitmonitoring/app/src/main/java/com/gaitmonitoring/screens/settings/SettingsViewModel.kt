package com.gaitmonitoring.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.gaitmonitoring.R
import com.gaitmonitoring.data.UiText
import com.gaitmonitoring.datastores.HomeDatastore
import com.gaitmonitoring.extensions.navigateToLoginScreen
import com.gaitmonitoring.firebase.authentication.FirebaseAuthentication
import com.gaitmonitoring.screens.settings.data.SettingItem
import com.gaitmonitoring.screens.settings.data.SettingItemAction
import com.gaitmonitoring.service.ServiceUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val firebaseAuthentication: FirebaseAuthentication,
    private val homeDatastore: HomeDatastore,
    private val serviceUtils: ServiceUtils
) : ViewModel() {

    private val _settingsItems = MutableStateFlow(value = emptyList<SettingItem>())
    val settingsItems = _settingsItems.asStateFlow()

    private val _toastMessage = MutableStateFlow(null as UiText?)
    val toastMessage = _toastMessage.asStateFlow()

    init {
        setSettingsItems()
    }

    private fun setSettingsItems() {
        _settingsItems.update {
            buildList {

                add(SettingItem(R.string.account_settings, SettingItemAction.AccountSettings))
                add(SettingItem(R.string.account_password, SettingItemAction.AccountPassword))
                add(SettingItem(R.string.sign_out, SettingItemAction.SignOut))
            }
        }
    }

    fun onItemClicked(
        navHostController: NavHostController,
        settingItem: SettingItem
    ) {

        when (settingItem.action) {
            SettingItemAction.SignOut -> {
                viewModelScope.launch {
                    firebaseAuthentication.signOut()
                    homeDatastore.clearData()
                    serviceUtils.stopBLEService()
                    navHostController.navigateToLoginScreen()
                }
            }

            else -> {
                val textRes = settingItem.textRes
                _toastMessage.update {
                    UiText.StringRes(textRes)
                }
            }
        }
    }

    fun clearToast() {
        _toastMessage.update { null }
    }
}