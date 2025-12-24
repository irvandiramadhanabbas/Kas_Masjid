package com.example.frontend.viewmodel

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontend.data.model.ReportResponse
import com.example.frontend.data.repository.ReportRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException

data class LaporanUiState(
    val isLoading: Boolean = false,
    val report: ReportResponse? = null,
    val error: String? = null,

    val isExporting: Boolean = false,
    val exportError: String? = null,
    val exportedUri: String? = null,

    val lastStartDate: String? = null,
    val lastEndDate: String? = null
)

@HiltViewModel
class LaporanViewModel @Inject constructor(
    private val repo: ReportRepository
) : ViewModel() {

    private val _state = MutableStateFlow(LaporanUiState())
    val state: StateFlow<LaporanUiState> = _state

    fun load(startDate: String, endDate: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                isLoading = true,
                error = null,
                report = null,
                exportedUri = null,
                lastStartDate = startDate,
                lastEndDate = endDate
            )

            try {
                val result = repo.getLaporan(startDate, endDate)
                _state.value = _state.value.copy(isLoading = false, report = result)
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Gagal memuat laporan"
                )
            }
        }
    }

    fun clearExportError() {
        _state.value = _state.value.copy(exportError = null)
    }

    private fun parseMessageFromErrorBody(raw: String?, fallback: String): String {
        if (raw.isNullOrBlank()) return fallback
        return try {
            val msg = JSONObject(raw).optString("message")
            if (msg.isNotBlank()) msg else fallback
        } catch (_: Exception) {
            fallback
        }
    }

    fun exportPdf(context: Context) {
        val startDate = _state.value.lastStartDate
        val endDate = _state.value.lastEndDate
        val report = _state.value.report

        android.util.Log.d("LAPORAN", "exportPdf clicked start=$startDate end=$endDate report=${report?.transaksi?.size}")

        if (startDate == null || endDate == null) {
            _state.value = _state.value.copy(exportError = "Periode belum dipilih.")
            return
        }

        if (report == null || report.transaksi.isEmpty()) {
            _state.value = _state.value.copy(
                exportError = "Tidak ada data untuk dilaporkan pada periode ini."
            )
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(
                isExporting = true,
                exportError = null,
                exportedUri = null
            )

            try {
                val resp = repo.eksporPdf(startDate, endDate)

                if (!resp.isSuccessful) {
                    val raw = resp.errorBody()?.string()
                    val msg = parseMessageFromErrorBody(raw, "HTTP ${resp.code()}")
                    _state.value = _state.value.copy(isExporting = false, exportError = msg)
                    return@launch
                }

                val body = resp.body()
                if (body == null) {
                    _state.value = _state.value.copy(isExporting = false, exportError = "PDF kosong")
                    return@launch
                }

                val fileName = "laporan_kas_${startDate}_sd_${endDate}.pdf"

                val bytes = body.use { it.bytes() }

                val uri = savePdfToDownloads(context, fileName, bytes)

                _state.value = _state.value.copy(
                    isExporting = false,
                    exportedUri = uri.toString()
                )

            } catch (e: HttpException) {
                val raw = e.response()?.errorBody()?.string()
                val msg = parseMessageFromErrorBody(raw, "HTTP ${e.code()}")
                _state.value = _state.value.copy(isExporting = false, exportError = msg)
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isExporting = false,
                    exportError = e.message ?: "Gagal ekspor PDF"
                )
            }
        }
    }

    @Throws(IOException::class)
    private fun savePdfToDownloads(context: Context, fileName: String, bytes: ByteArray): Uri {
        val resolver = context.contentResolver

        val values = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, fileName)
            put(MediaStore.Downloads.MIME_TYPE, "application/pdf")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Downloads.IS_PENDING, 1)
            }
        }

        val collection = MediaStore.Downloads.EXTERNAL_CONTENT_URI
        val itemUri = resolver.insert(collection, values)
            ?: throw IOException("Gagal membuat file di Downloads")

        resolver.openOutputStream(itemUri)?.use { out ->
            out.write(bytes)
            out.flush()
        } ?: throw IOException("Gagal menulis file PDF")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.clear()
            values.put(MediaStore.Downloads.IS_PENDING, 0)
            resolver.update(itemUri, values, null, null)
        }

        return itemUri
    }
}
