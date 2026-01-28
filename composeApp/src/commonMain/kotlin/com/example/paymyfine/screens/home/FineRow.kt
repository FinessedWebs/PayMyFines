package com.example.paymyfine.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.paymyfine.data.fines.IForceItem

@Composable
fun FineRow(
    fine: IForceItem,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = MaterialTheme.shapes.large
    ) {
        Column(Modifier.padding(16.dp)) {

            Text(
                text = fine.chargeDescriptions?.firstOrNull() ?: "No description",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = fine.offenceLocation ?: "Unknown location",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(6.dp))

            val amount = (fine.amountDueInCents ?: 0) / 100.0

            val amountText = (amount * 100).toInt() / 100.0

            Text(
                text = "R $amountText",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )

        }
    }
}
