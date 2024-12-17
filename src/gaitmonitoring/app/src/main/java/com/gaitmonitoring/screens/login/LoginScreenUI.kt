package com.gaitmonitoring.screens.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.gaitmonitoring.R
import com.gaitmonitoring.extensions.navigateToSignUp
import com.gaitmonitoring.screens.common.LoginSignupButton
import com.gaitmonitoring.screens.common.LogoUI
import com.gaitmonitoring.screens.login.uiChilds.HintText
import com.gaitmonitoring.screens.login.uiChilds.LoginTextField
import com.gaitmonitoring.ui.common.Space
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginScreenUI(
    navHostController: NavHostController
) {

    val context = LocalContext.current
    val viewModel: LoginScreenViewModel = koinViewModel()

    val fields by viewModel.fields.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val enableLoginButton by remember { derivedStateOf { fields.all { it.isValid } } }


    Surface(modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(top = 64.dp)
        ) {

            Spacer(modifier = Modifier.height(16.dp)) // Adjust the spacing as needed

            // Title Text
            LogoUI(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth()
            )

            Space(height = 32.dp) // Additional space below the title

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {

                fields.fastForEachIndexed { index, loginField ->
                    LoginTextField(
                        field = loginField,
                        label = loginField.labelUiText.getValue(),
                        onValueChanged = viewModel::updateField,
                        keyboardType = loginField.keyboardType,
                        modifier = Modifier.fillMaxWidth(),
                        imeAction = if (index == fields.lastIndex) {
                            ImeAction.Done
                        } else {
                            ImeAction.Next
                        }
                    )
                }
            }

            Space(height = 64.dp)

            LoginSignupButton(
                isLoading = isLoading,
                onClick = { viewModel.login(navHostController) },
                isEnabled = enableLoginButton,
                text = stringResource(id = R.string.login_as_text),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )


            Space(height = 32.dp)

            HintText(
                text = Utils.getAnnotatedString(fullText = context.getString(R.string.forget_password_hint)),
                modifier = Modifier.align(Alignment.End),
                onLinkClicked = {

                }
            )

            Space(height = 8.dp, modifier = Modifier.weight(1f))

            // register
            HintText(
                text = Utils.getAnnotatedString(
                    fullText = context.getString(R.string.create_account_hint),
                    textToHyperlink = context.getString(R.string.register_as_text)
                ),
                onLinkClicked = {
                    navHostController.navigateToSignUp()
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 24.dp)
            )

        }
    }

}
