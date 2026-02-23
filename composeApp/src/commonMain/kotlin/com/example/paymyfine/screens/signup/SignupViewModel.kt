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

    // 🔴 Field Errors
    val fullNameError: Boolean = false,
    val emailError: Boolean = false,
    val idNumberError: Boolean = false,
    val passwordError: Boolean = false,
    val repeatPasswordError: Boolean = false,

    val isLoading: Boolean = false,
    val message: String? = null,
    val messageIsError: Boolean = false
)

class SignupViewModel(
    private val repo: AuthRepository
) {

    var state by mutableStateOf(SignupUiState())
        private set

    // ----------------------------
    // FIELD UPDATES (Clear Errors)
    // ----------------------------

    fun onFullNameChange(v: String) {
        state = state.copy(
            fullName = v,
            fullNameError = false,
            message = null
        )
    }

    fun onEmailChange(v: String) {
        state = state.copy(
            email = v,
            emailError = false,
            message = null
        )
    }

    fun onIdNumberChange(v: String) {
        state = state.copy(
            idNumber = v,
            idNumberError = false,
            message = null
        )
    }

    fun onPasswordChange(v: String) {
        state = state.copy(
            password = v,
            passwordError = false,
            message = null
        )
    }

    fun onRepeatPasswordChange(v: String) {
        state = state.copy(
            repeatPassword = v,
            repeatPasswordError = false,
            message = null
        )
    }

    // ----------------------------
    // VALIDATION
    // ----------------------------

    private fun validate(): Boolean {

        val fullNameError = state.fullName.isBlank()

        val emailError =
            state.email.isBlank() || !state.email.contains("@")

        val idNumberError =
            state.idNumber.length != 13

        val passwordError =
            state.password.length < 8

        val repeatPasswordError =
            state.password != state.repeatPassword

        state = state.copy(
            fullNameError = fullNameError,
            emailError = emailError,
            idNumberError = idNumberError,
            passwordError = passwordError,
            repeatPasswordError = repeatPasswordError
        )

        return !(fullNameError ||
                emailError ||
                idNumberError ||
                passwordError ||
                repeatPasswordError)
    }

    // ----------------------------
    // SIGNUP
    // ----------------------------

    fun signup(scope: CoroutineScope, onSuccess: () -> Unit) {

        if (!validate()) {
            state = state.copy(
                message = "Please correct the highlighted fields",
                messageIsError = true
            )
            return
        }

        state = state.copy(
            isLoading = true,
            message = null
        )

        scope.launch {

            val result = repo.signup(
                state.fullName,
                state.email,
                state.idNumber,
                state.password
            )

            state = if (result.isSuccess) {
                onSuccess()
                state.copy(
                    isLoading = false,
                    message = null
                )
            } else {
                state.copy(
                    isLoading = false,
                    message = result.exceptionOrNull()?.message
                        ?: "Signup failed",
                    messageIsError = true
                )
            }
        }
    }
}