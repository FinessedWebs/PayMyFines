package com.example.paymyfine.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.paymyfine.data.family.models.FamilyMemberDto
import androidx.compose.foundation.lazy.items
import com.example.paymyfine.data.family.models.AddFamilyMemberRequest


@Composable
fun HomeScreen(
    state: HomeState,
    onModeChange: (HomeMode) -> Unit,
    onSearchClick: () -> Unit,
    onFilterClick: () -> Unit,
    onAddMemberClick: () -> Unit,
    onDeleteMemberClick: () -> Unit,
    onDismissDialog: () -> Unit,
    onSubmitFamily: (AddFamilyMemberRequest) -> Unit

)
 {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            HomeTopBar(
                mode = state.mode,
                hasActiveFilters = state.hasActiveFilters,
                onModeChange = onModeChange,
                onSearchClick = onSearchClick,
                onFilterClick = onFilterClick
            )

            Surface(
                modifier = Modifier.fillMaxSize(),
                shape = MaterialTheme.shapes.extraLarge,
                color = Color.White
            ) {
                when (state.mode) {
                    HomeMode.INDIVIDUAL -> IndividualHomeContent(state)
                    HomeMode.FAMILY -> FamilyHomeContent(state.familyMembers)

                }
            }
        }

        HomeFabMenu(
            mode = state.mode,
            onAddClick = onAddMemberClick,
            onDeleteClick = onDeleteMemberClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        )

        if (state.showAddDialog) {
            AddFamilyDialog(
                onDismiss = onDismissDialog,
                onSubmit = onSubmitFamily
            )
        }

        // ✅ ADD IT HERE (LAST ITEM IN BOX)
        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

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



/* ───────────────── TOP BAR (WITH MODE BUTTONS) ───────────────── */

@Composable
private fun HomeTopBar(
    mode: HomeMode,
    hasActiveFilters: Boolean,
    onModeChange: (HomeMode) -> Unit,
    onSearchClick: () -> Unit,
    onFilterClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if (mode == HomeMode.INDIVIDUAL) "My Fines" else "Family Fines",
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.titleMedium
            )

            Row {
                IconButton(onClick = onSearchClick) {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
                IconButton(onClick = onFilterClick) {
                    Icon(
                        Icons.Default.FilterList,
                        contentDescription = "Filter",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        // ✅ MODE BUTTONS
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {

            Button(
                onClick = { onModeChange(HomeMode.INDIVIDUAL) },
                colors = ButtonDefaults.buttonColors(
                    containerColor =
                        if (mode == HomeMode.INDIVIDUAL)
                            MaterialTheme.colorScheme.secondary
                        else Color.LightGray
                )
            ) {
                Text("Individual")
            }

            Spacer(Modifier.width(8.dp))

            Button(
                onClick = { onModeChange(HomeMode.FAMILY) },
                colors = ButtonDefaults.buttonColors(
                    containerColor =
                        if (mode == HomeMode.FAMILY)
                            MaterialTheme.colorScheme.secondary
                        else Color.LightGray
                )
            ) {
                Text("Family")
            }
        }
    }
}

/* ───────────────── INDIVIDUAL MODE ───────────────── */

@Composable
private fun IndividualHomeContent(state: HomeState) {
    when {
        state.isLoading -> {
            Box(Modifier.fillMaxSize(), Alignment.Center) {
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
            Box(Modifier.fillMaxSize(), Alignment.Center) {
                Text("No fines found")
            }
        }

        else -> {
            IndividualFinesList(
                fines = state.fines,
                onFineClick = {}
            )
        }
    }
}

/* ───────────────── FAMILY MODE ───────────────── */

@Composable
private fun FamilyHomeContent(
    members: List<FamilyMemberDto>
) {
    if (members.isEmpty()) {
        Box(Modifier.fillMaxSize(), Alignment.Center) {
            Text("No family members yet")
        }
        return
    }

    LazyColumn {
        items(
            items = members,
            key = { it.linkId } // ✅ STABLE KEY
        ) { m ->

            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = m.nickname ?: "${m.fullName} ${m.surname}",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = "${m.relationship} • ${m.idNumber}",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Divider()
        }
    }
}


