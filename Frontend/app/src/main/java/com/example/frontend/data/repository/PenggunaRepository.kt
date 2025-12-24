package com.example.frontend.data.repository

import com.example.frontend.data.api.ApiService
import com.example.frontend.data.model.ResetPasswordRequest
import com.example.frontend.data.model.TambahPenggunaRequest
import com.example.frontend.data.model.UpdatePenggunaRequest
import javax.inject.Inject

class PenggunaRepository @Inject constructor(
    private val api: ApiService
) {
    suspend fun tampilkanPengguna() = api.tampilkanPengguna()
    suspend fun tambahPengguna(body: TambahPenggunaRequest) =
        api.tambahPengguna(body)

    suspend fun updatePengguna(id: Int, body: UpdatePenggunaRequest) =
        api.updatePengguna(id, body)

    suspend fun resetPasswordPengguna(id: Int, body: ResetPasswordRequest ) =
        api.resetPasswordPengguna(id, body)

    suspend fun hapusPengguna(id: Int) =
        api.hapusPengguna(id)
}
