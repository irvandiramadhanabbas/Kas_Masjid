package com.example.frontend.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontend.data.datastore.SessionDataStore
import com.example.frontend.data.model.TransaksiCreateRequest
import com.example.frontend.data.model.TransaksiDto
import com.example.frontend.data.model.TransaksiUpdateRequest
import com.example.frontend.data.repository.TransaksiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import org.json.JSONObject

data class TransaksiFilter(
    val from: String? = null,      // yyyy-MM-dd
    val to: String? = null,        // yyyy-MM-dd
    val jenis: String? = null,     // PEMASUKAN / PENGELUARAN
    val kategoriId: Int? = null
)

data class TransaksiUiState(
    val isLoading: Boolean = false,
    val items: List<TransaksiDto> = emptyList(),
    val error: String? = null,

    val isSubmitting: Boolean = false,
    val submitError: String? = null,
    val submitSuccess: Boolean = false,

    val filter: TransaksiFilter = TransaksiFilter()
)

@HiltViewModel
class TransaksiViewModel @Inject constructor(
    private val repo: TransaksiRepository,
    private val session: SessionDataStore
) : ViewModel() {

    private val _state = MutableStateFlow(TransaksiUiState())
    val state: StateFlow<TransaksiUiState> = _state

    init { load() }

    private suspend fun ensureCanRead() {
        val role = session.getRoleOnce()?.uppercase()
        if (role !in setOf("BENDAHARA", "KETUA", "JAMAAH")) {
            throw IllegalStateException("Akses ditolak")
        }
    }

    private suspend fun ensureCanWrite() {
        val role = session.getRoleOnce()?.uppercase()
        if (role != "BENDAHARA") {
            throw IllegalStateException("Fitur ini hanya untuk Bendahara")
        }
    }

    fun load() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                ensureCanRead()
                _state.value = _state.value.copy(
                    isLoading = false,
                    items = repo.getAll()
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Gagal memuat transaksi"
                )
            }
        }
    }

    fun applyFilter(filter: TransaksiFilter) {
        _state.value = _state.value.copy(filter = filter)
    }

    fun resetFilter() {
        _state.value = _state.value.copy(filter = TransaksiFilter())
    }

    private fun parseBackendMessage(e: HttpException): String {
        return try {
            val raw = e.response()?.errorBody()?.string().orEmpty()
            val msg = JSONObject(raw).optString("message")
            if (msg.isNotBlank()) msg else "HTTP ${e.code()}"
        } catch (_: Exception) {
            "HTTP ${e.code()}"
        }
    }

    fun resetSubmitState() {
        _state.value = _state.value.copy(
            submitSuccess = false,
            submitError = null
        )
    }
    fun tambah(
        tglTransaksi: String,
        kategoriId: Int,
        jenis: String,
        nominal: Long,
        keterangan: String?
    ) {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                isSubmitting = true,
                submitError = null,
                submitSuccess = false
            )

            try {
                ensureCanWrite() // ✅ hanya bendahara

                repo.create(
                    TransaksiCreateRequest(
                        tglTransaksi = tglTransaksi,
                        jenis = jenis,
                        kategoriId = kategoriId,
                        nominal = nominal,
                        keterangan = keterangan
                    )
                )

                load()
                _state.value = _state.value.copy(
                    isSubmitting = false,
                    submitSuccess = true
                )
            } catch (e: HttpException) {
                _state.value = _state.value.copy(
                    isSubmitting = false,
                    submitError = parseBackendMessage(e)
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isSubmitting = false,
                    submitError = e.message ?: "Gagal tambah transaksi"
                )
            }
        }
    }

    fun update(
        id: Int,
        tglTransaksi: String,
        kategoriId: Int,
        jenis: String,
        nominal: Long,
        keterangan: String?
    ) {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                isSubmitting = true,
                submitError = null,
                submitSuccess = false
            )

            try {
                ensureCanWrite()

                repo.update(
                    id,
                    TransaksiUpdateRequest(
                        tglTransaksi = tglTransaksi,
                        jenis = jenis,
                        kategoriId = kategoriId,
                        nominal = nominal,
                        keterangan = keterangan
                    )
                )

                load()
                _state.value = _state.value.copy(
                    isSubmitting = false,
                    submitSuccess = true
                )
            } catch (e: HttpException) {
                _state.value = _state.value.copy(
                    isSubmitting = false,
                    submitError = parseBackendMessage(e)
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isSubmitting = false,
                    submitError = e.message ?: "Gagal update transaksi"
                )
            }
        }
    }
}
