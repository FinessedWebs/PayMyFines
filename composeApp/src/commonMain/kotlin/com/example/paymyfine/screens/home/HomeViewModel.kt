package com.example.paymyfine.screens.home

import androidx.compose.runtime.*
import com.example.paymyfine.data.infringements.InfringementRepository
import com.russhwolf.settings.Settings
import kotlinx.coroutines.*

class HomeViewModel(
    private val settings: Settings,
    private val repo: InfringementRepository
) {

    companion object {
        private const val KEY_HOME_MODE = "home_mode"
    }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val _uiState = mutableStateOf(
        HomeState(mode = loadSavedMode())
    )
    val uiState: State<HomeState> = _uiState

    private fun loadSavedMode(): HomeMode =
        HomeMode.valueOf(
            settings.getString(KEY_HOME_MODE, HomeMode.INDIVIDUAL.name)
        )

    /** ✅ REQUIRED by HomeScreen */
    fun switchMode(mode: HomeMode) {
        settings.putString(KEY_HOME_MODE, mode.name)
        _uiState.value = _uiState.value.copy(mode = mode)
        // ❌ DO NOT load fines here
    }

    fun loadIndividual(idNumber: String, force: Boolean) {
        scope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            repo.loadIndividual(idNumber, force)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        fines = it,
                        isLoading = false,
                        errorMessage = null
                    )
                }
                .onFailure {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = it.message
                    )
                }
        }
    }

    fun clear() {
        scope.cancel()
    }
}
