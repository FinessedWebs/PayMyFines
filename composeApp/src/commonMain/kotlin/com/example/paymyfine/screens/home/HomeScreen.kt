package com.example.paymyfine.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.BoxWithConstraints
import com.example.paymyfine.data.family.models.*
import com.example.paymyfine.data.fines.IForceItem
import org.jetbrains.compose.resources.painterResource
import paymyfine.composeapp.generated.resources.*

@Composable
fun HomeScreen(
    state: HomeState,
    onModeChange: (HomeMode) -> Unit,
    onSearchClick: () -> Unit,
    onFilterClick: () -> Unit,
    onAddMemberClick: () -> Unit,
    onDeleteMemberClick: () -> Unit,
    onDismissDialog: () -> Unit,
    onSubmitFamily: (AddFamilyMemberRequest) -> Unit,
    onFineClick: (IForceItem) -> Unit
) {

    var selectedFine by remember { mutableStateOf<IForceItem?>(null) }

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {

        val isDesktop = maxWidth > 700.dp

        if (isDesktop) {

            // ---------- DESKTOP ----------
            DesktopLayout(
                state = state,
                selectedFine = selectedFine,
                onFineClick = {
                    selectedFine = it
                    onFineClick(it)
                }
            )

        } else {

            // ---------- MOBILE ----------
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.primary)
            ) {

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
                    IndividualHomeContent(
                        state = state,
                        onFineClick = onFineClick
                    )
                }
            }
        }

        // ---------- GLOBAL FAB ----------
        HomeFabMenu(
            mode = state.mode,
            onAddClick = onAddMemberClick,
            onDeleteClick = onDeleteMemberClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        )

        // ---------- ADD DIALOG ----------
        if (state.showAddDialog) {
            AddFamilyDialog(
                onDismiss = onDismissDialog,
                onSubmit = onSubmitFamily
            )
        }

        // ---------- LOADER ----------
        if (state.isLoading) {
            Box(
                Modifier.fillMaxSize(),
                Alignment.Center
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
private fun IndividualHomeContent(
    state: HomeState,
    onFineClick: (IForceItem) -> Unit
) {
    when {
        state.errorMessage != null ->
            Text(state.errorMessage)

        state.fines.isEmpty() ->
            Box(Modifier.fillMaxSize(), Alignment.Center) {
                Text("No fines")
            }

        else ->
            IndividualFinesList(
                fines = state.fines,
                onFineClick = onFineClick
            )
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

@Composable
fun DesktopLayout(
    state: HomeState,
    selectedFine: IForceItem?,
    onFineClick: (IForceItem) -> Unit
) {
    Row(Modifier.fillMaxSize()) {

        // NAV 10%
        NavigationRail(
            modifier = Modifier.fillMaxHeight().weight(0.1f)
        ) {
            NavigationRailItem(
                selected = true,
                onClick = {},
                icon = { Icon(painterResource(Res.drawable.ic_home), null) }
            )
            NavigationRailItem(
                selected = false,
                onClick = {},
                icon = { Icon(painterResource(Res.drawable.ic_info), null) }
            )
            NavigationRailItem(
                selected = false,
                onClick = {},
                icon = { Icon(painterResource(Res.drawable.ic_notifications), null) }
            )
        }

        // LIST 40%
        Box(
            Modifier.fillMaxHeight().weight(0.4f)
        ) {
            IndividualFinesList(
                fines = state.fines,
                onFineClick = onFineClick
            )
        }

        // DETAILS 50%
        Box(
            Modifier.fillMaxHeight().weight(0.5f)
        ) {
            FineDetailsPane(selectedFine)
        }
    }
}


@Composable
fun MobileLayout(
    state: HomeState,
    onFineClick: (IForceItem) -> Unit
) {
    IndividualFinesList(
        fines = state.fines,
        onFineClick = onFineClick
    )
}

@Composable
fun FineDetailsPane(fine: IForceItem?) {

    if (fine == null) {
        Box(Modifier.fillMaxSize(), Alignment.Center) {
            Text("Select a fine")
        }
        return
    }

    Column(
        Modifier.fillMaxSize().padding(16.dp)
    ) {
        Text("Notice: ${fine.noticeNumber}")
        Text("Location: ${fine.offenceLocation}")
        Text("Date: ${fine.offenceDate}")
        Text("Amount: R${(fine.amountDueInCents ?: 0)/100.0}")
        Spacer(Modifier.height(12.dp))
        Text("Status: ${fine.status}")
        Text("Authority: ${fine.issuingAuthority}")
    }
}


@Composable
fun BottomNavBarVertical() {

    NavigationRail {

        NavigationRailItem(
            selected = true,
            onClick = {},
            icon = { Icon(painterResource(Res.drawable.ic_home), null) }
        )

        NavigationRailItem(
            selected = false,
            onClick = {},
            icon = { Icon(painterResource(Res.drawable.ic_info), null) }
        )

        NavigationRailItem(
            selected = false,
            onClick = {},
            icon = { Icon(painterResource(Res.drawable.ic_notifications), null) }
        )
    }
}



