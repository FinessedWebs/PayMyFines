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
import androidx.compose.ui.layout.ContentScale
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
import paymyfine.composeapp.generated.resources.login_screen
import paymyfine.composeapp.generated.resources.paymyfines_text_logo_white_back_remove
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MotionScheme
import androidx.compose.ui.draw.alpha

class LoginScreen(
    private val loginVm: LoginViewModel,
    private val signupVm: SignupViewModel
) : Screen {

    @OptIn(ExperimentalMaterial3ExpressiveApi::class)
    @Composable
    override fun Content() {

        val navigator = LocalNavigator.current
        val state = loginVm.state
        var showPass by remember { mutableStateOf(false) }

        val motion = MotionScheme.expressive()

        var startAnimation by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            startAnimation = true
        }

        val offsetY by animateDpAsState(
            targetValue = if (startAnimation) 0.dp else 40.dp,
            animationSpec = motion.defaultSpatialSpec()
        )

        val alpha by animateFloatAsState(
            targetValue = if (startAnimation) 1f else 0f,
            animationSpec = motion.defaultEffectsSpec()
        )

        Box(modifier = Modifier.fillMaxSize()) {

            // BACKGROUND IMAGE
            Image(
                painter = painterResource(Res.drawable.login_screen),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp)
                    .offset(y = offsetY)
                    .alpha(alpha),
                verticalArrangement = Arrangement.Center
            ) {

                Text(
                    "Welcome\nBack",
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(40.dp))

                DarkInputField(
                    value = state.email,
                    onChange = loginVm::onEmailChange,
                    placeholder = "Email"
                )

                Spacer(Modifier.height(16.dp))

                DarkPasswordField(
                    value = state.password,
                    onChange = loginVm::onPasswordChange,
                    placeholder = "Password",
                    visible = showPass,
                    toggle = { showPass = !showPass }
                )

                if (!state.errorMessage.isNullOrBlank()) {

                    val errorAlpha by animateFloatAsState(
                        targetValue = 1f,
                        animationSpec = motion.fastEffectsSpec()
                    )

                    Spacer(Modifier.height(12.dp))

                    Text(
                        state.errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.alpha(errorAlpha)
                    )
                }

                Spacer(Modifier.height(28.dp))

                PurpleButton(
                    text = "Log in",
                    loading = state.isLoading
                ) {
                    loginVm.login {
                        navigator?.replace(HomeScreenRoute())
                    }
                }

                if (state.showReactivate) {

                    Spacer(Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = { loginVm.reactivate() },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.Red
                        )
                    ) {
                        Text("Reactivate Account")
                    }
                }

                Spacer(Modifier.height(20.dp))

                TextButton(
                    onClick = { navigator?.push(SignupScreen(signupVm)) },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("Don’t have an account? Sign up", color = Color.White)
                }
            }
        }
    }

    @Composable
    private fun DarkInputField(
        value: String,
        onChange: (String) -> Unit,
        placeholder: String
    ) {
        TextField(
            value = value,
            onValueChange = onChange,
            placeholder = { Text(placeholder, color = Color.LightGray) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(50),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0x33000000),
                unfocusedContainerColor = Color(0x33000000),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = Color.White,
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
                        tint = Color.White
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(50),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0x33000000),
                unfocusedContainerColor = Color(0x33000000),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = Color.White,
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

