package com.example.paymyfine.screens.login

import androidx.compose.runtime.*
import com.example.paymyfine.data.auth.AuthRepository
import kotlinx.coroutines.*

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val showReactivate: Boolean = false
)

class LoginViewModel(
    private val repo: AuthRepository
) {

    var state by mutableStateOf(LoginUiState())
        private set

    private val scope =
        CoroutineScope(SupervisorJob() + Dispatchers.Main)

    fun onEmailChange(v: String) {
        state = state.copy(
            email = v,
            errorMessage = null,
            showReactivate = false
        )
    }

    fun onPasswordChange(v: String) {
        state = state.copy(
            password = v,
            errorMessage = null
        )
    }

    fun login(onSuccess: () -> Unit) {

        state = state.copy(isLoading = true)

        scope.launch {

            val result =
                repo.login(state.email, state.password)

            if (result.isSuccess) {
                state = state.copy(isLoading = false)
                onSuccess()
            } else {

                val msg =
                    result.exceptionOrNull()?.message
                        ?: "Login failed"

                // ⭐ KEY FIX HERE
                val isDeactivated =
                    msg.contains("deactivated", true) ||
                            msg.contains("inactive", true) ||
                            msg.contains("disabled", true)

                state = state.copy(
                    isLoading = false,
                    errorMessage = msg,
                    showReactivate = isDeactivated
                )
            }
        }
    }

    fun reactivate() {
        scope.launch {

            val result =
                repo.reactivate(state.email)

            if (result.isSuccess) {
                state = state.copy(
                    errorMessage =
                        "Account reactivated. Please login again.",
                    showReactivate = false
                )
            } else {
                state = state.copy(
                    errorMessage =
                        result.exceptionOrNull()?.message
                )
            }
        }
    }
}
