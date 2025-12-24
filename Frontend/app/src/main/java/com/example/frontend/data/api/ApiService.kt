package com.example.frontend.data.api

import com.example.frontend.data.model.TambahPenggunaRequest
import com.example.frontend.data.model.Pengguna
import com.example.frontend.data.model.ResetPasswordRequest
import com.example.frontend.data.model.UpdatePenggunaRequest
import com.example.frontend.data.model.LoginRequest
import com.example.frontend.data.model.LoginResponse
import com.example.frontend.data.model.KategoriRequest
import com.example.frontend.data.model.KategoriResponse
import com.example.frontend.data.model.SummaryResponse
import com.example.frontend.data.model.TransaksiCreateRequest
import com.example.frontend.data.model.TransaksiDto
import com.example.frontend.data.model.TransaksiUpdateRequest
import com.example.frontend.data.model.ReportResponse
import retrofit2.http.*

data class MessageResponse(
    val message: String
)

interface ApiService {

    // ===== AUTH =====
    @POST("auth/login")
    suspend fun login(@Body body: LoginRequest): LoginResponse

    // ===== DASHBOARD =====
    @GET("summary")
    suspend fun tampilkanRingkasan(): SummaryResponse

    // ===== KATEGORI =====
    @GET("categories")
    suspend fun tampilkanKategori(): List<KategoriResponse>

    @POST("categories")
    suspend fun tambahKategori(@Body body: KategoriRequest): KategoriResponse

    @PUT("categories/{id}")
    suspend fun updateKategori(
        @Path("id") id: Int,
        @Body body: KategoriRequest
    ): KategoriResponse

    @DELETE("categories/{id}")
    suspend fun hapusKategori(@Path("id") id: Int): MessageResponse

    // ===== TRANSAKSI =====
    @GET("transactions")
    suspend fun tampilkanTransaksi(): List<TransaksiDto>

    @POST("transactions")
    suspend fun tambahTransaksi(@Body body: TransaksiCreateRequest): TransaksiDto

    @PUT("transactions/{id}")
    suspend fun updateTransaksi(
        @Path("id") id: Int,
        @Body body: TransaksiUpdateRequest
    ): TransaksiDto

    // ===== REPORT =====
    @GET("reports")
    suspend fun tampilkanLaporan(
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String
    ): ReportResponse

    // PDF stream
    @GET("reports/pdf")
    @Streaming
    suspend fun eksporPdf(
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String
    ): retrofit2.Response<okhttp3.ResponseBody>

    // ===== USERS (khusus ketua) =====
    @GET("users")
    suspend fun tampilkanPengguna(): List<Pengguna>

    @POST("users")
    suspend fun tambahPengguna(@Body body: TambahPenggunaRequest): Pengguna

    @PUT("users/{id}")
    suspend fun updatePengguna(
        @Path("id") id: Int,
        @Body body: UpdatePenggunaRequest
    ): Pengguna

    @HTTP(method = "DELETE", path = "users/{id}", hasBody = false)
    suspend fun hapusPengguna(@Path("id") id: Int): MessageResponse

    @PATCH("users/{id}/reset-password")
    suspend fun resetPasswordPengguna(
        @Path("id") id: Int,
        @Body body: ResetPasswordRequest
    ): MessageResponse

}
