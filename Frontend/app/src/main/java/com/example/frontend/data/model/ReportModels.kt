package com.example.frontend.data.model

import com.google.gson.annotations.SerializedName

data class ReportResponse(
    val startDate: String,
    val endDate: String,
    val totalPemasukan: Long,
    val totalPengeluaran: Long,
    val saldoPeriode: Long,
    val transaksi: List<ReportTransaksiDto>
)

data class ReportTransaksiDto(
    val id: Int,
    @SerializedName("tglTransaksi") val tglTransaksi: String?,
    val jenis: String,
    val nominal: Long,
    val keterangan: String?,
    @SerializedName("kategori_id") val kategoriId: Int?,
    @SerializedName("kategori_nama") val kategoriNama: String?,
    @SerializedName("pengguna_id") val penggunaId: Int?,
    @SerializedName("dicatat_oleh") val dicatatOleh: String?
)
