package com.example.frontend.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontend.data.api.toApiMessage
import com.example.frontend.data.model.Pengguna
import com.example.frontend.data.model.ResetPasswordRequest
import com.example.frontend.data.model.TambahPenggunaRequest
import com.example.frontend.data.model.UpdatePenggunaRequest
import com.example.frontend.data.model.SummaryResponse
import com.example.frontend.data.repository.PenggunaRepository
import com.example.frontend.data.repository.SummaryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class KetuaDashboardViewModel @Inject constructor(
    private val penggunaRepo: PenggunaRepository,
    private val summaryRepo: SummaryRepository
) : ViewModel() {

    var pengguna by mutableStateOf<List<Pengguna>>(emptyList())
        private set

    var summary by mutableStateOf<SummaryResponse?>(null)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    fun loadData() {
        viewModelScope.launch {
            try {
                error = null
                summary = summaryRepo.getSummary()
                pengguna = penggunaRepo.tampilkanPengguna()
            } catch (e: Exception) {
                error = e.message
            }
        }
    }

    fun hapusPengguna(
        id: Int,
        onResult: (success: Boolean, message: String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                penggunaRepo.hapusPengguna(id)
                loadData()
                onResult(true, "Pengguna berhasil dihapus")
            } catch (t: Throwable) {
                onResult(false, t.toApiMessage("Gagal menghapus pengguna"))
            }
        }
    }

    fun tambahPengguna(
        username: String,
        email: String,
        password: String,
        role: String,
        onDone: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val body = TambahPenggunaRequest(
                    username = username,
                    email = email,
                    password = password,
                    role = role
                )
                penggunaRepo.tambahPengguna(body)
                loadData()
                onDone("Pengguna berhasil ditambahkan")
            } catch (e: retrofit2.HttpException) {
                onDone("HTTP ${e.code()}: ${e.response()?.errorBody()?.string()}")
            } catch (e: Exception) {
                onDone("Gagal: ${e.message}")
            }
        }
    }

    fun updatePengguna(
        id: Int,
        username: String?,
        email: String?,
        role: String?,
        status: String?,
        onDone: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val body = UpdatePenggunaRequest(
                    username = username,
                    email = email,
                    role = role,
                    status = status
                )
                penggunaRepo.updatePengguna(id, body)
                loadData()
                onDone("Pengguna berhasil diupdate")
            } catch (e: Exception) {
                onDone(e.message ?: "Gagal update pengguna")
            }
        }
    }

    fun resetPassword(
        id: Int,
        newPassword: String,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val body = ResetPasswordRequest(newPassword = newPassword)

                penggunaRepo.resetPasswordPengguna(id, body)

                onResult(true)
            } catch (e: Exception) {
                onResult(false)
            }
        }
    }
}
