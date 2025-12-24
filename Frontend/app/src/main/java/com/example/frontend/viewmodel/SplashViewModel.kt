package com.example.frontend.viewmodel

import androidx.lifecycle.ViewModel
import com.example.frontend.data.datastore.SessionDataStore
import com.example.frontend.navigation.Routes
import com.example.frontend.navigation.roleToHomeRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.first

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val session: SessionDataStore
) : ViewModel() {

    suspend fun getStartRoute(): String {
        val token = session.tokenFlow.first()
        val role = session.roleFlow.first()

        return if (!token.isNullOrBlank() && !role.isNullOrBlank()) {
            roleToHomeRoute(role)
        } else {
            Routes.LOGIN
        }
    }
}
