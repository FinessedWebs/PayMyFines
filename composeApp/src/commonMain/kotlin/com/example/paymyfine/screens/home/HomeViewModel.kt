package com.example.paymyfine.screens.home

import androidx.compose.runtime.*
import com.example.paymyfine.data.family.FamilyRepository
import com.example.paymyfine.data.family.models.AddFamilyMemberRequest
import com.example.paymyfine.data.fines.IForceItem
import com.example.paymyfine.data.infringements.InfringementRepository
import com.example.paymyfine.data.network.ApiResult
import com.russhwolf.settings.Settings
import kotlinx.coroutines.*

class HomeViewModel(
    private val settings: Settings,
    private val repo: InfringementRepository,
    private val familyRepo: FamilyRepository
) {

    private val _familyFines =
        mutableStateMapOf<String, List<IForceItem>>()

    val familyFines: Map<String, List<IForceItem>>
        get() = _familyFines


    companion object {
        private const val KEY_HOME_MODE = "home_mode"
    }

    private val scope = MainScope()

    private val _uiState =
        mutableStateOf(HomeState(mode = loadSavedMode()))

    val uiState: State<HomeState> = _uiState

    private fun loadSavedMode(): HomeMode =
        HomeMode.valueOf(
            settings.getString(KEY_HOME_MODE, HomeMode.INDIVIDUAL.name)
        )

    fun switchMode(mode: HomeMode) {
        settings.putString(KEY_HOME_MODE, mode.name)

        _uiState.value = _uiState.value.copy(
            mode = mode,
            errorMessage = null
        )

        if (mode == HomeMode.FAMILY) {
            loadFamily(true)
        }
    }


    // ---------- FINES ----------

    fun loadOpenFines(
        force: Boolean,
        silent: Boolean = false
    ) {
        scope.launch {

            // ⭐ Show spinner if no fines yet
            if (!silent || _uiState.value.fines.isEmpty()) {
                _uiState.value =
                    _uiState.value.copy(isLoading = true)
            }

            when (val result = repo.loadOpenFines(force)) {

                is ApiResult.Success ->
                    _uiState.value = _uiState.value.copy(
                        fines = result.data,
                        isLoading = false,
                        errorMessage = null
                    )

                is ApiResult.ApiError ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )

                is ApiResult.NetworkError ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )

                else ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Something went wrong"
                    )
            }

        }
    }

    fun refreshFines() {
        loadOpenFines(force = true, silent = false)
    }



    // ---------- FAMILY ----------

    fun loadFamily(force: Boolean = false) {
        scope.launch {

            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            when (val res = familyRepo.getMembers(force)) {

                is ApiResult.Success ->
                    _uiState.value = _uiState.value.copy(
                        familyMembers = res.data,
                        isLoading = false
                    )

                else -> handleError("Failed to load family")
            }
        }
    }

    fun addFamily(req: AddFamilyMemberRequest) {
        scope.launch {

            when (familyRepo.addMember(req)) {
                is ApiResult.Success -> {
                    loadFamily()
                    hideAddDialog()
                }

                else -> {}
            }
        }
    }


    fun deleteFamily(linkId: String) {
        scope.launch {

            when (familyRepo.deleteMember(linkId)) {
                is ApiResult.Success -> {
                    familyRepo.clearCache()
                    loadFamily(true)
                }
                else -> handleError("Delete failed")
            }
        }
    }

    fun loadFinesForMember(idNumber: String) {

        if (_familyFines.containsKey(idNumber)) return

        scope.launch {

            when (val res =
                repo.loadFinesForMember(idNumber)
            ) {

                is ApiResult.Success -> {
                    _familyFines[idNumber] = res.data
                }

                else -> {}
            }
        }
    }


    // ---------- HELPERS ----------

    private fun handleError(msg: String) {
        _uiState.value = _uiState.value.copy(
            isLoading = false,
            errorMessage = msg
        )
    }

    fun showAddDialog() {
        _uiState.value = _uiState.value.copy(showAddDialog = true)
    }

    fun hideAddDialog() {
        _uiState.value = _uiState.value.copy(showAddDialog = false)
    }

    fun selectFine(fine: IForceItem) {
        _uiState.value = _uiState.value.copy(
            selectedFine = fine
        )
    }



    fun clear() {
        scope.cancel()
    }
}
