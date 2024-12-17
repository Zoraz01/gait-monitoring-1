package com.gaitmonitoring.navigationDrawer

import android.content.Context
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDefaults
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import com.gaitmonitoring.data.UiText
import com.gaitmonitoring.navigationDrawer.models.MySnackBarData
import com.gaitmonitoring.navigationDrawer.models.SnackBarType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


object SnackBarStateHolder {

    private val _mySnackBarData = MutableStateFlow(value = null as MySnackBarData?)
    val snackBarData = _mySnackBarData.asStateFlow()

    /* set message which is captured in a launched effect */
    fun showSnackBar(
        uiText: UiText,
        type: SnackBarType = SnackBarType.Normal,
        onDismiss: () -> Unit = {},
        actionLabel: String? = null,
        onAction: () -> Unit = {}
    ) =
        _mySnackBarData.update {
            MySnackBarData(
                uiText = uiText,
                type = type,
                onDismiss = onDismiss,
                onAction = onAction,
                actionLabel = actionLabel
            )
        }

    /* set the message null to allow any other snack bar messages */
    private fun hideSnackBar() = _mySnackBarData.update { null }

    /* show the snackBar message and set it to null */
    suspend fun showSnackBar(
        context: Context,
        snackBarHostState: SnackbarHostState,
        data: MySnackBarData
    ) {
        val result = snackBarHostState.showSnackbar(message = data.uiText.getValue(context))

        when (result) {
            SnackbarResult.Dismissed -> data.onDismiss?.invoke()
            SnackbarResult.ActionPerformed -> data.onAction?.invoke()
        }

        hideSnackBar()      // must be called to allow subsequence messages
    }

    /** Alter background color according to [SnackBarType] */
    @Composable
    fun rememberSnackBarContainerColor(snackBarData: MySnackBarData?): Color {
        val normalSnackBarColor = SnackbarDefaults.color
        val errorSnackBarColor = MaterialTheme.colorScheme.error

        return remember(snackBarData) {
            when (snackBarData?.type) {
                SnackBarType.Normal -> normalSnackBarColor
                SnackBarType.Error -> errorSnackBarColor
                null -> normalSnackBarColor
            }
        }


    }
}