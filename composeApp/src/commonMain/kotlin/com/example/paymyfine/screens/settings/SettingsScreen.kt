package com.example.paymyfine.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.example.paymyfine.data.auth.AuthService
import com.example.paymyfine.data.session.SessionStore
import com.example.paymyfine.screens.login.LoginScreenRoute
import kotlinx.coroutines.launch

class SettingsScreen(
    private val sessionStore: SessionStore,
    private val authService: AuthService
) : Screen {

    @Composable
    override fun Content() {

        val navigator = LocalNavigator.current
        val scope = rememberCoroutineScope()

        var showConfirm by remember { mutableStateOf(false) }
        var isLoading by remember { mutableStateOf(false) }

        Column(
            Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {

            Text(
                "Account Settings",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = { showConfirm = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Deactivate Account")
            }
        }

        if (showConfirm) {
            AlertDialog(
                onDismissRequest = { showConfirm = false },

                title = { Text("Deactivate Account") },

                text = {
                    Text("Are you sure? This will deactivate your account.")
                },

                confirmButton = {
                    TextButton(
                        onClick = {
                            scope.launch {
                                isLoading = true

                                val result = authService.deactivate()

                                isLoading = false

                                if (result.isSuccess) {
                                    sessionStore.clear()

                                    navigator?.replace(
                                        LoginScreenRoute(sessionStore)
                                    )
                                }

                                showConfirm = false
                            }
                        }
                    ) {
                        Text("Deactivate")
                    }
                },

                dismissButton = {
                    TextButton(
                        onClick = { showConfirm = false }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }

        if (isLoading) {
            Box(
                Modifier.fillMaxSize(),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}
