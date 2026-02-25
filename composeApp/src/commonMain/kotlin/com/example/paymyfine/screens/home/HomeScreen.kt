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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.ToggleFloatingActionButtonDefaults.animateIcon
import androidx.compose.runtime.saveable.rememberSaveable
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


/* ================= HOME SCREEN ================= */

@OptIn(ExperimentalMaterial3Api::class)
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

// 🔎 FILTER STATE (ADD HERE — inside HomeScreen, before BoxWithConstraints)
    var activeFilters by remember { mutableStateOf(FilterOptions()) }

    var showFilterSheet by remember { mutableStateOf(false) }

// Derived filtered fines (search + advanced filters combined)
    val filteredFines by remember(
        state.fines,
        state.searchQuery,
        activeFilters
    ) {
        derivedStateOf {
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
                        .fillMaxWidth()
                ) {

                    // 🔍 Search Bar (Desktop)
                    FineSearchBar(
                        query = state.searchQuery,
                        onQueryChange = onSearchQueryChange
                    )



                    if (state.searchQuery.isNotBlank()) {
                        Text(
                            text = "${filteredFines.size} results",
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.Gray
                        )
                    }

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
                                    onFineClick = { selectedFine = it }
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
                                        }
                                    )
                            }
                        }
                    }
                }
            }
        }

        if (showFilterSheet) {
            ModalBottomSheet(
                onDismissRequest = { showFilterSheet = false }
            ) {
                FilterBottomSheetContent(
                    currentFilters = activeFilters,
                    onApply = { newFilters ->
                        activeFilters = newFilters
                        showFilterSheet = false
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
            onDeleteClick = onDeleteMemberClick,
            onLogoutClick = {
                sessionStore.clear()
                navigator?.replaceAll(LoginScreenRoute(sessionStore))
            },
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
    onQueryChange: (String) -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        placeholder = { Text("Search fines...") },
        singleLine = true,
        leadingIcon = {
            Icon(Icons.Default.Search, null)
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = Color.Gray,
            cursorColor = MaterialTheme.colorScheme.primary,   // 🔥 FIX
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
    onFineClick: (IForceItem) -> Unit
) {

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        items(members) { member ->

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

                    Text(
                        "${member.fullName} ${member.surname}",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        member.relationship ?: "",
                        color = Color.Gray
                    )

                    if (expanded) {

                        Spacer(Modifier.height(12.dp))

                        val fines =
                            familyFines[member.idNumber].orEmpty()

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

data class FilterOptions(
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
}

@Composable
fun HomeHeader(
    mode: HomeMode,
    filtersActive: Boolean,
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
                modifier = Modifier.weight(1f) // 🔥 Prevent full-width takeover
            )

            Spacer(Modifier.width(8.dp))

            // RIGHT: FILTER + SEARCH
            Row(verticalAlignment = Alignment.CenterVertically) {

                Box {

                    IconButton(onClick = onFilterClick) {
                        Icon(
                            Icons.Default.FilterList,
                            contentDescription = null,
                            tint = onPrimary
                        )
                    }

                    if (filtersActive) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(
                                    Color.Red,
                                    shape = CircleShape
                                )
                                .align(Alignment.TopEnd)
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



private fun List<IForceItem>.applyAdvancedFilters(
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
}

private fun isWithinRange(
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
}






