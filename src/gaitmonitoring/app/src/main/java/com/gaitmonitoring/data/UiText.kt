package com.gaitmonitoring.data

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource


/*
To overcome access of context to get string in view model
 */
sealed interface UiText {

    data class DynamicString(val value: String) : UiText
    class StringRes(
        @androidx.annotation.StringRes val res: Int,
        vararg val args: Any
    ) : UiText

    @Composable
    fun getValue(): String {
        return when (this) {
            is DynamicString -> value
            is StringRes -> stringResource(id = res, *args)
            else -> ({}).toString()
        }
    }

    fun getValue(context: Context): String {
        return when (this) {
            is DynamicString -> value
            is StringRes -> context.getString(res, *args)
            else -> ({}).toString()
        }
    }

}