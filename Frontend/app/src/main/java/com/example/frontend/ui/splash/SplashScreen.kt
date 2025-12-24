package com.example.frontend.ui.splash

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.frontend.navigation.Routes
import com.example.frontend.viewmodel.SplashViewModel

@Composable
fun SplashScreen(
    navController: NavHostController,
    vm: SplashViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        val start = vm.getStartRoute()
        navController.navigate(start) {
            popUpTo(Routes.SPLASH) { inclusive = true }
            launchSingleTop = true
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}
