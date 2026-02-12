package com.example.paymyfine.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator

import com.example.paymyfine.screens.login.LoginScreen
import com.example.paymyfine.screens.login.LoginViewModel
import com.example.paymyfine.screens.signup.SignupViewModel
import com.example.paymyfine.ui.ResponsiveScreenShell

import com.russhwolf.settings.Settings
import com.example.paymyfine.data.session.SessionStore
import com.example.paymyfine.data.network.HttpClientFactory
import com.example.paymyfine.data.auth.AuthRepository
import com.example.paymyfine.data.auth.AuthService
import com.example.paymyfine.data.network.BaseUrlProvider

class ProfileScreen : Screen {

    @Composable
    override fun Content() {

        val navigator = LocalNavigator.current

        var showLogoutDialog by remember { mutableStateOf(false) }

        ResponsiveScreenShell {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // PROFILE IMAGE
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray)
                        .clickable { /* future image picker */ },
                    contentAlignment = Alignment.Center
                ) {
                    Text("Edit")
                }

                Spacer(Modifier.height(24.dp))

                Button(onClick = { /* Save profile later */ }) {
                    Text("Save Changes")
                }

                Spacer(Modifier.height(40.dp))

                // ⭐ LOGOUT BUTTON (opens dialog)
                Button(
                    onClick = { showLogoutDialog = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Logout")
                }
            }
        }

        // ⭐ LOGOUT CONFIRMATION DIALOG
        if (showLogoutDialog) {

            AlertDialog(
                onDismissRequest = { showLogoutDialog = false },

                title = { Text("Logout") },

                text = {
                    Text("Are you sure you want to logout?")
                },

                confirmButton = {
                    TextButton(
                        onClick = {

                            showLogoutDialog = false

                            // ✅ rebuild dependency chain
                            val settings = Settings()
                            val sessionStore = SessionStore(settings)

// clear saved token/session
                            sessionStore.clear()

// client
                            val client = HttpClientFactory.create(sessionStore)

// ⭐ create service (THIS WAS MISSING)
                            val baseUrl = BaseUrlProvider.get()
                            val authService = AuthService(client, baseUrl)

// ⭐ repo uses service
                            val authRepo = AuthRepository(authService, sessionStore)

// VMs use repo
                            val loginVm = LoginViewModel(authRepo)
                            val signupVm = SignupViewModel(authRepo)

                            navigator?.replaceAll(
                                LoginScreen(loginVm, signupVm)
                            )

                        }
                    ) {
                        Text("Logout")
                    }
                },

                dismissButton = {
                    TextButton(
                        onClick = { showLogoutDialog = false }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
