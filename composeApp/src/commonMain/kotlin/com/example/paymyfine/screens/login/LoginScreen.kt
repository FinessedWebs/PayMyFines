package com.example.paymyfine.screens.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.example.paymyfine.screens.home.HomeScreenRoute
import com.example.paymyfine.screens.signup.SignupScreen
import com.example.paymyfine.screens.signup.SignupViewModel
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
        val state = loginVm.state
        var showPass by remember { mutableStateOf(false) }

        val BrandYellow = Color(0xFFFFD401)

        Column(
            Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center
        ) {

            // ✅ LOGO (UNCHANGED)
            Image(
                painter = painterResource(
                    Res.drawable.paymyfines_text_logo_white_back_remove
                ),
                contentDescription = null,
                modifier = Modifier
                    .size(280.dp)
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(Modifier.height(10.dp))

            Text(
                "Welcome Back",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Text("Login to continue", color = Color.Gray)

            Spacer(Modifier.height(32.dp))

            // EMAIL
            OutlinedTextField(
                value = state.email,
                onValueChange = loginVm::onEmailChange,
                label = { Text("Email") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email
                )
            )

            Spacer(Modifier.height(16.dp))

            // PASSWORD (FIXED ICONBUTTON)
            OutlinedTextField(
                value = state.password,
                onValueChange = loginVm::onPasswordChange,
                label = { Text("Password") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                visualTransformation =
                    if (showPass)
                        VisualTransformation.None
                    else
                        PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(
                        onClick = { showPass = !showPass }
                    ) {
                        Icon(
                            imageVector =
                                if (showPass)
                                    Icons.Default.VisibilityOff
                                else
                                    Icons.Default.Visibility,
                            contentDescription = null
                        )
                    }
                }
            )

            if (!state.errorMessage.isNullOrBlank()) {
                Spacer(Modifier.height(8.dp))
                Text(
                    state.errorMessage!!,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(Modifier.height(24.dp))

            // LOGIN BUTTON
            Button(
                onClick = {
                    loginVm.login {
                        navigator?.replace(HomeScreenRoute())
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrandYellow,
                    contentColor = Color.Black
                )
            ) {
                if (state.isLoading)
                    CircularProgressIndicator(
                        color = Color.Black,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(22.dp)
                    )
                else
                    Text("Login", fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(12.dp))

            TextButton(
                onClick = {
                    navigator?.push(SignupScreen(signupVm))
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Create account")
            }

            // ✅ REACTIVATE BUTTON (RED)
            if (state.showReactivate) {

                Spacer(Modifier.height(8.dp))

                OutlinedButton(
                    onClick = { loginVm.reactivate() },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.Red
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Reactivate Account")
                }
            }
        }
    }
}

