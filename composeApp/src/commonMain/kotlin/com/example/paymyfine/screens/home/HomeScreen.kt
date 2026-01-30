package com.example.paymyfine.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    state: HomeState,
    onModeChange: (HomeMode) -> Unit,
    onSearchClick: () -> Unit,
    onFilterClick: () -> Unit,
    onAddMemberClick: () -> Unit,
    onDeleteMemberClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
    ) {

        Column(modifier = Modifier.fillMaxSize()) {

            // ───────────── TOP BAR ─────────────
            HomeTopBar(
                mode = state.mode,
                hasActiveFilters = state.hasActiveFilters,
                onModeChange = onModeChange,
                onSearchClick = onSearchClick,
                onFilterClick = onFilterClick
            )

            // ───────────── MAIN CONTENT ─────────────
            Surface(
                modifier = Modifier.fillMaxSize(),
                shape = MaterialTheme.shapes.extraLarge,
                color = Color.White
            ) {
                when (state.mode) {
                    HomeMode.INDIVIDUAL -> IndividualHomeContent(state)
                    HomeMode.FAMILY -> FamilyHomeContent()
                }
            }
        }

        // ───────────── FLOATING ACTION BUTTONS ─────────────
        HomeFabMenu(
            mode = state.mode,
            onAddClick = onAddMemberClick,
            onDeleteClick = onDeleteMemberClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        )

        // ───────────── GLOBAL LOADING ─────────────
        /*if (state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = MaterialTheme.colorScheme.onPrimary
            )
        }*/
    }
}

/* ───────────────── FAB MENU ───────────────── */

@Composable
private fun HomeFabMenu(
    mode: HomeMode,
    onAddClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.End
    ) {
        FloatingActionButton(onClick = onAddClick) {
            Text("+")
        }

        if (mode == HomeMode.FAMILY) {
            Spacer(Modifier.height(8.dp))
            FloatingActionButton(
                onClick = onDeleteClick,
                containerColor = MaterialTheme.colorScheme.error
            ) {
                Text("–")
            }
        }
    }
}

/* ───────────────── TOP BAR ───────────────── */

@Composable
private fun HomeTopBar(
    mode: HomeMode,
    hasActiveFilters: Boolean,
    onModeChange: (HomeMode) -> Unit,
    onSearchClick: () -> Unit,
    onFilterClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = if (mode == HomeMode.INDIVIDUAL) "My Fines" else "Family Fines",
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.titleMedium
        )

        Row {
            IconButton(onClick = onSearchClick) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
            IconButton(onClick = onFilterClick) {
                Icon(
                    imageVector = Icons.Default.FilterList,
                    contentDescription = "Filter",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

/* ───────────────── INDIVIDUAL MODE ───────────────── */

@Composable
private fun IndividualHomeContent(state: HomeState) {
    when {
        state.isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        state.errorMessage != null -> {
            Text(
                text = state.errorMessage,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(16.dp)
            )
        }

        state.fines.isEmpty() -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No fines found",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        else -> {
            IndividualFinesList(
                fines = state.fines,
                onFineClick = { /* TODO */ }
            )
        }
    }
}

/* ───────────────── FAMILY MODE ───────────────── */

@Composable
private fun FamilyHomeContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Family Mode (coming soon)")
    }
}
