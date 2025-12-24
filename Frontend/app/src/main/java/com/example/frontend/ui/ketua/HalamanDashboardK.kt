package com.example.frontend.ui.ketua

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.frontend.R
import com.example.frontend.data.model.Pengguna
import com.example.frontend.ui.ketua.component.PenggunaCard
import com.example.frontend.viewmodel.KetuaDashboardViewModel
import com.example.frontend.viewmodel.ProfileViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import com.example.frontend.ui.component.Header
import com.example.frontend.ui.component.RingkasanCard

@Composable
fun HalamanDashboardK(
    vm: KetuaDashboardViewModel,
    onOpenResetPassword: (Pengguna) -> Unit,
    onOpenTambahPengguna: () -> Unit,
    onOpenUpdatePengguna: (Pengguna) -> Unit,
    onLogout: () -> Unit
) {
    LaunchedEffect(Unit) { vm.loadData() }

    var openedUserId by remember { mutableStateOf<Int?>(null) }

    var showTambah by remember { mutableStateOf(false) }
    var showUpdate by remember { mutableStateOf(false) }
    var selectedUser by remember { mutableStateOf<Pengguna?>(null) }

    var showDelete by remember { mutableStateOf(false) }
    var deleteTarget by remember { mutableStateOf<Pengguna?>(null) }

    var showSuccess by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }


    val scope = rememberCoroutineScope()

    val saldo = vm.summary?.totalSaldo ?: 0L
    val masuk = vm.summary?.totalPemasukan ?: 0L
    val keluar = vm.summary?.totalPengeluaran ?: 0L

    val profileVm: ProfileViewModel = hiltViewModel()
    val profileState by profileVm.state.collectAsStateWithLifecycle()

    val avatarLetter = profileState.username
        .trim()
        .firstOrNull()
        ?.uppercaseChar()
        ?.toString() ?: "A"

    val greenBg = Color(0xFF608B62)
    val softCard = Color(0xCCFFFFFF)
    val white = Color.White

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(greenBg)
    ) {
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Header(
                    title = "Dashboard",
                    subtitle = "Kas Masjid",
                    avatarLetter = avatarLetter,
                    greenBg = greenBg
                )
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    RingkasanCard(
                        modifier = Modifier.weight(1f),
                        title = "Pemasukan:",
                        value = rupiahIDNoDecimal(masuk),
                        iconRes = R.drawable.pemasukan,
                        iconBg = Color.White,
                        iconTint = Color(0xFF2E7D32),
                        valueTint = Color(0xFF4DAC2F)
                    )
                    RingkasanCard(
                        modifier = Modifier.weight(1f),
                        title = "Pengeluaran:",
                        value = rupiahIDNoDecimal(keluar),
                        iconRes = R.drawable.pengeluaran,
                        iconBg = Color.White,
                        iconTint = Color(0xFFC62828),
                        valueTint = Color(0xFFE51F1F)
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 17.dp)
                ) {
                    RingkasanCard(
                        modifier = Modifier.weight(1f),
                        title = "Total Saldo:",
                        value = rupiahIDNoDecimal(saldo),
                        iconRes = R.drawable.totalsaldo,
                        iconBg = Color(0xFF82B285),
                        iconTint = Color.White,
                        valueTint = Color.Black
                    )
                    Spacer(Modifier.weight(1f))
                }
            }

            items(vm.pengguna, key = { it.id }) { user ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = softCard),
                    shape = RoundedCornerShape(22.dp)
                ) {
                    PenggunaCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        user = user,

                        isMenuOpen = openedUserId == user.id,
                        onOpenMenu = { openedUserId = user.id },
                        onCloseMenu = { openedUserId = null },

                        onTambah = { onOpenTambahPengguna() },
                        onUpdate = { onOpenUpdatePengguna(it) },
                        onUpdatePassword = { onOpenResetPassword(it) },

                        onDelete = {
                            deleteTarget = it
                            showDelete = true
                        }
                    )
                }
            }
        }

            AnimatedVisibility(
                visible = showDelete && deleteTarget != null,
                enter = fadeIn() + scaleIn(initialScale = 0.9f),
                exit = fadeOut() + scaleOut(targetScale = 0.9f),
                modifier = Modifier.align(Alignment.Center)
            ) {
                Dialog(
                    onDismissRequest = {
                        showDelete = false
                        deleteTarget = null
                    }
                ) {
                    Box(
                        modifier = Modifier
                            .width(300.dp)
                            .height(90.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFFF3B3E)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Text(
                                text = "Apakah Anda yakin ingin\nmenghapus pengguna ini?",
                                color = Color.White,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium
                            )

                            Box(
                                modifier = Modifier
                                    .width(60.dp)
                                    .height(26.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFD9D9D9))
                                    .clickable {
                                        val target = deleteTarget ?: return@clickable

                                        showDelete = false
                                        deleteTarget = null
                                        openedUserId = null

                                        vm.hapusPengguna(target.id) { success, message ->

                                            showDelete = false
                                            deleteTarget = null
                                            openedUserId = null

                                            if (success) {
                                                showSuccess = true
                                                scope.launch {
                                                    delay(1500)
                                                    showSuccess = false
                                                }
                                            } else {
                                                scope.launch {
                                                    snackbarHostState.showSnackbar(
                                                        message = message,
                                                        withDismissAction = true
                                                    )
                                                }
                                            }
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Ya", fontWeight = FontWeight.Bold, color = Color.Black)
                            }
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = showSuccess,
                enter = fadeIn() + scaleIn(initialScale = 0.9f),
                exit = fadeOut() + scaleOut(targetScale = 0.9f),
                modifier = Modifier.align(Alignment.Center)
            ) {
                Box(
                    modifier = Modifier
                        .width(300.dp)
                        .height(60.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFA8EFA5)),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        Text(
                            "Pengguna berhasil dihapus",
                            fontWeight = FontWeight.Medium,
                            color = Color.Black
                        )
                        Text("✓", color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

private fun rupiahIDNoDecimal(v: Long): String {
    val symbols = DecimalFormatSymbols(Locale("in", "ID")).apply {
        groupingSeparator = '.'
    }
    val df = DecimalFormat("#,##0", symbols)
    return "Rp ${df.format(v)}"
}
