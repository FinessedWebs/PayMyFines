package com.example.paymyfine.screens.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.paymyfine.data.auth.AuthRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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

    var state by mutableStateOf(
        LoginUiState()
    )
        private set

    fun onEmailChange(v: String) {
        state = state.copy(email = v, errorMessage = null)
    }

    fun onPasswordChange(v: String) {
        state = state.copy(password = v, errorMessage = null)
    }

    fun login(scope: CoroutineScope, onSuccess: () -> Unit) {
        state = state.copy(isLoading = true, errorMessage = null)

        scope.launch {
            val result = repo.login(state.email, state.password)

            state = if (result.isSuccess) {
                onSuccess()
                state.copy(isLoading = false)
            } else {
                val msg = result.exceptionOrNull()?.message
                state.copy(
                    isLoading = false,
                    errorMessage = if (msg == "account_deactivated") {
                        state.copy(showReactivate = true)
                        "Account deactivated"
                    } else {
                        msg ?: "Invalid credentials"
                    }
                )
            }
        }
    }

    fun reactivate(scope: CoroutineScope) {
        scope.launch {
            repo.reactivate(state.email)
            state = state.copy(showReactivate = false)
        }
    }
}


