package com.example.paymyfine.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.paymyfine.data.cart.CartManager
import com.example.paymyfine.data.cart.CartProvider
import com.example.paymyfine.data.payment.PaymentProvider
import com.example.paymyfine.screens.cart.CartScreen
import com.example.paymyfine.screens.details.InfringementDetailsContent
import com.example.paymyfine.screens.profile.ProfileScreen
import com.example.paymyfine.ui.ProfileBar
import com.russhwolf.settings.Settings
import io.ktor.client.HttpClient





/* ================= HOME SCREEN ================= */

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
    onFineClick: (IForceItem) -> Unit
) {

    val navigator = LocalNavigator.current
    val client = remember { HttpClientFactory.create(sessionStore) }

    var selectedFine by remember { mutableStateOf<IForceItem?>(null) }
    val cartManager =
        remember { CartProvider.get(sessionStore) }



    BoxWithConstraints(Modifier.fillMaxSize()) {

        val isDesktop = maxWidth > 700.dp

        if (isDesktop) {

            Row(Modifier.fillMaxSize()) {

                // LEFT SIDE
                Column(
                    modifier = Modifier
                        .weight(0.5f)
                        .fillMaxHeight()
                ) {

                    Row(Modifier.padding(12.dp)) {

                        Button(
                            onClick = { onModeChange(HomeMode.INDIVIDUAL) }
                        ) { Text("Individual") }

                        Spacer(Modifier.width(8.dp))

                        Button(
                            onClick = { onModeChange(HomeMode.FAMILY) }
                        ) { Text("Family") }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {

                        when (state.mode) {

                            HomeMode.INDIVIDUAL ->
                                IndividualFinesList(
                                    fines = state.fines,
                                    onFineClick = { selectedFine = it }
                                )

                            HomeMode.FAMILY ->
                                FamilyHomeContent(
                                    members = members,
                                    familyFines = familyFines,
                                    onExpand = onExpand,
                                    onFineClick = { selectedFine = it }
                                )
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

            Column(
                Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.primary)
            ) {

                HomeHeader(
                    mode = state.mode,
                    filtersActive = false,
                    cartManager = cartManager,
                    onModeChange = onModeChange,
                    onSearchClick = onSearchClick,
                    onFilterClick = onFilterClick,
                    onCartClick = {
                        navigator?.push(
                            CartScreen(sessionStore, PaymentProvider.vm)
                        )

                    }
                )



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
                                navigator?.push(ProfileScreen())
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
                                        fines = state.fines,
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

        if (state.isLoading) {
            Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
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

@Composable
private fun HomeFabMenu(
    mode: HomeMode,
    onAddClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier, horizontalAlignment = Alignment.End) {

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

/* ================= DESKTOP ================= */

/*@Composable
fun DesktopLayout(
    state: HomeState,
    selectedFine: IForceItem?,
    onFineClick: (IForceItem) -> Unit
) {
    Row(Modifier.fillMaxSize()) {

        Box(Modifier.weight(1f)) {
            IndividualFinesList(
                fines = state.fines,
                onFineClick = onFineClick
            )
        }

        Box(Modifier.weight(1f)) {
            FineDetailsPane(
                fine = selectedFine,
                client = client,
                sessionStore = sessionStore
            )
        }

    }
}*/

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

@Composable
fun HomeHeader(
    mode: HomeMode,
    filtersActive: Boolean,
    cartManager: CartManager,
    onModeChange: (HomeMode) -> Unit,
    onSearchClick: () -> Unit,
    onFilterClick: () -> Unit,
    onCartClick: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFFFC107)) // ⭐ brand yellow
            .padding(top = 12.dp, start = 16.dp, end = 16.dp, bottom = 8.dp)
    ) {

        //////////////////////////////////////
        // TOP ROW — LOGO + ACTIONS
        //////////////////////////////////////

        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Column {
                Text(
                    "Paymyfines",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.Black
                )

                Text(
                    "best way to pay",
                    fontSize = 11.sp,
                    color = Color.Black
                )
            }

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
                            tint = Color.White
                        )
                    }
                }





                IconButton(onClick = { }) {
                    Icon(
                        Icons.Default.MoreVert,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        //////////////////////////////////////
        // SECOND ROW — MODE + SEARCH/FILTER
        //////////////////////////////////////

        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            // LEFT: MODE TOGGLE
            Row {

                IconButton(
                    onClick = { onModeChange(HomeMode.INDIVIDUAL) }
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = if (mode == HomeMode.INDIVIDUAL)
                            Color.White
                        else
                            Color.DarkGray
                    )
                }

                IconButton(
                    onClick = { onModeChange(HomeMode.FAMILY) }
                ) {
                    Icon(
                        Icons.Default.Home,
                        contentDescription = null,
                        tint = if (mode == HomeMode.FAMILY)
                            Color.White
                        else
                            Color.DarkGray
                    )
                }
            }

            // RIGHT: FILTER + SEARCH
            Row {

                Box {

                    IconButton(onClick = onFilterClick) {
                        Icon(
                            Icons.Default.FilterList,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }

                    if (filtersActive) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
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
                        tint = Color.White
                    )
                }
            }
        }
    }
}



