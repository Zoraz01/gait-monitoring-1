package com.gaitmonitoring.screens.login.uiChilds

import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle

@Composable
fun HintText(
    modifier: Modifier = Modifier,
    text: AnnotatedString,
    onLinkClicked: () -> Unit,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium
) {

    ClickableText(
        text = text,
        onClick = {
            onLinkClicked.invoke()
        },
        modifier = modifier,
        style = textStyle
    )

}