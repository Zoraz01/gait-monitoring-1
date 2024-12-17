package com.gaitmonitoring.firebase

sealed class TaskCallback<out T> {
    data class OnSuccess<T>(val data: T) : TaskCallback<T>()
    data class OnFailure(val exception: Exception) : TaskCallback<Nothing>()
}