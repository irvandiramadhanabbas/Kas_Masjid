package com.example.frontend.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontend.data.api.toApiMessage
import com.example.frontend.data.datastore.SessionDataStore
import com.example.frontend.data.model.KategoriResponse
import com.example.frontend.data.repository.KategoriRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class KategoriUiState(
    val isLoading: Boolean = false,
    val items: List<KategoriResponse> = emptyList(),
    val error: String? = null,
    val success: String? = null,
)

@HiltViewModel
class KategoriViewModel @Inject constructor(
    private val repo: KategoriRepository,
    private val session: SessionDataStore
) : ViewModel() {

    private val _state = MutableStateFlow(KategoriUiState())
    val state: StateFlow<KategoriUiState> = _state

    init { load() }

    fun clearMessage() {
        _state.value = _state.value.copy(error = null, success = null)
    }

    private suspend fun ensureCanRead() {
        val role = session.getRoleOnce()?.uppercase() ?: return
        if (role !in setOf("BENDAHARA", "KETUA", "JAMAAH")) {
            throw IllegalStateException("Akses ditolak (role: $role)")
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
                val data = repo.getAll()
                _state.value = _state.value.copy(isLoading = false, items = data)
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.toApiMessage("Gagal memuat kategori"),
                )
            }
        }
    }

    fun tambah(nama: String, onDone: (() -> Unit)? = null) {
        viewModelScope.launch {
            _state.value = _state.value.copy(error = null, success = null)
            try {
                ensureCanWrite()
                repo.tambah(nama)
                load()
                onDone?.invoke()
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = e.toApiMessage("Gagal tambah kategori")
                )
            }
        }
    }

    fun update(id: Int, nama: String, onDone: (() -> Unit)? = null) {
        viewModelScope.launch {
            _state.value = _state.value.copy(error = null, success = null)
            try {
                ensureCanWrite()
                repo.update(id, nama)
                load()
                onDone?.invoke()
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = e.toApiMessage("Gagal update kategori")
                )
            }
        }
    }

    fun hapus(id: Int, onDone: (() -> Unit)? = null) {
        viewModelScope.launch {
            _state.value = _state.value.copy(error = null, success = null)
            try {
                ensureCanWrite()
                val msg = repo.hapus(id)
                load()
                _state.value = _state.value.copy(success = msg ?: "Kategori berhasil dihapus")
                onDone?.invoke()
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = e.toApiMessage("Gagal hapus kategori")
                )
            }
        }
    }
}
