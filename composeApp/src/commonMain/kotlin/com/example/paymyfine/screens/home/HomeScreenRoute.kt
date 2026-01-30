package com.example.paymyfine.screens.home

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.screen.Screen
import com.example.paymyfine.data.infringements.InfringementRepository
import com.example.paymyfine.data.infringements.InfringementService
import com.example.paymyfine.data.network.BaseUrlProvider
import com.example.paymyfine.data.network.HttpClientFactory
import com.example.paymyfine.data.session.SessionStore
import com.russhwolf.settings.Settings

class HomeScreenRoute : Screen {



    @Composable
    override fun Content() {



        val settings = remember { Settings() }
        val sessionStore = remember { SessionStore(settings) }

        val service = remember {
            InfringementService(
                HttpClientFactory.create(),
                BaseUrlProvider.get(),
                sessionStore
            )
        }

        val repo = remember { InfringementRepository(service) }
        val vm = remember { HomeViewModel(settings, repo) }

        val state by vm.uiState
        val idNumber = sessionStore.getIdNumber()



        LaunchedEffect(state.mode, idNumber) {
            idNumber?.let { safeId ->
                if (
                    state.mode == HomeMode.INDIVIDUAL &&
                    !state.isLoading &&
                    state.fines.isEmpty()
                ) {
                    println("HOME → Loading fines for idNumber=$safeId")
                    vm.loadIndividual(
                        idNumber = safeId,
                        force = true
                    )
                }
            }
        }



        /*println("HOME → Loading fines for idNumber=$idNumber")*/




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
