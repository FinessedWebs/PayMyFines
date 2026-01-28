package com.example.paymyfine.screens.home

import com.example.paymyfine.data.fines.IForceItem

enum class HomeMode {
    INDIVIDUAL,
    FAMILY
}

data class HomeState(
    val mode: HomeMode = HomeMode.INDIVIDUAL,
    val isLoading: Boolean = false,
    val fines: List<IForceItem> = emptyList(),
    val errorMessage: String? = null,
    val isSearchVisible: Boolean = false,
    val hasActiveFilters: Boolean = false
)
