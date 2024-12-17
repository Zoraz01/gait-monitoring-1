package com.gaitmonitoring.data

data class MyUser(
    val firstName:String,
    val lastName:String,
    val email:String,
    val profilePicUriString:String,
    val uid:String,
) {
    val fullName get() = "$firstName $lastName"
}