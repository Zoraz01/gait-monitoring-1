package com.gaitmonitoring.screens.login

import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.gaitmonitoring.R
import com.gaitmonitoring.data.UiText
import com.gaitmonitoring.datastores.HomeDatastore
import com.gaitmonitoring.extensions.navigateToHomeScreen
import com.gaitmonitoring.firebase.TaskCallback
import com.gaitmonitoring.firebase.authentication.FirebaseAuthentication
import com.gaitmonitoring.navigationDrawer.SnackBarStateHolder
import com.gaitmonitoring.navigationDrawer.models.SnackBarType
import com.gaitmonitoring.screens.data.LoginField
import com.gaitmonitoring.screens.data.LoginFieldType
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginScreenViewModel(
    private val firebaseAuthentication: FirebaseAuthentication,
    private val homeDatastore: HomeDatastore
) : ViewModel() {

    // login fields ..
    private val _fields = MutableStateFlow(emptyList<LoginField>())
    val fields = _fields.asStateFlow()

    private val _isLoading = MutableStateFlow(value = false)
    val isLoading = _isLoading.asStateFlow()

    init {
        setFields()
    }


    private fun setFields() {
        _fields.update {
            buildList {
                add(
                    LoginField(
                        value = "",
                        type = LoginFieldType.Email,
                        labelUiText = UiText.StringRes(res = R.string.email_as_text),
                        keyboardType = KeyboardType.Email,
                        validityText = UiText.StringRes(R.string.email_validity_text)
                    )
                )

                add(
                    LoginField(
                        value = "", type = LoginFieldType.Password,
                        labelUiText = UiText.StringRes(R.string.password_as_text),
                        keyboardType = KeyboardType.Password,
                        validityText = UiText.StringRes(R.string.password_validity_text)
                    )
                )
            }
        }
    }

    fun updateField(fieldType: LoginFieldType, newValue: String) {
        _fields.update { list ->
            LoginField.updateField(newValue = newValue, fieldType = fieldType, list = list)
        }
    }


    fun login(navHostController: NavHostController) {

        _isLoading.update { true }

        val email = _fields.value.find { it.type == LoginFieldType.Email }?.value ?: ""
        val password = _fields.value.find { it.type == LoginFieldType.Password }?.value ?: ""

        firebaseAuthentication.signIn(email = email, password = password) { result ->
            when (result) {
                is TaskCallback.OnFailure -> {
                    _isLoading.update { false }
                    SnackBarStateHolder.showSnackBar(
                        uiText = UiText.StringRes(
                            R.string.error_sign_in,
                            result.exception.message ?: ""
                        ),
                        type = SnackBarType.Error
                    )
                }

                is TaskCallback.OnSuccess -> {
                    val firebaseUser = result.data
                    // save user document id
                    finalizeData(firebaseUser)
                    navHostController.navigateToHomeScreen()
                }
            }
        }

    }

    private fun mockLoginSuccess(navHostController: NavHostController) {
        // Mock user data or any necessary setup after login
        // ...

        // Navigate to the home screen

        val email = _fields.value.find { it.type == LoginFieldType.Email }?.value ?: ""
        val password = _fields.value.find { it.type == LoginFieldType.Password }?.value ?: ""
        navHostController.navigateToHomeScreen()
    }

    private fun finalizeData(firebaseUser:FirebaseUser){
        viewModelScope.launch {
            homeDatastore.setUserFirestoreDocumentId(firebaseUser.uid)
            _isLoading.update { false }
            _fields.update { list ->
                list.map { it.copy(value = "") }
            }
        }
    }
}