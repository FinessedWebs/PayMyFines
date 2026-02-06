package com.example.paymyfine.screens.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.paymyfine.data.fines.IForceItem
import kotlin.math.round

@Composable
fun FineRow(
    fine: IForceItem,
    onClick: () -> Unit
) {

    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        border = BorderStroke(1.dp, Color(0xFFDDDDDD)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {

        Column(Modifier.padding(16.dp)) {

            InfoRow("Ticket Number:", fine.noticeNumber)

            InfoRow("Reg Number:", fine.vehicleLicenseNumber)

            Spacer(Modifier.height(6.dp))

            // REASON LABEL
            Text(
                "Reason:",
                fontSize = 10.sp,
                color = Color.Gray
            )

            // REASON BADGE
            ReasonBadge(
                fine.chargeDescriptions?.firstOrNull()
                    ?: "No description"
            )

            Spacer(Modifier.height(8.dp))

            InfoRow("Location:", fine.offenceLocation)

            InfoRow("Offence Date:", fine.offenceDate)

            InfoRow(
                "Amount Due:",
                formatMoney(fine.amountDueInCents)
            )

            Spacer(Modifier.height(12.dp))

            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                /*Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = Color(0xFFFFD401),
                    modifier = Modifier.size(30.dp)
                )*/

                Spacer(Modifier.weight(1f))

                Text(
                    "Show more details +",
                    color = Color(0xFF005BBB),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onClick() }
                )
            }
        }
    }
}
@Composable
private fun InfoRow(label: String, value: String?) {

    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Text(
            label,
            fontSize = 10.sp,
            color = Color.Gray
        )

        Text(
            value ?: "-",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }

    Spacer(Modifier.height(4.dp))
}

@Composable
private fun ReasonBadge(text: String) {

    Box(
        modifier = Modifier
            .padding(top = 4.dp)
            .background(
                Color(0x33FF0000),
                RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text,
            color = Color.Red,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp
        )
    }
}

private fun formatMoney(cents: Int?): String {
    val value = (cents ?: 0) / 100.0

    val whole = value.toInt()
    val decimals = ((value - whole) * 100).toInt()

    return "R$whole.${decimals.toString().padStart(2, '0')}"
}


