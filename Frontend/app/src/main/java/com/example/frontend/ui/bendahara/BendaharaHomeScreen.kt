@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.frontend.ui.bendahara

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.*
import com.example.frontend.navigation.Routes
import com.example.frontend.ui.HalamanLaporan
import com.example.frontend.ui.component.AppBottomBar
import com.example.frontend.ui.ketua.HalamanProfil
import com.example.frontend.ui.theme.SetSystemBars
import com.example.frontend.viewmodel.BendaharaDashboardViewModel

@Composable
fun BendaharaHomeScreen(
    onLogoutToLogin: () -> Unit,
    onOpenKategori: () -> Unit,
    onOpenLaporan: () -> Unit
) {
    val greenBg = Color(0xFF608B62)

    SetSystemBars(darkIcons = false)

    val tabNavController = rememberNavController()
    val navBackStackEntry by tabNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val vm: BendaharaDashboardViewModel = hiltViewModel()

    Scaffold(
        containerColor = greenBg,
        bottomBar = {
            AppBottomBar(
                currentRoute = currentRoute,
                onNavigate = { route ->
                    tabNavController.navigate(route) {
                        popUpTo(Routes.TAB_DASHBOARD) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(greenBg)
                .padding(innerPadding)
        ) {
            NavHost(
                navController = tabNavController,
                startDestination = Routes.TAB_DASHBOARD
            ) {
                composable(Routes.TAB_DASHBOARD) {
                    HalamanDashboardB(
                        vm = vm,
                        onGoTransaksi = { tabNavController.navigate(Routes.TAB_TRANSAKSI) },
                        onGoKategori = onOpenKategori,
                        onGoLaporan = { tabNavController.navigate(Routes.TAB_LAPORAN) }, // ✅ ganti ini
                        onLogout = onLogoutToLogin
                    )
                }

                composable(Routes.TAB_TRANSAKSI) { HalamanTransaksiB() }

                composable(Routes.TAB_LAPORAN) { HalamanLaporan() }

                composable(Routes.TAB_PROFIL) {
                    HalamanProfil(onBack = null, onLogoutToLogin = onLogoutToLogin)
                }
            }
        }
    }
}
