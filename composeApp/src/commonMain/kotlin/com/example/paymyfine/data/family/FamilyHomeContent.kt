package com.example.paymyfine.data.family

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
private fun FamilyHomeContent(
    members: List<FamilyMember>,
    onDelete: (String) -> Unit
) {
    LazyColumn {
        items(members) { m ->

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Row(
                    Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Column {
                        Text("${m.fullName} ${m.surname}")
                        Text(m.relationship)
                        Text("ID: ${m.idNumber}")
                    }

                    IconButton(
                        onClick = { onDelete(m.linkId) }
                    ) {
                        Icon(Icons.Default.Delete, null)
                    }
                }
            }
        }
    }
}
