package com.gaitmonitoring.screens.login

import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit

object Utils {

    @Composable
    fun getAnnotatedString(
        fullText: String,
        textToHyperlink: String = fullText,
        underlineLink: Boolean = false,
        linkFontSize: TextUnit = MaterialTheme.typography.bodyMedium.fontSize


    ): AnnotatedString {

        val annotatedString: AnnotatedString = buildAnnotatedString {

            val startIndex = fullText.indexOf(textToHyperlink)
            val endIndex = startIndex + textToHyperlink.length
            withStyle(style = SpanStyle(color = LocalContentColor.current)){
                append(fullText)
            }

            addStyle(
                style = SpanStyle(
                    color = Color(0xff64B5F6),
                    fontSize = linkFontSize,
                    textDecoration = if (underlineLink) TextDecoration.Underline else null
                ), start = startIndex, end = endIndex
            )

            // attach a string annotation that stores a URL to the text "link"
            addStringAnnotation(
                tag = "URL",
                annotation = "https://github.com",
                start = startIndex,
                end = endIndex
            )

        }

        return annotatedString
    }

}