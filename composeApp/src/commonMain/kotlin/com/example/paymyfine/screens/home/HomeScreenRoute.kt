package com.example.paymyfine.screens.home

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.screen.Screen
import com.example.paymyfine.data.infringements.InfringementRepository
import com.example.paymyfine.data.infringements.InfringementService
import com.example.paymyfine.data.network.BaseUrlProvider
import com.example.paymyfine.data.network.HttpClientFactory
import com.russhwolf.settings.Settings

class HomeScreenRoute : Screen {

    @Composable
    override fun Content() {

        val settings = remember { Settings() }

        val service = remember {
            InfringementService(
                HttpClientFactory.create(),
                BaseUrlProvider.get()
            )
        }

        val repo = remember { InfringementRepository(service) }

        val vm = remember { HomeViewModel(settings, repo) }

        val state by vm.uiState

        LaunchedEffect(state.mode) {
            if (state.mode == HomeMode.INDIVIDUAL && state.fines.isEmpty()) {
                vm.loadIndividual(force = true)
            }
        }

        HomeScreen(
            state = state,
            onModeChange = vm::switchMode,
            onSearchClick = {},
            onFilterClick = {},
            onAddMemberClick = {},
            onDeleteMemberClick = {}
        )
    }
}
