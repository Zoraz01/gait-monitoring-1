package com.gaitmonitoring.screens.signup

import android.Manifest.permission.CAMERA
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.gaitmonitoring.R
import com.gaitmonitoring.data.UiText
import com.gaitmonitoring.extensions.createTempImageFile
import com.gaitmonitoring.extensions.getBitmap
import com.gaitmonitoring.extensions.getFileUri
import com.gaitmonitoring.extensions.isPermissionGranted
import com.gaitmonitoring.navigationDrawer.SnackBarStateHolder
import com.gaitmonitoring.screens.common.LoginSignupButton
import com.gaitmonitoring.screens.common.LogoUI
import com.gaitmonitoring.screens.login.Utils
import com.gaitmonitoring.screens.login.uiChilds.HintText
import com.gaitmonitoring.screens.login.uiChilds.LoginTextField
import com.gaitmonitoring.ui.common.Space
import com.gaitmonitoring.ui.common.imagePickContract
import com.gaitmonitoring.ui.common.rememberCameraImageLauncher
import com.gaitmonitoring.ui.common.rememberPickImageLauncher
import org.koin.androidx.compose.koinViewModel

@Composable
fun SignUpScreenUI(
    navHostController: NavHostController
) {

    val context = LocalContext.current
    val viewModel: SignUpViewModel = koinViewModel()

    // states ..
    val fields by viewModel.fields.collectAsStateWithLifecycle()
    val enableSignUpButton by remember { derivedStateOf { fields.all { it.isValid } } }
    val isLoading by viewModel.isProcessingSignUp.collectAsStateWithLifecycle()
    val uploadProfilePictureProgress by viewModel.uploadProfilePictureProgress.collectAsStateWithLifecycle()
    val userBitmap by viewModel.userProfileImageBitmap.collectAsStateWithLifecycle()
    var capturedImageUriString by rememberSaveable { mutableStateOf("") }

    // profile picture pic
    val pickImageLauncher = rememberPickImageLauncher(onBitmap = { bitmap ->
        viewModel.setUserProfilePictureBitmap(bitmap)
    })

    val captureImageLauncher = rememberCameraImageLauncher(onResult = { isPhotoSaved ->
        if (isPhotoSaved) {
            // get bitmap from file uri
            val uri = Uri.parse(capturedImageUriString)
            val bitmap = context.getBitmap(uri)
            viewModel.setUserProfilePictureBitmap(bitmap)
        } else {
            SnackBarStateHolder.showSnackBar(uiText = UiText.StringRes(R.string.error_save_camera_image))
            capturedImageUriString = ""
        }

    })

    val launchCamera = remember {
        {
            val tempFile = context.createTempImageFile()
            val uri = context.getFileUri(tempFile)
            capturedImageUriString = uri.toString()
            captureImageLauncher.launch(uri)
        }
    }

    val cameraPermissionLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission(),
            onResult = { isGranted ->
                if (isGranted) {
                    launchCamera.invoke()
                } else {
                    SnackBarStateHolder.showSnackBar(
                        uiText = UiText.StringRes(R.string.camera_permission_denied)
                    )
                }
            })

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(top = 48.dp)
        ) {


            Space(height = 16.dp)
            LogoUI(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth()
            )

            Space(height = 40.dp)

            LazyColumn(
                content = {
                    itemsIndexed(
                        fields,
                        key = { _, field -> field.type.name }) { index, loginField ->
                        LoginTextField(
                            field = loginField,
                            label = loginField.labelUiText.getValue(),
                            onValueChanged = viewModel::updateField,
                            keyboardType = loginField.keyboardType,
                            modifier = Modifier.fillMaxWidth(),
                            imeAction = if (index == fields.lastIndex) {
                                ImeAction.Done
                            } else {
                                ImeAction.Next
                            }
                        )

                    }
                    item {
                        PickProfilePictureRow(
                            onPickFromCamera = {
                                if (context.isPermissionGranted(CAMERA)) {
                                    launchCamera.invoke()
                                } else {
                                    cameraPermissionLauncher.launch(CAMERA)
                                }
                            },
                            onPickFromGallery = {
                                pickImageLauncher.launch(imagePickContract)
                            },
                            pickedBitmap = userBitmap,
                            onClearBitmap = viewModel::clearBitmap,
                            uploadProfilePictureProgress = uploadProfilePictureProgress,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            )

            Space(height = 16.dp)

            LoginSignupButton(
                modifier = Modifier.fillMaxWidth(),
                isLoading = isLoading,
                onClick = { viewModel.signUp(navHostController) },
                isEnabled = enableSignUpButton,
                text = stringResource(id = R.string.sign_up)
            )


            Space(height = 24.dp)


            HintText(
                text = Utils.getAnnotatedString(
                    fullText = stringResource(id = R.string.already_have_account_hint),
                    textToHyperlink = stringResource(id = R.string.sign_in_text)
                ),
                onLinkClicked = { navHostController.popBackStack() },
                modifier = Modifier
                    .padding(bottom = 24.dp)
                    .align(Alignment.CenterHorizontally)
            )
        }
    }

}