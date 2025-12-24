package com.example.frontend.data.model

data class Pengguna(
    val id: Int,
    val username: String,
    val email: String,
    val role: String,
    val status: String
)

data class TambahPenggunaRequest(
    val username: String,
    val email: String,
    val password: String,
    val role: String
)

data class UpdatePenggunaRequest(
    val username: String? = null,
    val email: String? = null,
    val role: String? = null,
    val status: String? = null
)

data class ResetPasswordRequest(
    val newPassword: String
)
