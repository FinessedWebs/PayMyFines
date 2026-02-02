package com.example.paymyfine.screens.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.example.paymyfine.screens.home.HomeScreenRoute
import com.example.paymyfine.screens.signup.SignupScreen
import com.example.paymyfine.screens.signup.SignupViewModel
import com.example.paymyfine.theme.OffWhite
import org.jetbrains.compose.resources.painterResource
import paymyfine.composeapp.generated.resources.Res
import paymyfine.composeapp.generated.resources.paymyfines_text_logo_white_back_remove

class LoginScreen(
    private val loginVm: LoginViewModel,
    private val signupVm: SignupViewModel
) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current
        val scope = rememberCoroutineScope()
        val state = loginVm.state
        var passwordVisible by remember { mutableStateOf(false) }

        Column(modifier = Modifier.fillMaxSize()) {

            // ───────────────── TOP BRAND AREA (30%) ─────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.3f)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {

                    Image(
                        painter = painterResource(
                            Res.drawable.paymyfines_text_logo_white_back_remove
                        ),
                        contentDescription = "PayMyFines Logo",
                        modifier = Modifier.size(90.dp)
                    )

                    Spacer(Modifier.height(12.dp))

                    Image(
                        painter = painterResource(
                            Res.drawable.paymyfines_text_logo_white_back_remove
                        ),
                        contentDescription = "PayMyFines Text Logo",
                        modifier = Modifier.height(60.dp)
                    )
                }
            }

            // ───────────────── LOGIN FORM (70%) ─────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.7f)
                    .clip(MaterialTheme.shapes.extraLarge)
                    .background(OffWhite)
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp)
            ) {

                Text(
                    text = "Enter your Email and Password to view your fines",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(20.dp))

                PayMyFineInput(
                    value = state.email,
                    onValueChange = loginVm::onEmailChange,
                    label = "Email",
                    keyboardType = KeyboardType.Email
                )

                Spacer(Modifier.height(14.dp))

                PayMyFinePasswordInput(
                    value = state.password,
                    onValueChange = loginVm::onPasswordChange,
                    label = "Password",
                    passwordVisible = passwordVisible,
                    onTogglePassword = { passwordVisible = !passwordVisible }
                )

                if (!state.errorMessage.isNullOrBlank()) {
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = state.errorMessage!!,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = {
                        loginVm.login {
                            navigator?.replace(HomeScreenRoute())
                        }

                    },
                    enabled = !state.isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = MaterialTheme.shapes.large
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Login")
                    }
                }

                Spacer(Modifier.height(14.dp))

                TextButton(
                    onClick = {
                        navigator?.push(SignupScreen(signupVm))
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("Don’t have an account? Sign up")
                }

                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

/* ───────────────── INPUT COMPONENTS ───────────────── */

@Composable
private fun PayMyFineInput(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    keyboardType: KeyboardType
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        shape = MaterialTheme.shapes.large
    )
}

@Composable
private fun PayMyFinePasswordInput(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    passwordVisible: Boolean,
    onTogglePassword: () -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label) },
        singleLine = true,
        visualTransformation = if (passwordVisible)
            VisualTransformation.None
        else
            PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        trailingIcon = {
            IconButton(onClick = onTogglePassword) {
                Icon(
                    imageVector = if (passwordVisible)
                        Icons.Default.VisibilityOff
                    else
                        Icons.Default.Visibility,
                    contentDescription = null
                )
            }
        },
        shape = MaterialTheme.shapes.large
    )
}
