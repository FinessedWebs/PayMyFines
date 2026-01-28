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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.example.paymyfine.theme.OffWhite
import org.jetbrains.compose.resources.painterResource
import paymyfine.composeapp.generated.resources.Res
import paymyfine.composeapp.generated.resources.paymyfines_text_logo_white_back_remove

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
            modifier = Modifier
                .fillMaxSize()
                .background(OffWhite) // like your branded background
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            Spacer(Modifier.height(50.dp))

            Text(
                text = "Create Account",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(Modifier.height(24.dp))

            BrandOutlinedTextField(
                value = state.fullName,
                onValueChange = vm::onFullNameChange,
                label = "Full Name",
                keyboardType = KeyboardType.Text
            )

            Spacer(Modifier.height(16.dp))

            BrandOutlinedTextField(
                value = state.email,
                onValueChange = vm::onEmailChange,
                label = "Email",
                keyboardType = KeyboardType.Email
            )

            Spacer(Modifier.height(16.dp))

            BrandOutlinedTextField(
                value = state.idNumber,
                onValueChange = vm::onIdNumberChange,
                label = "ID Number",
                keyboardType = KeyboardType.Number
            )

            Spacer(Modifier.height(16.dp))

            BrandOutlinedPasswordField(
                value = state.password,
                onValueChange = vm::onPasswordChange,
                label = "Password",
                visible = passwordVisible,
                onToggleVisible = { passwordVisible = !passwordVisible }
            )

            Spacer(Modifier.height(16.dp))

            BrandOutlinedPasswordField(
                value = state.repeatPassword,
                onValueChange = vm::onRepeatPasswordChange,
                label = "Repeat Password",
                visible = repeatPasswordVisible,
                onToggleVisible = { repeatPasswordVisible = !repeatPasswordVisible }
            )

            Spacer(Modifier.height(24.dp))

            if (!state.message.isNullOrBlank()) {
                Text(
                    text = state.message!!,
                    color = if (state.messageIsError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(12.dp))
            }

            Button(
                onClick = {
                    vm.signup(scope) {
                        navigator?.pop() // go back to Login
                    }
                },
                enabled = !state.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = MaterialTheme.shapes.large,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White // matches your XML
                )
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(22.dp),
                        color = Color.White
                    )
                } else {
                    Text("Sign Up")
                }
            }

            Spacer(Modifier.height(16.dp))

            TextButton(
                onClick = { navigator?.pop() },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = "Already have an account? Login",
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(Modifier.height(28.dp))

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(Res.drawable.paymyfines_text_logo_white_back_remove),
                    contentDescription = "PMF Logo",
                    modifier = Modifier.size(100.dp)
                )
            }
        }
    }
}

@Composable
private fun BrandOutlinedTextField(
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
        shape = MaterialTheme.shapes.large,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.primary,
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@Composable
private fun BrandOutlinedPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    visible: Boolean,
    onToggleVisible: () -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label) },
        singleLine = true,
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        trailingIcon = {
            IconButton(onClick = onToggleVisible) {
                Icon(
                    imageVector = if (visible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = if (visible) "Hide password" else "Show password"
                )
            }
        },
        shape = MaterialTheme.shapes.large,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.primary,
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface
        )
    )
}
