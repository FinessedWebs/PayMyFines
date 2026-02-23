package com.example.paymyfine.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.example.paymyfine.data.auth.AuthService
import com.example.paymyfine.data.session.SessionStore
import com.example.paymyfine.screens.login.LoginScreenRoute
import kotlinx.coroutines.launch

class ChangePasswordScreen(
    private val sessionStore: SessionStore,
    private val authService: AuthService
) : Screen {

    @Composable
    override fun Content() {

        val navigator = LocalNavigator.current
        val scope = rememberCoroutineScope()

        var currentPassword by remember { mutableStateOf("") }
        var newPassword by remember { mutableStateOf("") }
        var message by remember { mutableStateOf<String?>(null) }

        Column(
            Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {

            OutlinedTextField(
                value = currentPassword,
                onValueChange = { currentPassword = it },
                label = { Text("Current Password") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = newPassword,
                onValueChange = { newPassword = it },
                label = { Text("New Password") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    scope.launch {

                        val result =
                            authService.changePassword(
                                currentPassword,
                                newPassword
                            )

                        if (result.isSuccess) {

                            sessionStore.clear()

                            navigator?.replaceAll(
                                LoginScreenRoute(sessionStore)
                            )
                        } else {
                            message =
                                result.exceptionOrNull()?.message
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Update Password")
            }

            message?.let {
                Spacer(Modifier.height(12.dp))
                Text(it, color = Color.Red)
            }
        }
    }



}