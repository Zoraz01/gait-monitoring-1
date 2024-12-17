package com.gaitmonitoring.screens.login.uiChilds

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.gaitmonitoring.R
import com.gaitmonitoring.screens.data.LoginField
import com.gaitmonitoring.screens.data.LoginFieldType
import com.gaitmonitoring.ui.common.Space

@Composable
fun LoginTextField(
    modifier: Modifier = Modifier,
    field: LoginField,
    label: String,
    onValueChanged: (type: LoginFieldType, newValue: String) -> Unit,
    keyboardType: KeyboardType,
    imeAction: ImeAction = ImeAction.Default
) {

    var passwordIsVisible by remember { mutableStateOf(false) }

    Column(modifier = modifier) {

        OutlinedTextField(
            shape = RoundedCornerShape(8.dp),
            value = field.value,
            onValueChange = {
                onValueChanged(field.type, it)
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text(text = label, style = MaterialTheme.typography.bodyMedium)
            },
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
            visualTransformation =
            if (field.type == LoginFieldType.Password && field.isNotEmpty && !passwordIsVisible) {
                PasswordVisualTransformation()
            } else {
                VisualTransformation.None
            },
            trailingIcon = {
                TrailingIconRow(
                    passwordIsVisible = passwordIsVisible,
                    togglePasswordVisibility = { passwordIsVisible = !passwordIsVisible },
                    onClearField = { onValueChanged(field.type, "") },
                    field = field
                )

            },
            maxLines = 1,
            singleLine = true
        )

        if (field.isOptional) {
            Text(
                text = stringResource(id = R.string.optional_text),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .alpha(0.65f)
                    .padding(top = 2.dp, start = 2.dp),

                )
        }

        Crossfade(
            targetState = field.isNotEmpty,
            label = "",
            modifier = Modifier.padding(top = 2.dp, start = 2.dp)
        ) {
            if (it) {

                ValidityText(validityText = field.validityText.getValue(), isValid = field.isValid)
            }
        }
    }
}

@Composable
private fun TrailingIconRow(
    passwordIsVisible: Boolean,
    togglePasswordVisibility: () -> Unit,
    onClearField: () -> Unit,
    field: LoginField,
) {

    val imageRes = remember(passwordIsVisible) {
        if (passwordIsVisible)
            R.drawable.visibility_on_icon
        else
            R.drawable.visibility_off_icon
    }
    val trailingIconSize = 16.dp

    val trailingIconColor = remember { Color.Gray }



    Row {
        Crossfade(
            targetState = field.type == LoginFieldType.Password && field.isNotEmpty,
            label = ""
        ) {
            if (it) {
                IconButton(onClick = togglePasswordVisibility) {
                    Icon(
                        painter = painterResource(id = imageRes),
                        contentDescription = null,
                        modifier = Modifier.size(trailingIconSize),
                        tint = trailingIconColor
                    )
                }
            }
        }

        Crossfade(targetState = field.isNotEmpty, label = "") {
            if (it) {
                IconButton(onClick = onClearField) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        modifier = Modifier.size(trailingIconSize),
                        tint = trailingIconColor
                    )
                }
            }
        }
    }
}

@Composable
fun ValidityText(
    validityText: String,
    isValid: Boolean
) {

    val color by animateColorAsState(
        label = "",
        targetValue = if (isValid) MaterialTheme.colorScheme.primary else
            MaterialTheme.colorScheme.error
    )
    val iconSize = 16.dp

    if (validityText.isNotEmpty()) {

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = validityText, style = MaterialTheme.typography.bodySmall, color = color)
            Space(width = 8.dp)
            Crossfade(targetState = isValid, label = "") {
                if (it) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null, modifier = Modifier.size(iconSize),
                        tint = color
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null, modifier = Modifier.size(iconSize),
                        tint = color
                    )
                }
            }
        }
    }
}