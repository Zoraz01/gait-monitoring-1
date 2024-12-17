package com.gaitmonitoring.ui.common

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.gaitmonitoring.extensions.getBitmap

/* capture image from camera */
@Composable
fun rememberCameraImageLauncher(onResult: (isPhotoSaved: Boolean) -> Unit)
        : ManagedActivityResultLauncher<Uri, Boolean> {
    return rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { isPhotoSaved ->
            onResult(isPhotoSaved)
        }
    )
}

/* pick image from device storage */
@Composable
fun rememberPickImageLauncher(
    onBitmap: (Bitmap?) -> Unit
): ManagedActivityResultLauncher<PickVisualMediaRequest, Uri?> {
    val context = LocalContext.current
    return rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { pickedPhotoUri ->
            pickedPhotoUri?.let {
                val bitmap = context.getBitmap(pickedPhotoUri)
                onBitmap(bitmap)
            }
        })
}

val imagePickContract
    get() = PickVisualMediaRequest(
        ActivityResultContracts.PickVisualMedia.ImageOnly
    )

