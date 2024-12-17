package com.gaitmonitoring.firebase.storage

import android.graphics.Bitmap
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream
import java.util.Date

class MyFirebaseStorage {


    private val imageFileExtension = ".jpg"
    private val reference get() = Firebase.storage.reference
    private val profilePicturesFolderName = "users_profile_pictures"
    private var uploadTask: UploadTask? = null

    fun uploadProfilePicture(
        bitmap: Bitmap,
        callback: UploadMediaCallback
    ) {
        uploadImage(
            bitmap = bitmap,
            callback = callback,
            collectionName = profilePicturesFolderName
        )

    }


    private fun uploadImage(
        bitmap: Bitmap,
        collectionName: String,
        callback: UploadMediaCallback
    ) {
        val imageName = Date().toString() + ".$imageFileExtension"
        val imageRef = reference.child("$collectionName/$imageName")
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        uploadTask?.cancel()
        uploadTask = null
        uploadTask = imageRef.putBytes(data)
        uploadTask?.let { myTask ->

            myTask.addOnProgressListener { taskSnapshot ->

                val totalBytes = taskSnapshot.totalByteCount
                if (totalBytes > 0) {
                    val transferred = taskSnapshot.bytesTransferred.toFloat()
                    val portion = (transferred / totalBytes.toFloat())
                    callback.onProgress(portion)
                }
            }.addOnFailureListener { exception ->
                callback.onFailure(exception)
            }.addOnSuccessListener { uploadTask ->
                uploadTask.storage.downloadUrl.addOnSuccessListener { uri ->
                    callback.onSuccess(uri.toString(), imageName)
                }
            }.addOnCompleteListener {
                callback.onCompleted(it)
            }
        }

    }

    fun cancelTask() {
        uploadTask?.cancel()
        uploadTask = null
    }

}