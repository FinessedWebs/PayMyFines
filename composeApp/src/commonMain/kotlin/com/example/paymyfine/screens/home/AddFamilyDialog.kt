package com.example.paymyfine.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.paymyfine.data.family.models.AddFamilyMemberRequest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFamilyDialog(
    onDismiss: () -> Unit,
    onSubmit: (AddFamilyMemberRequest) -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var surname by remember { mutableStateOf("") }
    var idNumber by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var cell by remember { mutableStateOf("") }
    var relationship by remember { mutableStateOf("Relative") }
    var nickname by remember { mutableStateOf("") }

    val relationships = listOf(
        "Parent","Child","Spouse","Sibling",
        "Grandparent","Relative","Friend","Other"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {

            Button(
                onClick = {
                    if (fullName.isBlank() ||
                        surname.isBlank() ||
                        idNumber.isBlank()
                    ) return@Button

                    onSubmit(
                        AddFamilyMemberRequest(
                            fullName = fullName.trim(),
                            surname = surname.trim(),
                            idNumber = idNumber.trim(),
                            email = email.ifBlank { null },
                            cell = cell.ifBlank { null },
                            relationship = relationship,
                            nickname = nickname.ifBlank { null }
                        )
                    )
                    onDismiss()
                }
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        title = { Text("Add Family Member") },
        text = {

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                TextField(fullName,{fullName=it},label={Text("Full Name")})
                TextField(surname,{surname=it},label={Text("Surname")})
                TextField(idNumber,{idNumber=it},label={Text("ID Number")})

                TextField(email,{email=it},label={Text("Email")})
                TextField(cell,{cell=it},label={Text("Cell")})
                TextField(nickname,{nickname=it},label={Text("Nickname")})

                // Relationship dropdown
                var expanded by remember { mutableStateOf(false) }

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {

                    TextField(
                        value = relationship,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Relationship") },
                        modifier = Modifier.menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        relationships.forEach {
                            DropdownMenuItem(
                                text = { Text(it) },
                                onClick = {
                                    relationship = it
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        }
    )
}
