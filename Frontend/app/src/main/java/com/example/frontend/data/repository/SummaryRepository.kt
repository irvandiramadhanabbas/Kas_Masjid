package com.example.frontend.data.repository

import com.example.frontend.data.api.ApiService
import com.example.frontend.data.model.SummaryResponse
import javax.inject.Inject

class SummaryRepository @Inject constructor(
    private val api: ApiService
) {
    suspend fun getSummary(): SummaryResponse =
        api.tampilkanRingkasan()
}
