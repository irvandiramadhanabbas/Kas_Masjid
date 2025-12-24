package com.example.frontend.data.repository

import com.example.frontend.data.api.ApiService
import com.example.frontend.data.datastore.SessionDataStore
import com.example.frontend.data.model.LoginRequest
import com.example.frontend.data.model.LoginResponse
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val api: ApiService,
    private val session: SessionDataStore
) {
    suspend fun login(email: String, password: String): LoginResponse {
        val res = api.login(LoginRequest(email, password))
        session.saveSession(
            token = res.token,
            role = res.user.role,
            email = res.user.email,
            username = res.user.username,
            userId = res.user.id,
            status = res.user.status
        )
        return res
    }

    suspend fun logout() = session.clearSession()
}
