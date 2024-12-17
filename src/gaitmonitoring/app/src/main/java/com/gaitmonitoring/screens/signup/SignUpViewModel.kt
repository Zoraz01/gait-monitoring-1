package com.gaitmonitoring.screens.signup

import android.graphics.Bitmap
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.gaitmonitoring.R
import com.gaitmonitoring.data.MyUser
import com.gaitmonitoring.data.UiText
import com.gaitmonitoring.datastores.HomeDatastore
import com.gaitmonitoring.extensions.navigateToHomeScreen
import com.gaitmonitoring.firebase.TaskCallback
import com.gaitmonitoring.firebase.authentication.FirebaseAuthentication
import com.gaitmonitoring.firebase.firestore.FirestoreUsersActions
import com.gaitmonitoring.firebase.storage.MyFirebaseStorage
import com.gaitmonitoring.firebase.storage.UploadMediaCallback
import com.gaitmonitoring.navigationDrawer.SnackBarStateHolder
import com.gaitmonitoring.navigationDrawer.models.SnackBarType
import com.gaitmonitoring.screens.data.LoginField
import com.gaitmonitoring.screens.data.LoginFieldType
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class SignUpViewModel(
    private val firebaseAuthentication: FirebaseAuthentication,
    private val firestoreUsersActions: FirestoreUsersActions,
    private val myFirebaseStorage: MyFirebaseStorage,
    private val homeDatastore: HomeDatastore
) : ViewModel() {

    // states
    private val _fields = MutableStateFlow(value = emptyList<LoginField>())
    val fields = _fields.asStateFlow()

    private val _userProfileImageBitmap = MutableStateFlow(null as Bitmap?)
    val userProfileImageBitmap = _userProfileImageBitmap.asStateFlow()


    private val _isProcessingSignUp = MutableStateFlow(false)
    val isProcessingSignUp = _isProcessingSignUp.asStateFlow()

    private val _uploadProfilePictureProgress = MutableStateFlow(null as Float?)
    val uploadProfilePictureProgress = _uploadProfilePictureProgress.asStateFlow()


    init {
        setFields()
    }


    private fun setFields() {

        _fields.update {
            buildList {

                add(
                    LoginField(
                        value = "",
                        labelUiText = UiText.StringRes(R.string.first_name_as_text),
                        type = LoginFieldType.FirstName,
                        keyboardType = KeyboardType.Text,
                        validityText = UiText.DynamicString("")
                    )
                )

                add(
                    LoginField(
                        value = "",
                        labelUiText = UiText.StringRes(R.string.last_name_as_text),
                        type = LoginFieldType.LastName,
                        keyboardType = KeyboardType.Text,
                        validityText = UiText.DynamicString("")
                    )
                )

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

    fun updateField(loginFieldType: LoginFieldType, newValue: String) {
        _fields.update { list ->
            LoginField.updateField(
                newValue = newValue,
                fieldType = loginFieldType,
                list = list
            )

        }

    }

    fun setUserProfilePictureBitmap(bitmap: Bitmap?) {
        _userProfileImageBitmap.update { bitmap }
    }

    fun signUp(navHostController: NavHostController) {
        _isProcessingSignUp.update { true }
        val list = _fields.value
        val email = getValueFromField(list, LoginFieldType.Email)
        val password = getValueFromField(list, LoginFieldType.Password)
        val firstName = getValueFromField(list, LoginFieldType.FirstName)
        val lastName = getValueFromField(list, LoginFieldType.LastName)
        val bitmap = _userProfileImageBitmap.value

        if (email.isNotEmpty() && password.isNotEmpty()
            && firstName.isNotEmpty() &&
            lastName.isNotEmpty()
        ) {


            // add authentication ..
            firebaseAuthentication.signUp(
                email = email,
                password = password
            ) { result ->
                when (result) {
                    is TaskCallback.OnFailure -> showSignUpFailedFeedback(result.exception)
                    is TaskCallback.OnSuccess -> {
                        // add user to firestore ..
                        val firebaseUser = result.data
                        val myUser = MyUser(
                            firstName = firstName.trim(),
                            lastName = lastName.trim(),
                            email = email.trim(),
                            uid = firebaseUser.uid,
                            profilePicUriString = ""

                        )

                        if (bitmap == null) {
                            // add user
                            addUserToFirestore(myUser = myUser, firebaseUser = firebaseUser,
                                onDone = {
                                    navHostController.navigateToHomeScreen()
                                })
                        } else {

                            // upload profile picture to storage
                            uploadProfilePicture(bitmap, firebaseUser) { mediaUrl ->
                                // add user to firebase ..
                                addUserToFirestore(
                                    myUser = myUser.copy(profilePicUriString = mediaUrl),
                                    firebaseUser = firebaseUser,
                                    onDone = { userDocumentId ->
                                        // save document id in data store ..
                                        viewModelScope.launch {
                                            homeDatastore.setUserFirestoreDocumentId(userDocumentId)
                                            navHostController.navigateToHomeScreen()
                                        }


                                    }
                                )
                            }

                        }

                    }
                }
            }
        }
    }

    /** upload profile picture to storage and get the image download url */
    private fun uploadProfilePicture(
        bitmap: Bitmap,
        firebaseUser: FirebaseUser,
        onDone: (imageUri: String) -> Unit
    ) {
        myFirebaseStorage.uploadProfilePicture(
            bitmap = bitmap, callback = object : UploadMediaCallback {
                override fun onProgress(portion: Float) {
                    _uploadProfilePictureProgress.update { portion }
                }

                override fun onFailure(exception: Exception) {
                    // inform user to try again and delete authentication data ..
                    _isProcessingSignUp.update { false }
                    _uploadProfilePictureProgress.update { null }

                    SnackBarStateHolder.showSnackBar(
                        uiText = UiText.StringRes(
                            res = R.string.upload_profile_pic_error,
                            args = arrayOf(exception.message ?: "")
                        ),
                        type = SnackBarType.Error
                    )
                    firebaseAuthentication.deleteUser(firebaseUser)

                }

                override fun onSuccess(mediaUrl: String, mediaName: String) {
                    _uploadProfilePictureProgress.update { null }
                    onDone(mediaUrl)

                }
            }
        )
    }

    private fun addUserToFirestore(
        myUser: MyUser,
        firebaseUser: FirebaseUser,
        onDone: (userDocumentId: String) -> Unit
    ) {
        firestoreUsersActions.addUser(myUser) { addUserResult ->
            when (addUserResult) {
                is TaskCallback.OnFailure -> {
                    showSignUpFailedFeedback(addUserResult.exception)
                    // delete user from authentication ..
                    firebaseAuthentication.deleteUser(firebaseUser)
                }

                is TaskCallback.OnSuccess -> {
                    val userDocumentId = addUserResult.data

                    // navigate to Home Screen ..
                    _isProcessingSignUp.update { false }
                    onDone(userDocumentId)
                }
            }
        }
    }

    private fun showSignUpFailedFeedback(exception: Exception) {
        _isProcessingSignUp.update { false }
        SnackBarStateHolder.showSnackBar(
            uiText = UiText.StringRes(res = R.string.error_sign_up, exception.message ?: ""),
            type = SnackBarType.Error

        )
    }

    /* get text field value */
    private fun getValueFromField(
        fieldsList: List<LoginField>,
        type: LoginFieldType
    ): String {
        return fieldsList.find { it.type == type }?.value ?: ""
    }

    fun clearBitmap() {
        _userProfileImageBitmap.update { null }
        myFirebaseStorage.cancelTask()

        firebaseAuthentication.currentUser?.let {
            firebaseAuthentication.deleteUser(it)
        }
        _isProcessingSignUp.update { false }
    }

}