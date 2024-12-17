package com.gaitmonitoring.navigationDrawer.models

import com.gaitmonitoring.data.UiText

data class MySnackBarData(
    val uiText: UiText,
    val type: SnackBarType = SnackBarType.Normal,
    val actionLabel: String? = null,
    val onAction: (() -> Unit)? = null,
    val onDismiss: (() -> Unit)? = null

)