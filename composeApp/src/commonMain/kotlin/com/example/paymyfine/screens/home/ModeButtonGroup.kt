package com.example.paymyfine.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ModeButtonGroup(
    mode: HomeMode,
    onModeChange: (HomeMode) -> Unit,
    modifier: Modifier = Modifier
) {

    val primary = MaterialTheme.colorScheme.primary
    val onPrimary = MaterialTheme.colorScheme.onPrimary
    val onPrimaryContainer = MaterialTheme.colorScheme.onPrimary

    SingleChoiceSegmentedButtonRow(
        modifier = modifier
    ) {

        SegmentedButton(
            selected = mode == HomeMode.INDIVIDUAL,
            onClick = { onModeChange(HomeMode.INDIVIDUAL) },
            shape = SegmentedButtonDefaults.itemShape(
                index = 0,
                count = 2
            ),
            colors = SegmentedButtonDefaults.colors(
                activeContainerColor = primary,
                activeContentColor = onPrimary,
                inactiveContainerColor = Color.Transparent,
                inactiveContentColor = onPrimaryContainer
            )
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Person, contentDescription = null)
                Spacer(Modifier.width(6.dp))
                Text("Individual")
            }
        }

        SegmentedButton(
            selected = mode == HomeMode.FAMILY,
            onClick = { onModeChange(HomeMode.FAMILY) },
            shape = SegmentedButtonDefaults.itemShape(
                index = 1,
                count = 2
            ),
            colors = SegmentedButtonDefaults.colors(
                activeContainerColor = primary,
                activeContentColor = onPrimary,
                inactiveContainerColor = Color.Transparent,
                inactiveContentColor = onPrimaryContainer
            )
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Home, contentDescription = null)
                Spacer(Modifier.width(6.dp))
                Text("Family")
            }
        }
    }
}