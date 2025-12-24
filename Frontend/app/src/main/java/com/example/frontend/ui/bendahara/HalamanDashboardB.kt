package com.example.frontend.ui.bendahara

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.frontend.R
import com.example.frontend.ui.component.Header
import com.example.frontend.ui.component.RingkasanCard
import com.example.frontend.viewmodel.BendaharaDashboardViewModel
import com.example.frontend.viewmodel.ProfileViewModel
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

@Composable
fun HalamanDashboardB(
    vm: BendaharaDashboardViewModel,
    onGoTransaksi: () -> Unit,
    onGoKategori: () -> Unit,
    onGoLaporan: () -> Unit,
    onLogout: () -> Unit
) {
    val state by vm.state.collectAsStateWithLifecycle()

    val profileVm: ProfileViewModel = hiltViewModel()
    val profileState by profileVm.state.collectAsStateWithLifecycle()

    val avatarLetter = profileState.username
        .trim()
        .firstOrNull()
        ?.uppercaseChar()
        ?.toString() ?: "A"

    LaunchedEffect(Unit) { vm.load() }

    val greenBg = Color(0xFF608B62)

    val saldo = state.summary?.totalSaldo ?: 0L
    val masuk = state.summary?.totalPemasukan ?: 0L
    val keluar = state.summary?.totalPengeluaran ?: 0L

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(greenBg)
    ) {

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

            if (state.isLoading) {
                item {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    )
                }
            }

            state.error?.let { err ->
                item {
                    Text(
                        text = err,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
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
                        .padding(horizontal = 17.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
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
                    RingkasanCard(
                        modifier = Modifier.weight(1f),
                        title = "Laporan :",
                        value = "Tekan di sini",
                        iconRes = R.drawable.laporan,
                        iconBg = Color(0xFF6EABD7),
                        iconTint = Color.White,
                        valueTint = Color(0xFF000000),
                        onClick = onGoLaporan // ✅ hanya ini yang klik
                    )

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
