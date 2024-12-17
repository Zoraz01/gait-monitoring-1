package com.gaitmonitoring.extensions

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.gaitmonitoring.utils.isApi29
import java.io.File

fun Context.toastShort(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

/* get bitmap of selected image by uri */
fun Context.getBitmap(uri: Uri): Bitmap? {
    return try {
        val bitmap = if (isApi29) {
            val decoder = ImageDecoder.createSource(contentResolver, uri)
            val bitmap = ImageDecoder.decodeBitmap(decoder)
            bitmap
        } else {
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
            bitmap
        }
        bitmap
    } catch (e: Exception) {
        null
    }
}

fun Context.createTempImageFile(): File {
    val imageFileName = "JPEG_" + System.currentTimeMillis() + "_"
    return File.createTempFile(
        imageFileName, /* prefix */
        ".jpg", /* suffix */
        externalCacheDir      /* directory */
    )
}


fun Context.getFileUri(file: File): Uri? {
    return FileProvider.getUriForFile(
        this,
        "$packageName.provider", file
    )
}

fun Context.isPermissionGranted(permission: String): Boolean {
    return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
}