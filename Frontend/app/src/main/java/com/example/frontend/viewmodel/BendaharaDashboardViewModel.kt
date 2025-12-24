package com.example.frontend.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontend.data.datastore.SessionDataStore
import com.example.frontend.data.model.SummaryResponse
import com.example.frontend.data.repository.SummaryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class BendaharaDashboardUiState(
    val isLoading: Boolean = false,
    val summary: SummaryResponse? = null,
    val username: String? = null,
    val role: String? = null,
    val error: String? = null
)

@HiltViewModel
class BendaharaDashboardViewModel @Inject constructor(
    private val repo: SummaryRepository,
    private val session: SessionDataStore
) : ViewModel() {

    private val _state = MutableStateFlow(BendaharaDashboardUiState())
    val state: StateFlow<BendaharaDashboardUiState> = _state

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)

            try {
                val username = session.getUsernameOnce()
                val role = session.getRoleOnce()

                val summary = repo.getSummary()

                _state.value = _state.value.copy(
                    isLoading = false,
                    summary = summary,
                    username = username,
                    role = role
                )

                if (role != "BENDAHARA") {
                    session.clearSession()
                    throw IllegalStateException("Role tidak valid")
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Gagal memuat dashboard"
                )
            }

        }
    }

    fun logout(onDone: () -> Unit) {
        viewModelScope.launch {
            session.clearSession()
            onDone()
        }
    }
}
