package com.example.frontend.data.model

import com.google.gson.annotations.SerializedName

data class TransaksiDto(
    val id: Int,

    @SerializedName("tglTransaksi")
    val tglTransaksi: String?,

    val jenis: String,
    val nominal: Long,
    val keterangan: String?,

    @SerializedName("tglPencatatan")
    val tglPencatatan: String?,

    @SerializedName("kategori_id")
    val kategoriIdDb: Int?,

    @SerializedName("kategori_nama")
    val kategoriNama: String?,

    @SerializedName("pengguna_id")
    val penggunaId: Int?,

    @SerializedName("dicatat_oleh")
    val dicatatOleh: String?
)

data class TransaksiCreateRequest(
    val tglTransaksi: String,
    val jenis: String,
    val kategoriId: Int,
    val nominal: Long,
    val keterangan: String? = null
)

data class TransaksiUpdateRequest(
    val tglTransaksi: String,
    val jenis: String,
    val kategoriId: Int,
    val nominal: Long,
    val keterangan: String? = null
)
