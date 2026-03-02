package com.example.paymyfine.screens.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import com.example.paymyfine.data.family.models.*
import com.example.paymyfine.data.fines.IForceItem
import com.example.paymyfine.data.network.HttpClientFactory
import com.example.paymyfine.data.session.SessionStore
import com.example.paymyfine.screens.details.InfringementDetailsScreen
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ToggleFloatingActionButtonDefaults.animateIcon
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.input.key.Key.Companion.R
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.annotation.InternalVoyagerApi
import cafe.adriel.voyager.navigator.internal.BackHandler
import com.example.paymyfine.data.auth.AuthService
import com.example.paymyfine.data.cart.CartManager
import com.example.paymyfine.data.cart.CartProvider
import com.example.paymyfine.data.filter.AmountRange
import com.example.paymyfine.data.filter.DateRangeFilter
import com.example.paymyfine.data.filter.FilterOptions
import com.example.paymyfine.data.filter.VehicleFilter
import com.example.paymyfine.data.filterByQuery
import com.example.paymyfine.data.network.BaseUrlProvider
import com.example.paymyfine.data.payment.PaymentProvider
import com.example.paymyfine.screens.cart.CartScreen
import com.example.paymyfine.screens.details.InfringementDetailsContent
import com.example.paymyfine.screens.login.LoginScreenRoute
import com.example.paymyfine.screens.profile.ProfileScreen
import com.example.paymyfine.screens.settings.SettingsScreenRoute
import com.example.paymyfine.ui.ProfileBar
import com.russhwolf.settings.Settings
import io.ktor.client.HttpClient
import org.jetbrains.compose.resources.painterResource
import paymyfine.composeapp.generated.resources.Res
import paymyfine.composeapp.generated.resources.ic_paymyfines_logo
import paymyfine.composeapp.generated.resources.paymyfines_text_logo_white_back_remove
import kotlinx.datetime.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.TimeSource
import com.example.paymyfine.data.applyAdvancedFilters


/* ================= HOME SCREEN ================= */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    state: HomeState,
    onModeChange: (HomeMode) -> Unit,
    onSearchClick: () -> Unit,
    onAddMemberClick: () -> Unit,
    onDeleteMemberClick: (FamilyMemberDto) -> Unit,
    onDismissDialog: () -> Unit,
    onSubmitFamily: (AddFamilyMemberRequest) -> Unit,
    sessionStore: SessionStore,
    members: List<FamilyMemberDto>,
    familyFines: Map<String, List<IForceItem>>,
    onExpand: (String) -> Unit,
    onFineClick: (IForceItem) -> Unit,
    onSearchQueryChange: (String) -> Unit,

    ) {

    val navigator = LocalNavigator.current
    val client = remember { HttpClientFactory.create(sessionStore) }


    var selectedFine by remember { mutableStateOf<IForceItem?>(null) }
    val cartManager =
        remember { CartProvider.get(sessionStore) }


    var fabExpanded by rememberSaveable { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var memberPendingDelete by remember { mutableStateOf<FamilyMemberDto?>(null) }

// 🔎 FILTER STATE (ADD HERE — inside HomeScreen, before BoxWithConstraints)
    var activeFilters by remember { mutableStateOf(FilterOptions()) }

    var showFilterSheet by remember { mutableStateOf(false) }
    // Extract unique values for filter chips
    val uniqueStatuses = remember(state.fines) {
        state.fines.mapNotNull { it.status }.distinct().sorted()
    }

    val uniqueAuthorities = remember(state.fines) {
        state.fines.mapNotNull { it.issuingAuthority }
            .map { it.substringAfter("COURT:", it).trim().takeIf { c -> c.isNotEmpty() } ?: it }
            .distinct()
            .sorted()
    }



// Derived filtered fines (search + advanced filters combined)
    val filteredFines: List<IForceItem> by remember(state.fines, state.searchQuery, activeFilters) {
        derivedStateOf<List<IForceItem>> {
            state.fines
                .filterByQuery(state.searchQuery)
                .applyAdvancedFilters(activeFilters)
        }
    }



    BoxWithConstraints(Modifier.fillMaxSize()) {

        val isDesktop = maxWidth > 700.dp

        if (isDesktop) {
            Row(Modifier.fillMaxSize()) {
                // LEFT SIDE
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    // DESKTOP HEADER WITH SEARCH + MODE TOGGLE + FILTER
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Search Bar (flexible width but not greedy)
                        FineSearchBar(
                            query = state.searchQuery,
                            onQueryChange = onSearchQueryChange,
                            modifier = Modifier.weight(1f, fill = false) // Don't force fill
                                .widthIn(max = 400.dp) // Max width constraint
                        )

                        // MODE TOGGLE BUTTONS
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { onModeChange(HomeMode.INDIVIDUAL) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (state.mode == HomeMode.INDIVIDUAL)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.surface,
                                    contentColor = if (state.mode == HomeMode.INDIVIDUAL)
                                        MaterialTheme.colorScheme.onPrimary
                                    else
                                        MaterialTheme.colorScheme.onSurface
                                )
                            ) {
                                Text("Individual")
                            }

                            Button(
                                onClick = { onModeChange(HomeMode.FAMILY) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (state.mode == HomeMode.FAMILY)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.surface,
                                    contentColor = if (state.mode == HomeMode.FAMILY)
                                        MaterialTheme.colorScheme.onPrimary
                                    else
                                        MaterialTheme.colorScheme.onSurface
                                )
                            ) {
                                Text("Family")
                            }
                        }

                        // FILTER BUTTON
                        Box {
                            IconButton(
                                onClick = { showFilterSheet = true }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FilterList,
                                    contentDescription = "Filter",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            // Red dot indicator
                            if (!activeFilters.isEmpty) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(Color.Red, CircleShape)
                                        .align(Alignment.TopEnd)
                                        .offset(x = (-4).dp, y = 4.dp)
                                )
                            }
                        }
                    }

                    // Results count
                    if (state.searchQuery.isNotBlank()) {
                        Text(
                            text = "${filteredFines.size} results",
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.Gray
                        )
                    }

                    // Fines List
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        when (state.mode) {
                            HomeMode.INDIVIDUAL -> {
                                if (filteredFines.isEmpty() && state.searchQuery.isNotBlank()) {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("No results found")
                                    }
                                } else {
                                    IndividualFinesList(
                                        fines = filteredFines,
                                        onFineClick = { selectedFine = it }
                                    )
                                }
                            }
                            HomeMode.FAMILY -> {
                                FamilyHomeContent(
                                    members = members,
                                    familyFines = familyFines,
                                    onExpand = onExpand,
                                    onFineClick = { selectedFine = it },
                                    onDeleteMember = onDeleteMemberClick   // ✅ ADD THIS LINE
                                )
                            }
                        }
                    }
                }

                // RIGHT SIDE
                Box(
                    modifier = Modifier
                        .weight(0.5f)
                        .fillMaxHeight()
                ) {
                    FineDetailsPane(
                        fine = selectedFine,
                        client = client,
                        sessionStore = sessionStore
                    )
                }
            }
        }





        else {

            var showSearch by remember { mutableStateOf(false) }

            Column(
                Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.primary)
            ) {

                HomeHeader(
                    mode = state.mode,
                    filtersActive = !activeFilters.isEmpty,
                    cartManager = cartManager,
                    onModeChange = onModeChange,
                    onSearchClick = { showSearch = !showSearch },
                    onFilterClick = { showFilterSheet = true },
                    onCartClick = {
                        navigator?.push(
                            CartScreen(sessionStore, PaymentProvider.vm)
                        )
                    },
                    onSettingsClick = {
                        navigator?.push(
                            SettingsScreenRoute(sessionStore)
                        )
                    }



                )


                if (showSearch) {
                    FineSearchBar(
                        query = state.searchQuery,
                        onQueryChange = { query ->
                            onSearchQueryChange(query)
                        }
                    )
                }



                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(
                        topStart = 24.dp,
                        topEnd = 24.dp
                    ),
                    color = Color.White
                ) {

                    Column(Modifier.fillMaxSize()) {

                        // ✅ PROFILE HEADER
                        ProfileBar(
                            fullName = sessionStore.getFullName() ?: "You",
                            email = sessionStore.getEmail() ?: "",
                            idNumber = sessionStore.getIdNumber() ?: "",
                            fineCount = state.fines.size,
                            onProfileClick = {
                                navigator?.push(
                                    ProfileScreen(
                                        sessionStore = sessionStore,
                                        authService = AuthService(
                                            HttpClientFactory.create(sessionStore),
                                            BaseUrlProvider.get()
                                        )
                                    )
                                )
                            }
                        )

                        // ✅ FINES AREA FILLS REST OF SCREEN
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                        ) {

                            when (state.mode) {

                                HomeMode.INDIVIDUAL ->
                                    IndividualFinesList(
                                        fines = filteredFines,
                                        onFineClick = {
                                            navigator?.push(
                                                InfringementDetailsScreen(
                                                    it,
                                                    client,
                                                    sessionStore
                                                )
                                            )
                                        }
                                    )

                                HomeMode.FAMILY ->
                                    FamilyHomeContent(
                                        members = members,
                                        familyFines = familyFines,
                                        onExpand = onExpand,
                                        onFineClick = {
                                            navigator?.push(
                                                InfringementDetailsScreen(
                                                    it,
                                                    client,
                                                    sessionStore
                                                )
                                            )
                                        },
                                        onDeleteMember = onDeleteMemberClick   // ✅ ADD THIS LINE
                                    )
                            }
                        }
                    }
                }
            }
        }

        // At the end of HomeScreen, before final closing braces:

// Filter Bottom Sheet
        if (showFilterSheet) {
            ModalBottomSheet(
                onDismissRequest = { showFilterSheet = false },
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
            ) {
                FilterBottomSheetContent(
                    currentFilters = activeFilters,
                    availableFines = state.fines,
                    uniqueStatuses = uniqueStatuses,
                    uniqueAuthorities = uniqueAuthorities,
                    onApply = { newFilters ->
                        activeFilters = newFilters
                        showFilterSheet = false
                    },
                    onClear = {
                        activeFilters = FilterOptions()
                    }
                )
            }
        }

        if (fabExpanded) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.2f))
                    .clickable { fabExpanded = false }
            )
        }




        HomeFabMenu(
            mode = state.mode,
            expanded = fabExpanded,
            onExpandedChange = { fabExpanded = it },
            onAddClick = onAddMemberClick,
            onDeleteClick = { showDeleteDialog = true },
            onLogoutClick = {
                sessionStore.clear()
                navigator?.replaceAll(LoginScreenRoute(sessionStore))
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        )

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                confirmButton = {},
                dismissButton = {
                    TextButton(
                        onClick = { showDeleteDialog = false }
                    ) {
                        Text("Cancel")
                    }
                },
                title = {
                    Text("Delete Family Member")
                },
                text = {
                    if (members.isEmpty()) {
                        Text("No family members found.")
                    } else {
                        Column {
                            members.forEach { member ->

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            showDeleteDialog = false
                                            memberPendingDelete = member
                                        }
                                        .padding(vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {

                                    Column {
                                        Text(
                                            "${member.fullName} ${member.surname}",
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                        Text(
                                            member.relationship ?: "",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.Gray
                                        )
                                    }

                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }

                                Divider()
                            }
                        }
                    }
                }
            )
        }

        memberPendingDelete?.let { member ->

            AlertDialog(
                onDismissRequest = { memberPendingDelete = null },
                confirmButton = {
                    TextButton(
                        onClick = {
                            onDeleteMemberClick(member)
                            memberPendingDelete = null
                        }
                    ) {
                        Text(
                            "Delete",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { memberPendingDelete = null }
                    ) {
                        Text("Cancel")
                    }
                },
                title = {
                    Text("Confirm Deletion")
                },
                text = {
                    Text(
                        "Are you sure you want to delete ${member.fullName} ${member.surname}? This action cannot be undone."
                    )
                }
            )
        }

        if (state.showAddDialog) {
            AddFamilyDialog(
                onDismiss = onDismissDialog,
                onSubmit = onSubmitFamily
            )
        }

        @OptIn(ExperimentalMaterial3ExpressiveApi::class)
        if (state.isLoading) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.05f)),
                contentAlignment = Alignment.Center
            ) {
                LoadingIndicator(
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }





}

@Composable
fun FilterBottomSheetContent(
    currentFilters: FilterOptions,
    onApply: (FilterOptions) -> Unit
) {
    // temporary minimal implementation so it compiles
    Column(
        Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {

        Text(
            text = "Filters",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {
                onApply(currentFilters)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Apply")
        }
    }
}

@Composable
fun FineSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .padding(vertical = 8.dp),
        placeholder = { Text("Search fines...") },
        singleLine = true,
        leadingIcon = {
            Icon(Icons.Default.Search, null)
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = Color.Gray,
            cursorColor = MaterialTheme.colorScheme.primary,
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black
        )
    )
}

/* ================= TOP BAR ================= */

@Composable
private fun HomeTopBar(
    mode: HomeMode,
    onModeChange: (HomeMode) -> Unit,
    onSearchClick: () -> Unit,
    onFilterClick: () -> Unit
) {
    Column(Modifier.fillMaxWidth().padding(12.dp)) {

        Row(
            Modifier.fillMaxWidth(),
            Arrangement.SpaceBetween,
            Alignment.CenterVertically
        ) {
            Text(
                if (mode == HomeMode.INDIVIDUAL) "My Fines" else "Family Fines",
                color = MaterialTheme.colorScheme.onPrimary
            )

            Row {
                IconButton(onClick = onSearchClick) {
                    Icon(Icons.Default.Search, null)
                }
                IconButton(onClick = onFilterClick) {
                    Icon(Icons.Default.FilterList, null)
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(onClick = { onModeChange(HomeMode.INDIVIDUAL) }) {
                Text("Individual")
            }

            Spacer(Modifier.width(8.dp))

            Button(onClick = { onModeChange(HomeMode.FAMILY) }) {
                Text("Family")
            }
        }
    }
}

/* ================= INDIVIDUAL ================= */

@Composable
private fun IndividualHomeContent(
    state: HomeState,
    onFineClick: (IForceItem) -> Unit
) {
    when {
        state.errorMessage != null ->
            Box(Modifier.fillMaxSize(), Alignment.Center) {
                Text(state.errorMessage)
            }

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


/* ================= FAMILY ================= */



@Composable
private fun FamilyHomeContent(
    members: List<FamilyMemberDto>,
    familyFines: Map<String, List<IForceItem>>,
    onExpand: (String) -> Unit,
    onFineClick: (IForceItem) -> Unit,
    onDeleteMember: (FamilyMemberDto) -> Unit
) {

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        items(
            items = members,
            key = { it.linkId }   // ✅ VERY IMPORTANT
        ) { member ->

            var expanded by rememberSaveable { mutableStateOf(false) }


            Card(
                onClick = {
                    expanded = !expanded
                    if (expanded) {
                        member.idNumber?.let(onExpand)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {



                Column(Modifier.padding(16.dp)) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                expanded = !expanded
                                if (expanded) {
                                    member.idNumber?.let(onExpand)
                                }
                            },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Column {
                            Text(
                                "${member.fullName} ${member.surname}",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                member.relationship ?: "",
                                color = Color.Gray
                            )
                        }

                        /*IconButton(
                            onClick = { onDeleteMember(member) }
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Delete Member",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }*/
                    }

                    if (expanded) {

                        Spacer(Modifier.height(12.dp))

                        val fines = familyFines[member.idNumber].orEmpty()

                        if (fines.isEmpty()) {
                            Text("No fines")
                        } else {
                            fines.forEach { fine ->
                                FineRow(
                                    fine = fine,
                                    onClick = { onFineClick(fine) }
                                )
                            }
                        }
                    }
                }
                }
            }
        }
    }






/* ================= FAB ================= */

@OptIn(ExperimentalMaterial3ExpressiveApi::class, InternalVoyagerApi::class)
@Composable
fun HomeFabMenu(
    mode: HomeMode,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onAddClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    val primaryColor = MaterialTheme.colorScheme.primary
    val errorColor = MaterialTheme.colorScheme.error
    val motion = MaterialTheme.motionScheme

    BackHandler(expanded) { onExpandedChange(false) }

    val scale by animateFloatAsState(
        targetValue = if (expanded) 1.1f else 1f,
        animationSpec = motion.fastSpatialSpec()
    )

    FloatingActionButtonMenu(
        modifier = modifier,
        expanded = expanded,
        button = {

            ToggleFloatingActionButton(
                checked = expanded,
                onCheckedChange = { onExpandedChange(!expanded) },
                modifier = Modifier.scale(scale),
                containerColor = { primaryColor }
            ) {

                if (this.checkedProgress > 0.5f) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close menu",
                        tint = Color.White
                    )
                } else {
                    Icon(
                        painter = painterResource(Res.drawable.ic_paymyfines_logo),
                        contentDescription = "Open menu",
                        tint = Color.Unspecified
                    )
                }
            }
        }
    ) {

        FloatingActionButtonMenuItem(
            onClick = {
                onExpandedChange(false)
                onAddClick()
            },
            containerColor = primaryColor,
            icon = {
                Icon(Icons.Default.PersonAdd, contentDescription = null)
            },
            text = { Text("Add Member") }
        )

        if (mode == HomeMode.FAMILY) {
            FloatingActionButtonMenuItem(
                onClick = {
                    onExpandedChange(false)
                    onDeleteClick()
                },
                containerColor = errorColor,
                icon = {
                    Icon(Icons.Default.Delete, contentDescription = null)
                },
                text = { Text("Delete Member") }
            )
        }

        FloatingActionButtonMenuItem(
            onClick = {
                onExpandedChange(false)
                onLogoutClick()
            },
            containerColor = errorColor,
            icon = {
                Icon(Icons.Default.Logout, contentDescription = null)
            },
            text = { Text("Logout") }
        )
    }
}



/* ================= DETAILS ================= */

@Composable
fun FineDetailsPane(
    fine: IForceItem?,
    client: HttpClient,
    sessionStore: SessionStore
) {

    if (fine == null) {
        Box(Modifier.fillMaxSize(), Alignment.Center) {
            Text("Select a fine")
        }
        return
    }

    InfringementDetailsContent(
        fine = fine,
        client = client,
        sessionStore = sessionStore
    )
}

/*data class FilterOptions(
    val statuses: List<String> = emptyList(),
    val severities: List<String> = emptyList(),
    val paymentFlags: List<String> = emptyList(),
    val dateRange: String? = null,
    val issuingAuthorities: List<String> = emptyList()
) {
    val isEmpty: Boolean
        get() =
            statuses.isEmpty() &&
                    severities.isEmpty() &&
                    paymentFlags.isEmpty() &&
                    dateRange == null &&
                    issuingAuthorities.isEmpty()
}*/

@Composable
fun HomeHeader(
    mode: HomeMode,
    filtersActive: Boolean = false,
    filterCount: Int = 0,
    cartManager: CartManager,
    onModeChange: (HomeMode) -> Unit,
    onSearchClick: () -> Unit,
    onFilterClick: () -> Unit,
    onCartClick: () -> Unit,
    onSettingsClick: () -> Unit
) {

    val primary = MaterialTheme.colorScheme.primary
    val onPrimary = MaterialTheme.colorScheme.onPrimary

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(primary)
            .padding(top = 12.dp, start = 16.dp, end = 16.dp, bottom = 8.dp)
    ) {

        //////////////////////////////////////////////////////
        // TOP ROW — LOGO + CART + SETTINGS
        //////////////////////////////////////////////////////

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Image(
                painter = painterResource(Res.drawable.paymyfines_text_logo_white_back_remove),
                contentDescription = "PayMyFines Logo",
                modifier = Modifier.height(30.dp),
                contentScale = ContentScale.Fit
            )

            Row(verticalAlignment = Alignment.CenterVertically) {

                val cart by cartManager.cartFlow.collectAsState()

                BadgedBox(
                    badge = {
                        androidx.compose.animation.AnimatedVisibility(
                            visible = cart.isNotEmpty(),
                            enter = fadeIn() + scaleIn(),
                            exit = fadeOut() + scaleOut()
                        ) {
                            Badge {
                                Text(
                                    if (cart.size > 99) "99+"
                                    else cart.size.toString()
                                )
                            }
                        }
                    }
                ) {
                    IconButton(onClick = onCartClick) {
                        Icon(
                            Icons.Default.ShoppingCart,
                            contentDescription = null,
                            tint = onPrimary
                        )
                    }
                }

                IconButton(onClick = onSettingsClick) {
                    Icon(
                        Icons.Default.MoreVert,
                        contentDescription = null,
                        tint = onPrimary
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        //////////////////////////////////////////////////////
        // SECOND ROW — MODE + FILTER + SEARCH
        //////////////////////////////////////////////////////

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // LEFT: MODE SELECTOR
            ModeButtonGroup(
                mode = mode,
                onModeChange = onModeChange,
                modifier = Modifier.weight(1f)
            )

            Spacer(Modifier.width(8.dp))

            // RIGHT: FILTER + SEARCH (SINGLE FILTER BUTTON)
            Row(verticalAlignment = Alignment.CenterVertically) {

                // SINGLE FILTER BUTTON WITH BADGE
                BadgedBox(
                    badge = {
                        if (filtersActive) {
                            Badge(
                                containerColor = Color.Red,
                                contentColor = Color.White
                            ) {
                                if (filterCount > 0) {
                                    Text(filterCount.toString())
                                }
                            }
                        }
                    }
                ) {
                    IconButton(onClick = onFilterClick) {
                        Icon(
                            Icons.Default.FilterList,
                            contentDescription = "Filter",
                            tint = onPrimary
                        )
                    }
                }

                IconButton(onClick = onSearchClick) {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = null,
                        tint = onPrimary
                    )
                }
            }
        }
    }
}




/*private fun List<IForceItem>.applyAdvancedFilters(
    filters: FilterOptions
): List<IForceItem> {

    return this.filter { fine ->

        val statusMatch =
            filters.statuses.isEmpty() ||
                    filters.statuses.any { fine.status?.contains(it, true) == true }

        val severityMatch =
            if (filters.severities.isEmpty()) true
            else {
                val amount = fine.amountDueInCents ?: 0
                val severity = when {
                    amount >= 100_000 -> "High"
                    amount >= 50_000 -> "Medium"
                    amount > 0 -> "Low"
                    else -> "Low"
                }
                severity in filters.severities
            }

        val paymentMatch =
            filters.paymentFlags.isEmpty() ||
                    filters.paymentFlags.any {
                        if (it == "Allowed") fine.paymentAllowed == true
                        else fine.paymentAllowed != true
                    }

        val dateMatch =
            if (filters.dateRange == null) true
            else isWithinRange(fine.offenceDate, filters.dateRange)

        val authorityMatch =
            filters.issuingAuthorities.isEmpty() ||
                    filters.issuingAuthorities.any {
                        fine.issuingAuthority?.contains(it, true) == true
                    }

        statusMatch &&
                severityMatch &&
                paymentMatch &&
                dateMatch &&
                authorityMatch
    }


}*/

/*private fun isWithinRange(
    offenceDate: String?,
    range: String?
): Boolean {

    if (offenceDate == null || range == null) return true

    return try {

        val offenceMillis = offenceDate.toLong()

        // Monotonic time in millis (multiplatform safe)
        val nowMillis = TimeSource.Monotonic.markNow().elapsedNow().inWholeMilliseconds

        val diffMillis = nowMillis - offenceMillis

        val rangeDays = range.toLong()

        diffMillis <= rangeDays.days.inWholeMilliseconds

    } catch (e: Exception) {
        true
    }
}*/
// ================= FILTER BOTTOM SHEET =================

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun FilterBottomSheetContent(
    currentFilters: FilterOptions,
    availableFines: List<IForceItem>,
    uniqueStatuses: List<String>,
    uniqueAuthorities: List<String>,
    onApply: (FilterOptions) -> Unit,
    onClear: () -> Unit = {}
) {
    var tempFilters by remember { mutableStateOf(currentFilters) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .navigationBarsPadding()
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Filter Fines",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            if (!tempFilters.isEmpty) {
                TextButton(onClick = {
                    tempFilters = FilterOptions()
                }) {
                    Text("Reset")
                }
            }
        }

        if (tempFilters.activeFilterCount > 0) {
            Text(
                text = "${tempFilters.activeFilterCount} active",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Divider(modifier = Modifier.padding(vertical = 16.dp))

        // Filter options
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Status Filter
            if (uniqueStatuses.isNotEmpty()) {
                item {
                    FilterSection(
                        title = "Status",
                        icon = Icons.Default.Warning,
                        expanded = true
                    ) {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            uniqueStatuses.forEach { status ->
                                FilterChip(
                                    selected = status in tempFilters.statuses,
                                    onClick = {
                                        tempFilters = tempFilters.copy(
                                            statuses = if (status in tempFilters.statuses) {
                                                tempFilters.statuses - status
                                            } else {
                                                tempFilters.statuses + status
                                            }
                                        )
                                    },
                                    label = { Text(status) },
                                    leadingIcon = if (status in tempFilters.statuses) {
                                        { Icon(Icons.Default.Check, null, Modifier.size(16.dp)) }
                                    } else null
                                )
                            }
                        }
                    }
                }
            }

            // Amount Range Filter
            item {
                FilterSection(
                    title = "Amount Range",
                    icon = Icons.Default.Money,
                    expanded = true
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        AmountRange.entries.forEach { range ->
                            val label = when (range) {
                                AmountRange.LOW -> "Low (< R500)"
                                AmountRange.MEDIUM -> "Medium (R500 - R1000)"
                                AmountRange.HIGH -> "High (> R1000)"
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        tempFilters = tempFilters.copy(
                                            amountRanges = if (range in tempFilters.amountRanges) {
                                                tempFilters.amountRanges - range
                                            } else {
                                                tempFilters.amountRanges + range
                                            }
                                        )
                                    }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = range in tempFilters.amountRanges,
                                    onCheckedChange = null
                                )
                                Text(
                                    text = label,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Payment Status Filter
            item {
                FilterSection(
                    title = "Payment Status",
                    icon = Icons.Default.Check,
                    expanded = true
                ) {
                    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                        SegmentedButton(
                            selected = tempFilters.paymentAllowed == null,
                            onClick = { tempFilters = tempFilters.copy(paymentAllowed = null) },
                            shape = SegmentedButtonDefaults.itemShape(0, 3)
                        ) { Text("All") }

                        SegmentedButton(
                            selected = tempFilters.paymentAllowed == true,
                            onClick = { tempFilters = tempFilters.copy(paymentAllowed = true) },
                            shape = SegmentedButtonDefaults.itemShape(1, 3)
                        ) { Text("Allowed") }

                        SegmentedButton(
                            selected = tempFilters.paymentAllowed == false,
                            onClick = { tempFilters = tempFilters.copy(paymentAllowed = false) },
                            shape = SegmentedButtonDefaults.itemShape(2, 3)
                        ) { Text("Blocked") }
                    }
                }
            }

            // Date Range Filter
            item {
                FilterSection(
                    title = "Date Range",
                    icon = Icons.Default.DateRange,
                    expanded = true
                ) {
                    Column {
                        DateRangeFilter.entries.forEach { range ->
                            val label = when (range) {
                                DateRangeFilter.LAST_30_DAYS -> "Last 30 days"
                                DateRangeFilter.LAST_3_MONTHS -> "Last 3 months"
                                DateRangeFilter.LAST_6_MONTHS -> "Last 6 months"
                                DateRangeFilter.LAST_YEAR -> "Last year"
                                DateRangeFilter.ALL_TIME -> "All time"
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        tempFilters = tempFilters.copy(
                                            dateRange = if (tempFilters.dateRange == range) null else range
                                        )
                                    }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = tempFilters.dateRange == range,
                                    onClick = null
                                )
                                Text(
                                    text = label,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Vehicle Filter
            item {
                FilterSection(
                    title = "Vehicle",
                    icon = Icons.Default.Person,
                    expanded = true
                ) {
                    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                        SegmentedButton(
                            selected = tempFilters.vehicleFilter == VehicleFilter.ALL,
                            onClick = { tempFilters = tempFilters.copy(vehicleFilter = VehicleFilter.ALL) },
                            shape = SegmentedButtonDefaults.itemShape(0, 3)
                        ) { Text("All") }

                        SegmentedButton(
                            selected = tempFilters.vehicleFilter == VehicleFilter.WITH_VEHICLE,
                            onClick = { tempFilters = tempFilters.copy(vehicleFilter = VehicleFilter.WITH_VEHICLE) },
                            shape = SegmentedButtonDefaults.itemShape(1, 3)
                        ) { Text("With Vehicle") }

                        SegmentedButton(
                            selected = tempFilters.vehicleFilter == VehicleFilter.WITHOUT_VEHICLE,
                            onClick = { tempFilters = tempFilters.copy(vehicleFilter = VehicleFilter.WITHOUT_VEHICLE) },
                            shape = SegmentedButtonDefaults.itemShape(2, 3)
                        ) { Text("No Vehicle") }
                    }
                }
            }

            // Issuing Authority Filter
            if (uniqueAuthorities.isNotEmpty()) {
                item {
                    FilterSection(
                        title = "Court/Municipality",
                        icon = Icons.Default.FilterList,
                        expanded = false
                    ) {
                        Column {
                            uniqueAuthorities.take(5).forEach { authority ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            tempFilters = tempFilters.copy(
                                                issuingAuthorities = if (authority in tempFilters.issuingAuthorities) {
                                                    tempFilters.issuingAuthorities - authority
                                                } else {
                                                    tempFilters.issuingAuthorities + authority
                                                }
                                            )
                                        }
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = authority in tempFilters.issuingAuthorities,
                                        onCheckedChange = null
                                    )
                                    Text(
                                        text = authority,
                                        modifier = Modifier.padding(start = 8.dp),
                                        maxLines = 1
                                    )
                                }
                            }
                            if (uniqueAuthorities.size > 5) {
                                Text(
                                    text = "+${uniqueAuthorities.size - 5} more",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(start = 32.dp, top = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Apply Button
        Button(
            onClick = { onApply(tempFilters) },
            modifier = Modifier.fillMaxWidth(),
            enabled = tempFilters != currentFilters || !tempFilters.isEmpty
        ) {
            Text("Apply Filters")
        }
    }
}

@Composable
private fun FilterSection(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    expanded: Boolean = true,
    content: @Composable () -> Unit
) {
    var isExpanded by remember { mutableStateOf(expanded) }
    val rotation by animateFloatAsState(targetValue = if (isExpanded) 180f else 0f)

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isExpanded = !isExpanded }
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Icon(
                imageVector = Icons.Default.KeyboardArrowUp,
                contentDescription = null,
                modifier = Modifier.rotate(rotation)
            )
        }

        AnimatedVisibility(visible = isExpanded) {
            Box(modifier = Modifier.padding(start = 28.dp, top = 8.dp, bottom = 8.dp)) {
                content()
            }
        }
    }
}





