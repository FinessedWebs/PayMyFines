package com.example.paymyfine.data.family

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun AddFamilyDialog(
    onDismiss: () -> Unit,
    onAdd: (FamilyAddRequest) -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var surname by remember { mutableStateOf("") }
    var idNumber by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    onAdd(
                        FamilyAddRequest(
                            fullName,
                            surname,
                            idNumber,
                            "Other"
                        )
                    )
                    onDismiss()
                }
            ) { Text("Add") }
        },
        dismissButton = {
            Button(onClick = onDismiss) { Text("Cancel") }
        },
        text = {
            Column {
                TextField(fullName,{fullName=it},label={Text("Full Name")})
                TextField(surname,{surname=it},label={Text("Surname")})
                TextField(idNumber,{idNumber=it},label={Text("ID Number")})
            }
        }
    )
}
