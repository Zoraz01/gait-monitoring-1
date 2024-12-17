package com.gaitmonitoring.screens.data

import android.util.Patterns
import androidx.compose.ui.text.input.KeyboardType
import com.gaitmonitoring.data.UiText

data class LoginField(
    val value: String,
    val labelUiText: UiText,
    val type: LoginFieldType,
    val keyboardType: KeyboardType,
    val isOptional: Boolean = false,
    val validityText:UiText

    ) {

    val isNotEmpty: Boolean get() = value.isNotEmpty()

    val isValid: Boolean
        get() {
            return when (type) {
                LoginFieldType.FirstName -> value.isNotEmpty()
                LoginFieldType.LastName -> value.isNotEmpty()
                LoginFieldType.Email -> Patterns.EMAIL_ADDRESS.matcher(value).matches()
                LoginFieldType.Password -> value.length >= 6
            }
        }


    companion object {

        /** update login field within fields list */
        fun updateField(
            newValue: String,
            fieldType: LoginFieldType,
            list: List<LoginField>
        ): List<LoginField> {
            val mutable = list.toMutableList()
            val index = mutable.indexOf(mutable.find { it.type == fieldType })
            if (index >= 0) {
                mutable[index] = mutable[index].copy(value = newValue)
            }
            return mutable.toList()
        }

    }
}