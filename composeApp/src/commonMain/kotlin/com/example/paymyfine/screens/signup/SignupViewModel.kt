package com.example.paymyfine.screens.signup

import androidx.compose.runtime.*
import com.example.paymyfine.data.auth.AuthRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

data class SignupUiState(
    val fullName: String = "",
    val email: String = "",
    val idNumber: String = "",
    val password: String = "",
    val repeatPassword: String = "",
    val isLoading: Boolean = false,
    val message: String? = null,
    val messageIsError: Boolean = false
)


class SignupViewModel(
    private val repo: AuthRepository
) {

    var state by mutableStateOf(SignupUiState())
        private set

    // ✅ REQUIRED by UI
    fun onFullNameChange(v: String) {
        state = state.copy(fullName = v, message = null)
    }

    fun onEmailChange(v: String) {
        state = state.copy(email = v, message = null)
    }

    fun onIdNumberChange(v: String) {
        state = state.copy(idNumber = v, message = null)
    }

    fun onPasswordChange(v: String) {
        state = state.copy(password = v, message = null)
    }

    fun onRepeatPasswordChange(v: String) {
        state = state.copy(repeatPassword = v, message = null)
    }

    fun signup(scope: CoroutineScope, onSuccess: () -> Unit) {
        state = state.copy(isLoading = true)

        scope.launch {
            val s = state

            if (s.fullName.isBlank() ||
                s.email.isBlank() ||
                s.idNumber.length != 13 ||
                s.password.length < 8 ||
                s.password != s.repeatPassword
            ) {
                state = s.copy(
                    isLoading = false,
                    message = "Please check your details",
                    messageIsError = true
                )
                return@launch
            }

            val result = repo.signup(
                s.fullName,
                s.email,
                s.idNumber,
                s.password
            )

            state = if (result.isSuccess) {
                onSuccess()
                s.copy(isLoading = false)
            } else {
                s.copy(
                    isLoading = false,
                    message = result.exceptionOrNull()?.message,
                    messageIsError = true
                )
            }
        }
    }
}
