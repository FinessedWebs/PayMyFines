package com.example.paymyfine.screens.home


import androidx.compose.runtime.*
import cafe.adriel.voyager.core.screen.Screen
import com.example.paymyfine.data.infringements.InfringementService
import com.example.paymyfine.data.network.BaseUrlProvider
import com.example.paymyfine.data.network.HttpClientFactory
import com.example.paymyfine.data.session.SessionStore
import com.russhwolf.settings.Settings
import com.example.paymyfine.data.family.FamilyService
import com.example.paymyfine.data.family.FamilyRepository
import com.example.paymyfine.data.infringements.InfringementRepository


class HomeScreenRoute : Screen {

    @Composable
    override fun Content() {

        val settings = remember { Settings() }
        val sessionStore = remember { SessionStore(settings) }

        // ✅ Shared client + baseUrl
        val client = remember { HttpClientFactory.create(sessionStore) }
        val baseUrl = remember { BaseUrlProvider.get() }

        // ✅ Infringements
        val service = remember {
            InfringementService(client, baseUrl)
        }

        val repo = remember { InfringementRepository(service) }

        // ✅ Family
        val familyService = remember {
            FamilyService(client, baseUrl)
        }

        val familyRepo = remember {
            FamilyRepository(familyService)
        }

        // ✅ ViewModel
        val vm = remember {
            HomeViewModel(settings, repo, familyRepo)
        }

        val state by vm.uiState
        val idNumber = sessionStore.getIdNumber()

        var showDialog by remember { mutableStateOf(false) }


        LaunchedEffect(state.mode, idNumber) {

            if (state.mode == HomeMode.INDIVIDUAL) {
                idNumber?.let {
                    println("LOADING FINES for $it")
                    vm.loadOpenFines(
                        force = true,
                        silent = true // 🔥 key fix
                    )

                }
            }

            if (state.mode == HomeMode.FAMILY) {
                vm.loadFamily()
            }
        }



        HomeScreen(
            state = state,
            onModeChange = vm::switchMode,
            onSearchClick = {},
            onFilterClick = {},
            onAddMemberClick = vm::showAddDialog,
            onDeleteMemberClick = {},
            onDismissDialog = vm::hideAddDialog,
            onSubmitFamily = vm::addFamily
        )


    }
}
