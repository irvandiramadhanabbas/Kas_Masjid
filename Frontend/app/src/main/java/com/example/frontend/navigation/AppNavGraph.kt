package com.example.frontend.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.frontend.ui.HalamanLogin
import com.example.frontend.ui.bendahara.BendaharaHomeScreen
import com.example.frontend.ui.jamaah.JamaahHomeScreen
import com.example.frontend.ui.ketua.KetuaHomeScreen
import com.example.frontend.ui.splash.SplashScreen
import com.example.frontend.viewmodel.AuthViewModel

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH
    ) {

        composable(Routes.SPLASH) {
            SplashScreen(navController)
        }

        composable(Routes.LOGIN) {
            val authViewModel: AuthViewModel = hiltViewModel()
            HalamanLogin(
                viewModel = authViewModel,
                onLoginSuccess = { user ->
                    navController.navigate(roleToHomeRoute(user.role)) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Routes.HOME_BENDAHARA) {
            BendaharaHomeScreen(
                onLogoutToLogin = {
                    navController.navigate(Routes.LOGIN) { popUpTo(0) }
                },
                onOpenKategori = {},
                onOpenLaporan = {}
            )
        }

        composable(Routes.HOME_KETUA) {
            KetuaHomeScreen(
                onLogoutToLogin = {
                    navController.navigate(Routes.LOGIN) { popUpTo(0) }
                }
            )
        }

        composable(Routes.HOME_JAMAAH) {
            JamaahHomeScreen(
                onLogoutToLogin = {
                    navController.navigate(Routes.LOGIN) { popUpTo(0) }
                }
            )
        }
    }
}

