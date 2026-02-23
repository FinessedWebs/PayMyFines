package com.example.paymyfine.screens.signup

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.example.paymyfine.theme.OffWhite
import org.jetbrains.compose.resources.painterResource
import paymyfine.composeapp.generated.resources.*

class SignupScreen(
    private val vm: SignupViewModel
) : Screen {

    @Composable
    override fun Content() {

        val navigator = LocalNavigator.current
        val scope = rememberCoroutineScope()
        val state = vm.state

        var passwordVisible by remember { mutableStateOf(false) }
        var repeatPasswordVisible by remember { mutableStateOf(false) }

        Column(
            Modifier
                .fillMaxSize()
                .background(OffWhite)
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {

            Spacer(Modifier.height(40.dp))

            Text(
                "Create Account",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(24.dp))

            BrandTextField(
                state.fullName,
                vm::onFullNameChange,
                "Full Name",
                KeyboardType.Text
            )

            Spacer(Modifier.height(16.dp))

            BrandTextField(
                state.email,
                vm::onEmailChange,
                "Email",
                KeyboardType.Email
            )

            Spacer(Modifier.height(16.dp))

            BrandTextField(
                state.idNumber,
                vm::onIdNumberChange,
                "ID Number",
                KeyboardType.Number
            )

            Spacer(Modifier.height(16.dp))

            BrandPasswordField(
                state.password,
                vm::onPasswordChange,
                "Password",
                passwordVisible
            ) { passwordVisible = !passwordVisible }

            Spacer(Modifier.height(16.dp))

            BrandPasswordField(
                state.repeatPassword,
                vm::onRepeatPasswordChange,
                "Repeat Password",
                repeatPasswordVisible
            ) { repeatPasswordVisible = !repeatPasswordVisible }

            Spacer(Modifier.height(24.dp))

            if (!state.message.isNullOrBlank()) {
                Text(
                    state.message!!,
                    color = if (state.messageIsError)
                        MaterialTheme.colorScheme.error
                    else
                        MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(12.dp))
            }

            Button(
                onClick = {
                    vm.signup(scope) { navigator?.pop() }
                },
                enabled = !state.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                if (state.isLoading)
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.dp
                    )
                else
                    Text("Sign Up")
            }

            Spacer(Modifier.height(12.dp))

            TextButton(
                onClick = { navigator?.pop() },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Already have an account? Login")
            }

            Spacer(Modifier.height(30.dp))

            Box(Modifier.fillMaxWidth(), Alignment.Center) {
                Image(
                    painterResource(Res.drawable.paymyfines_text_logo_white_back_remove),
                    null,
                    modifier = Modifier.size(220.dp)
                )
            }
        }
    }
}

@Composable
private fun BrandTextField(
    value: String,
    onChange: (String) -> Unit,
    label: String,
    type: KeyboardType
) {
    OutlinedTextField(
        value,
        onChange,
        label = { Text(label) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = type)
    )
}

@Composable
private fun BrandPasswordField(
    value: String,
    onChange: (String) -> Unit,
    label: String,
    visible: Boolean,
    toggle: () -> Unit
) {
    OutlinedTextField(
        value,
        onChange,
        label = { Text(label) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        visualTransformation =
            if (visible) VisualTransformation.None
            else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        trailingIcon = {
            IconButton(toggle) {
                Icon(
                    if (visible)
                        Icons.Default.VisibilityOff
                    else Icons.Default.Visibility,
                    null
                )
            }
        }
    )
}
