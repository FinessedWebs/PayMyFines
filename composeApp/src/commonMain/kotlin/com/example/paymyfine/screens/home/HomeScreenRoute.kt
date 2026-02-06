package com.example.paymyfine.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import cafe.adriel.voyager.core.screen.Screen
import com.example.paymyfine.data.family.*
import com.example.paymyfine.data.infringements.*
import com.example.paymyfine.data.network.*
import com.example.paymyfine.data.session.SessionStore
import com.example.paymyfine.ui.ResponsiveScreenShell
import com.russhwolf.settings.Settings

class HomeScreenRoute : Screen {

    @Composable
    override fun Content() {

        // ---------- SETTINGS / SESSION ----------
        val settings = remember { Settings() }
        val sessionStore = remember { SessionStore(settings) }

        val client = remember {
            HttpClientFactory.create(sessionStore)
        }

        val baseUrl = BaseUrlProvider.get()

        // ---------- INFRINGEMENTS ----------
        val infringementService = remember {
            InfringementService(client, baseUrl)
        }

        val repo = remember {
            InfringementRepository(
                infringementService,
                sessionStore
            )
        }

        // ---------- FAMILY ----------
        val familyService = remember {
            FamilyService(client, baseUrl)
        }

        val familyRepo = remember {
            FamilyRepository(
                familyService,
                sessionStore
            )
        }

        // ---------- VIEWMODEL ----------
        val vm = remember {
            HomeViewModel(settings, repo, familyRepo)
        }

        val state by vm.uiState
        val idNumber = sessionStore.getIdNumber()

        // ---------- LOAD DATA ----------
        LaunchedEffect(state.mode, idNumber) {

            if (state.mode == HomeMode.INDIVIDUAL) {
                idNumber?.let {
                    vm.loadOpenFines(
                        force = true,
                        silent = true
                    )
                }
            }

            if (state.mode == HomeMode.FAMILY) {
                vm.loadFamily()
            }
        }

        // ---------- RESPONSIVE LAYOUT ----------
        ResponsiveScreenShell {

            HomeScreen(
                state = state,
                onModeChange = vm::switchMode,
                onSearchClick = {},
                onFilterClick = {},
                onAddMemberClick = vm::showAddDialog,
                onDeleteMemberClick = {},
                onDismissDialog = vm::hideAddDialog,
                onSubmitFamily = vm::addFamily,
                sessionStore = sessionStore,
                members = state.familyMembers,
                familyFines = vm.familyFines,
                onExpand = vm::loadFinesForMember,
                onFineClick = vm::selectFine
            )
        }
    }
}
