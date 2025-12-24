package com.example.frontend.data.repository

import com.example.frontend.data.api.ApiService
import com.example.frontend.data.model.ReportResponse
import okhttp3.ResponseBody
import retrofit2.Response
import javax.inject.Inject

class ReportRepository @Inject constructor(
    private val api: ApiService
) {
    suspend fun getLaporan(startDate: String, endDate: String): ReportResponse {
        return api.tampilkanLaporan(startDate, endDate)
    }

    suspend fun eksporPdf(startDate: String, endDate: String): Response<ResponseBody> {
        return api.eksporPdf(startDate, endDate)
    }
}
