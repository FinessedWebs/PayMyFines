package com.example.paymyfine.screens.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import com.example.paymyfine.data.infringements.*
import com.example.paymyfine.data.network.*
import com.example.paymyfine.data.session.SessionStore
import com.example.paymyfine.data.family.*
import com.russhwolf.settings.Settings
import cafe.adriel.voyager.navigator.LocalNavigator
import com.example.paymyfine.ui.BottomNavBar


class HomeScreenRoute : Screen {

    @Composable
    override fun Content() {

        val settings = remember { Settings() }
        val sessionStore = remember { SessionStore(settings) }

        val client = remember {
            HttpClientFactory.create(sessionStore)
        }

        val baseUrl = BaseUrlProvider.get()

        // ---------- Infringements ----------
        val infringementService = remember {
            InfringementService(client, baseUrl)
        }

        val repo = remember {
            InfringementRepository(
                infringementService,
                sessionStore
            )
        }

        // ---------- Family ----------
        val familyService = remember {
            FamilyService(client, baseUrl)
        }

        val familyRepo = remember {
            FamilyRepository(
                familyService,
                sessionStore
            )
        }

        val vm = remember {
            HomeViewModel(settings, repo, familyRepo)
        }

        val state by vm.uiState
        val idNumber = sessionStore.getIdNumber()

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

        val navigator = LocalNavigator.current!!

        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            Box(
                modifier = Modifier.weight(1f)
            ) {
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

            BottomNavBar(navigator)
        }


    }
}
