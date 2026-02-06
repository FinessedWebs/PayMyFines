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
import com.example.paymyfine.ui.ResponsiveScreenShell

class ProfileScreen : Screen {

    @Composable
    override fun Content() {

        var name by remember { mutableStateOf("Hope") }
        var email by remember { mutableStateOf("hope@gmail.com") }

        ResponsiveScreenShell {

            Column(
                Modifier
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
                        .clickable {
                            // TODO: image picker later
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text("Edit")
                }

                Spacer(Modifier.height(24.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name") }
                )

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") }
                )

                Spacer(Modifier.height(24.dp))

                Button(onClick = { /* TODO Save */ }) {
                    Text("Save Changes")
                }
            }
        }
    }
}
