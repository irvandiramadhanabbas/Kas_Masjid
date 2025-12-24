package com.example.frontend.data.model

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginUser(
    val id: Int,
    val username: String,
    val email: String,
    val role: String,
    val status: String
)

data class LoginResponse(
    val message: String,
    val token: String,
    val user: LoginUser
)
