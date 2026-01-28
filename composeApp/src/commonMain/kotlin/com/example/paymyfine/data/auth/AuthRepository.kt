package com.example.paymyfine.data.auth

import com.example.paymyfine.data.session.SessionStore

class AuthRepository(
    private val service: AuthService,
    private val sessionStore: SessionStore
) {

    suspend fun login(email: String, password: String): Result<Unit> {
        return service.login(LoginRequest(email, password))
            .map {
                sessionStore.saveSession(
                    token = it.token,
                    fullName = it.fullName,
                    email = it.email,
                    idNumber = it.idNumber
                )
            }
    }

    suspend fun signup(
        fullName: String,
        email: String,
        idNumber: String,
        password: String
    ): Result<Unit> =
        service.signup(
            SignupRequest(fullName, email, idNumber, password)
        )

    suspend fun reactivate(email: String): Result<Unit> =
        service.reactivate(email)
}
