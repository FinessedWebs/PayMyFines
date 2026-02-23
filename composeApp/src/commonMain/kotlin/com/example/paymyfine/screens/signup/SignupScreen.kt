package com.example.paymyfine.screens.signup

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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
        val fullNameError: Boolean
        val emailError: Boolean
        val idNumberError: Boolean
        val passwordError: Boolean
        val repeatPasswordError: Boolean

        Box(Modifier.fillMaxSize()) {

            // Background
            Image(
                painter = painterResource(Res.drawable.sign_up_screen),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp)
                    .verticalScroll(rememberScrollState())
            ) {

                Spacer(Modifier.height(50.dp))

                // Back Button
                IconButton(onClick = { navigator?.pop() }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = null,
                        tint = Color.White
                    )
                }

                Spacer(Modifier.height(10.dp))

                Text(
                    "Create\nAccount",
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(40.dp))

                // Full Name
                DarkInputField(
                    value = state.fullName,
                    onChange = vm::onFullNameChange,
                    placeholder = "Full Name",
                    isError = state.fullNameError
                )

                Spacer(Modifier.height(16.dp))

                // Email
                DarkInputField(
                    value = state.email,
                    onChange = vm::onEmailChange,
                    placeholder = "Email",
                    isError = state.emailError
                )

                Spacer(Modifier.height(16.dp))

                // ID Number
                DarkInputField(
                    value = state.idNumber,
                    onChange = vm::onIdNumberChange,
                    placeholder = "ID Number",
                    isError = state.idNumberError
                )

                Spacer(Modifier.height(16.dp))

                // Password
                DarkPasswordField(
                    value = state.password,
                    onChange = vm::onPasswordChange,
                    placeholder = "Password",
                    visible = passwordVisible,
                    isError = state.passwordError,
                    toggle = { passwordVisible = !passwordVisible }
                )

                Spacer(Modifier.height(16.dp))

                // Repeat Password
                DarkPasswordField(
                    value = state.repeatPassword,
                    onChange = vm::onRepeatPasswordChange,
                    placeholder = "Repeat Password",
                    visible = repeatPasswordVisible,
                    isError = state.repeatPasswordError,
                    toggle = { repeatPasswordVisible = !repeatPasswordVisible }
                )

                Spacer(Modifier.height(30.dp))

                // Message (Error / Success)
                if (!state.message.isNullOrBlank()) {

                    Text(
                        text = state.message!!,
                        color = if (state.messageIsError)
                            MaterialTheme.colorScheme.error
                        else
                            Color(0xFF4CAF50)
                    )

                    Spacer(Modifier.height(16.dp))
                }

                PurpleButton(
                    text = "Sign up",
                    loading = state.isLoading
                ) {
                    vm.signup(scope) { navigator?.pop() }
                }

                Spacer(Modifier.height(30.dp))
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

    @Composable
    private fun DarkInputField(
        value: String,
        onChange: (String) -> Unit,
        placeholder: String,
        isError: Boolean = false
    ) {
        TextField(
            value = value,
            onValueChange = onChange,
            placeholder = { Text(placeholder, color = Color.LightGray) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.5.dp,
                    color = if (isError) Color.Red else Color.Transparent,
                    shape = RoundedCornerShape(50)
                ),
            shape = RoundedCornerShape(50),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0x33000000),
                unfocusedContainerColor = Color(0x33000000),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = if (isError) Color.Red else Color.White,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )
    }

    @Composable
    private fun DarkPasswordField(
        value: String,
        onChange: (String) -> Unit,
        placeholder: String,
        visible: Boolean,
        isError: Boolean = false,
        toggle: () -> Unit
    ) {
        TextField(
            value = value,
            onValueChange = onChange,
            placeholder = { Text(placeholder, color = Color.LightGray) },
            singleLine = true,
            visualTransformation =
                if (visible) VisualTransformation.None
                else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = toggle) {
                    Icon(
                        if (visible)
                            Icons.Default.VisibilityOff
                        else
                            Icons.Default.Visibility,
                        contentDescription = null,
                        tint = if (isError) Color.Red else Color.White
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.5.dp,
                    color = if (isError) Color.Red else Color.Transparent,
                    shape = RoundedCornerShape(50)
                ),
            shape = RoundedCornerShape(50),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0x33000000),
                unfocusedContainerColor = Color(0x33000000),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = if (isError) Color.Red else Color.White,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )
    }

    @Composable
    private fun PurpleButton(
        text: String,
        loading: Boolean,
        onClick: () -> Unit
    ) {
        Button(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF6C3BFF)
            )
        ) {
            if (loading)
                CircularProgressIndicator(
                    color = Color.White,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(22.dp)
                )
            else
                Text(text, color = Color.White, fontWeight = FontWeight.Bold)
        }
    }

}

