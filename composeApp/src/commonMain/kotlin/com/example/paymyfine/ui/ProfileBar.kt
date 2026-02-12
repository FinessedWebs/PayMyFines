package com.example.paymyfine.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.paymyfine.data.fines.IForceItem
import com.example.paymyfine.screens.home.FineRow
import kotlin.math.*
import kotlinx.datetime.*
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime



/* ---------------- SMART AVATAR ---------------- */

@Composable
fun SmartAvatar(idNumber: String?) {

    val icon = chooseAvatarIcon(idNumber)

    Box(
        modifier = Modifier
            .size(60.dp)
            .clip(CircleShape)
            .background(Color(0xFFF5F5F5)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(34.dp),
            tint = Color.Gray
        )
    }
}

fun chooseAvatarIcon(id: String?): androidx.compose.ui.graphics.vector.ImageVector {

    if (id.isNullOrBlank() || id.length < 10)
        return Icons.Default.Person

    val genderDigits = id.substring(6, 10).toIntOrNull() ?: 0
    val isMale = genderDigits >= 5000

    val age = calculateAge(id)

    return when {
        isMale && age < 18 -> Icons.Default.ChildCare
        isMale && age < 65 -> Icons.Default.Man
        isMale -> Icons.Default.Elderly

        !isMale && age < 18 -> Icons.Default.ChildFriendly
        !isMale && age < 65 -> Icons.Default.Woman
        else -> Icons.Default.ElderlyWoman
    }
}

fun calculateAge(id: String): Int {

    if (id.length < 6) return 30

    val yearPart = id.substring(0, 2).toIntOrNull() ?: return 30

    val birthYear =
        if (yearPart <= 25) 2000 + yearPart
        else 1900 + yearPart

    val currentYear = 2025

    return currentYear - birthYear
}


/* ---------------- PROFILE BAR ---------------- */

@Composable
fun ProfileBar(
    fullName: String,
    email: String,
    idNumber: String,
    fineCount: Int,
    onProfileClick: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .clickable { onProfileClick() },
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFE082)
        )
    ) {

        Column {

            Row(
                Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                SmartAvatar(idNumber)

                Spacer(Modifier.width(12.dp))

                Column {
                    Text(
                        fullName.ifBlank { "You" },
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1565C0)
                    )

                    Text(email, color = Color.Gray)
                    Text("ID: $idNumber", color = Color.Gray)
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(
                    bottomStart = 18.dp,
                    bottomEnd = 18.dp
                )
            ) {
                Text(
                    "$fineCount unpaid fines found",
                    modifier = Modifier.padding(16.dp),
                    color = Color.Gray
                )
            }
        }
    }
}
