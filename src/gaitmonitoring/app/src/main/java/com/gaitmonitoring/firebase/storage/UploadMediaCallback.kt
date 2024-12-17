package com.gaitmonitoring.firebase.storage

import com.google.android.gms.tasks.Task
import com.google.firebase.storage.UploadTask

interface UploadMediaCallback {


    fun onProgress(portion: Float) {}
    fun onFailure(exception: Exception) {}
    fun onSuccess(mediaUrl: String, mediaName:String) {}
    fun onCompleted(task: Task<UploadTask.TaskSnapshot>){}
}