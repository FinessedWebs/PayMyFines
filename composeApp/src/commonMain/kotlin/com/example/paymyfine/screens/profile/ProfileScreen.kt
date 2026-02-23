package com.example.paymyfine.screens.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import androidx.compose.foundation.Image
import androidx.compose.material.icons.filled.CameraAlt
import com.example.paymyfine.data.session.SessionStore
import com.example.paymyfine.data.network.HttpClientFactory
import com.example.paymyfine.data.auth.AuthRepository
import com.example.paymyfine.data.auth.AuthService
import com.example.paymyfine.data.network.BaseUrlProvider
import com.example.paymyfine.platform.rememberPlatformImagePicker
import com.example.paymyfine.screens.login.LoginScreenRoute
import kotlinx.coroutines.launch



class ProfileScreen(
    private val sessionStore: SessionStore,
    private val authService: AuthService
) : Screen {

    @Composable
    override fun Content() {

        val navigator = LocalNavigator.current
        val scope = rememberCoroutineScope()

        val userId = sessionStore.getIdNumber() ?: ""

        var fullName by remember {
            mutableStateOf(sessionStore.getFullName() ?: "")
        }

        var email by remember {
            mutableStateOf(sessionStore.getEmail() ?: "")
        }

        var imageBytes by remember {
            mutableStateOf(sessionStore.getProfileImage(userId))
        }

        var showLogoutDialog by remember { mutableStateOf(false) }
        var showPasswordDialog by remember { mutableStateOf(false) }
        var message by remember { mutableStateOf<String?>(null) }
        val imagePicker = rememberPlatformImagePicker()

        Column(
            Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // ───────────────── PROFILE IMAGE ─────────────────
            Box(
                modifier = Modifier.size(140.dp),
                contentAlignment = Alignment.Center
            ) {

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(Color.LightGray)
                        .clickable {
                            // Simulate new image
                            val fake = ByteArray(50) { it.toByte() }
                            sessionStore.saveProfileImage(userId, fake)
                            imageBytes = fake
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        if (imageBytes == null)
                            "Add Image"
                        else
                            "Edit Image"
                    )
                }

                // CAMERA ICON
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .clickable {
                            imagePicker.pickImage { bytes ->
                                if (bytes != null) {
                                    sessionStore.saveProfileImage(userId, bytes)
                                    imageBytes = bytes
                                }
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text("📷", color = Color.White)
                }
            }

            Spacer(Modifier.height(32.dp))

            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    scope.launch {

                        val result =
                            authService.updateProfile(fullName, email)

                        if (result.isSuccess) {

                            sessionStore.saveFullName(fullName)
                            sessionStore.saveEmail(email)

                            showLogoutDialog = true
                        } else {
                            message =
                                result.exceptionOrNull()?.message
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Changes")
            }

            Spacer(Modifier.height(12.dp))

            OutlinedButton(
                onClick = { showPasswordDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Change Password")
            }

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = { showLogoutDialog = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Logout")
            }

            message?.let {
                Spacer(Modifier.height(12.dp))
                Text(it, color = MaterialTheme.colorScheme.error)
            }
        }

        if (showPasswordDialog) {

            ChangePasswordDialog(
                authService = authService,
                onDismiss = { showPasswordDialog = false },
                onSuccess = {
                    showPasswordDialog = false
                    showLogoutDialog = true
                }
            )
        }

        if (showLogoutDialog) {

            AlertDialog(
                onDismissRequest = { showLogoutDialog = false },
                title = { Text("Session Expired") },
                text = { Text("Please login again.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            sessionStore.clear()
                            navigator?.replaceAll(
                                LoginScreenRoute(sessionStore)
                            )
                        }
                    ) {
                        Text("Login")
                    }
                }
            )
        }
    }
}

@Composable
fun ChangePasswordDialog(
    authService: AuthService,
    onDismiss: () -> Unit,
    onSuccess: () -> Unit
) {

    val scope = rememberCoroutineScope()

    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Change Password") },
        text = {

            Column {

                OutlinedTextField(
                    value = currentPassword,
                    onValueChange = { currentPassword = it },
                    label = { Text("Current Password") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("New Password") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )

                error?.let {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    scope.launch {

                        val result =
                            authService.changePassword(
                                currentPassword,
                                newPassword
                            )

                        if (result.isSuccess) {
                            onSuccess()
                        } else {
                            error =
                                result.exceptionOrNull()?.message
                        }
                    }
                }
            ) {
                Text("Change")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}