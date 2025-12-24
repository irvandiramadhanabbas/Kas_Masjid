package com.example.frontend.ui.ketua

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.*
import com.example.frontend.data.model.Pengguna
import com.example.frontend.navigation.Routes
import com.example.frontend.ui.HalamanTransaksi
import com.example.frontend.ui.component.AppBottomBar
import com.example.frontend.ui.ketua.sheet.ResetPasswordPengguna
import com.example.frontend.ui.ketua.sheet.TambahPengguna
import com.example.frontend.ui.ketua.sheet.UpdatePengguna
import com.example.frontend.ui.theme.SetSystemBars
import com.example.frontend.viewmodel.KetuaDashboardViewModel
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

@Composable
fun KetuaHomeScreen(
    onLogoutToLogin: () -> Unit
) {
    val greenBg = Color(0xFF608B62)
    SetSystemBars(darkIcons = false)

    val tabNavController = rememberNavController()
    val navBackStackEntry by tabNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    var showResetSheet by remember { mutableStateOf(false) }
    var showTambahSheet by remember { mutableStateOf(false) }
    var showUpdateSheet by remember { mutableStateOf(false) }
    var updateTargetUser by remember { mutableStateOf<Pengguna?>(null) }
    var resetTargetUser by remember { mutableStateOf<Pengguna?>(null) }

    val vm: KetuaDashboardViewModel = hiltViewModel()

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
                    HalamanDashboardK(
                        vm = vm,
                        onLogout = onLogoutToLogin,

                        onOpenResetPassword = { user ->
                            resetTargetUser = user
                            showResetSheet = true
                        },

                        onOpenTambahPengguna = {
                            showTambahSheet = true
                        },

                        onOpenUpdatePengguna = { user ->
                            updateTargetUser = user
                            showUpdateSheet = true
                        }
                    )
                }

                composable(Routes.TAB_TRANSAKSI) { HalamanTransaksi() }

                composable(Routes.TAB_PROFIL) {
                    HalamanProfil(
                        onBack = null,
                        onLogoutToLogin = onLogoutToLogin
                    )
                }
            }

            if (showResetSheet) {
                ResetPasswordPengguna(
                    bottomInset = innerPadding.calculateBottomPadding(),
                    onDismiss = {
                        showResetSheet = false
                        resetTargetUser = null
                    },
                    onSubmit = { newPass ->
                        val target = resetTargetUser ?: return@ResetPasswordPengguna false

                        suspendCancellableCoroutine { cont ->
                            vm.resetPassword(target.id, newPass) { ok ->
                                if (cont.isActive) cont.resume(ok)
                            }
                        }
                    }
                )
            }

            if (showTambahSheet) {
                TambahPengguna(
                    bottomInset = innerPadding.calculateBottomPadding(),
                    onDismiss = { showTambahSheet = false },
                    onSubmit = { username, email, password, role ->
                        showTambahSheet = false
                        vm.tambahPengguna(username, email, password, role) {
                        }
                    }
                )
            }

            if (showUpdateSheet && updateTargetUser != null) {
                UpdatePengguna(
                    user = updateTargetUser!!,
                    bottomInset = innerPadding.calculateBottomPadding(),
                    onDismiss = {
                        showUpdateSheet = false
                        updateTargetUser = null
                    },
                    onSubmit = { username, email, role, status ->
                        val target = updateTargetUser ?: return@UpdatePengguna
                        showUpdateSheet = false

                        vm.updatePengguna(target.id, username, email, role, status) { }

                        updateTargetUser = null
                    }
                )
            }
        }
    }
}
