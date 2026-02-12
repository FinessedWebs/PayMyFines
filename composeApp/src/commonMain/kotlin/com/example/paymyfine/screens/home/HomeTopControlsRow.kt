package com.example.paymyfine.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun HomeTopControlsRow(
    mode: HomeMode,
    filtersActive: Boolean,
    onModeChange: (HomeMode) -> Unit,
    onFilterClick: () -> Unit,
    onSearchClick: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        //////////////////////////////////////
        // LEFT — MODE TOGGLE
        //////////////////////////////////////

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconToggleButton(
                checked = mode == HomeMode.INDIVIDUAL,
                onCheckedChange = {
                    onModeChange(HomeMode.INDIVIDUAL)
                }
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = "Individual Mode",
                    tint =
                        if (mode == HomeMode.INDIVIDUAL)
                            Color.White
                        else
                            Color.DarkGray,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(Modifier.width(12.dp))

            IconToggleButton(
                checked = mode == HomeMode.FAMILY,
                onCheckedChange = {
                    onModeChange(HomeMode.FAMILY)
                }
            ) {
                Icon(
                    Icons.Default.Group,
                    contentDescription = "Family Mode",
                    tint =
                        if (mode == HomeMode.FAMILY)
                            Color.White
                        else
                            Color.DarkGray,
                    modifier = Modifier.size(26.dp)
                )
            }
        }

        //////////////////////////////////////
        // RIGHT — ACTIONS
        //////////////////////////////////////

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            // FILTER + BADGE
            Box {

                IconButton(
                    onClick = onFilterClick
                ) {
                    Icon(
                        Icons.Default.FilterList,
                        contentDescription = "Filter",
                        tint = Color.White
                    )
                }

                if (filtersActive) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(Color.Red, CircleShape)
                            .align(Alignment.TopEnd)
                    )
                }
            }

            Spacer(Modifier.width(8.dp))

            IconButton(
                onClick = onSearchClick
            ) {
                Icon(
                    Icons.Default.Search,
                    contentDescription = "Search",
                    tint = Color.White
                )
            }
        }
    }
}
