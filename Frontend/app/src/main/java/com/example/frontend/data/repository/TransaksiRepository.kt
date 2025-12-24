package com.example.frontend.data.repository

import com.example.frontend.data.api.ApiService
import com.example.frontend.data.model.TransaksiCreateRequest
import com.example.frontend.data.model.TransaksiDto
import com.example.frontend.data.model.TransaksiUpdateRequest
import javax.inject.Inject

class TransaksiRepository @Inject constructor(
    private val api: ApiService
) {
    suspend fun getAll(): List<TransaksiDto> = api.tampilkanTransaksi()

    suspend fun create(req: TransaksiCreateRequest): TransaksiDto {
        return api.tambahTransaksi(req)
    }

    suspend fun update(id: Int, req: TransaksiUpdateRequest): TransaksiDto {
        return api.updateTransaksi(id, req)
    }
}
