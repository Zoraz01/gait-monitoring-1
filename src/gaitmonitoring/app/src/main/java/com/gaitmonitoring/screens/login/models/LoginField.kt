package com.gaitmonitoring.screens.login.models

sealed class LoginField(val value: String) {
    data class Email(private val _value: String) : LoginField(_value)
    data class Password(private val _value: String): LoginField(_value)

    val isEmpty get() = value.isEmpty()
    val isNotEmpty get() = value.isNotEmpty()

}