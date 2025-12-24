package com.example.frontend.data.repository

import com.example.frontend.data.api.ApiService
import com.example.frontend.data.model.KategoriRequest
import com.example.frontend.data.model.KategoriResponse
import javax.inject.Inject

class KategoriRepository @Inject constructor(
    private val api: ApiService
) {
    suspend fun getAll(): List<KategoriResponse> =
        api.tampilkanKategori()

    suspend fun tambah(nama: String): KategoriResponse =
        api.tambahKategori(KategoriRequest(nama))

    suspend fun update(id: Int, nama: String): KategoriResponse =
        api.updateKategori(id, KategoriRequest(nama))

    suspend fun hapus(id: Int): String =
        api.hapusKategori(id).message
}

