package com.example.frontend.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontend.data.datastore.SessionDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ProfileUiState(
    val username: String = "-",
    val email: String = "-",
    val role: String = "-",
    val status: String = "-"
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val session: SessionDataStore
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileUiState())
    val state: StateFlow<ProfileUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                session.usernameFlow,
                session.emailFlow,
                session.roleFlow,
                session.statusFlow
            ) { username, email, role, status ->
                ProfileUiState(
                    username = username ?: "-",
                    email = email ?: "-",
                    role = role ?: "-",
                    status = status ?: "-"
                )
            }.collect { _state.value = it }
        }
    }

    fun logout() {
        viewModelScope.launch {
            session.clearSession()
        }
    }
}
