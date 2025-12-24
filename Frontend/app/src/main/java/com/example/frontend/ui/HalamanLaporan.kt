@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.frontend.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.frontend.R
import com.example.frontend.data.model.ReportTransaksiDto
import com.example.frontend.ui.component.Header
import com.example.frontend.ui.theme.Poppins
import com.example.frontend.viewmodel.LaporanViewModel
import com.example.frontend.viewmodel.ProfileViewModel
import java.text.NumberFormat
import java.time.LocalDate
import java.time.YearMonth
import java.util.Locale

@Composable
fun HalamanLaporan() {
    val greenBg = Color(0xFF5F8E65)
    val filterBlue = Color(0xCCFFFFFF)
    val panelBg = Color(0xCCFFFFFF)

    val vm: LaporanViewModel = hiltViewModel()
    val state by vm.state.collectAsStateWithLifecycle()

    val profileVm: ProfileViewModel = hiltViewModel()
    val profileState by profileVm.state.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    val avatarLetter = profileState.username
        .trim()
        .firstOrNull()
        ?.uppercaseChar()
        ?.toString() ?: "A"

    val bulanOptions = remember { bulanIndonesia() }
    val tahunOptions = remember { buildYearsRange(5) }

    var showFilter by remember { mutableStateOf(false) }
    var expandedBulan by remember { mutableStateOf(false) }
    var expandedTahun by remember { mutableStateOf(false) }

    val now = remember { YearMonth.now() }
    var selectedBulan by remember { mutableStateOf(now.monthValue) }
    var selectedTahun by remember { mutableStateOf(now.year) }

    LaunchedEffect(Unit) {
        val (start, end) = monthYearToRange(selectedTahun, selectedBulan)
        vm.load(start, end)
    }

    LaunchedEffect(state.exportError) {
        state.exportError?.let {
            snackbarHostState.showSnackbar(it)
            vm.clearExportError()
        }
    }

    LaunchedEffect(state.exportedUri) {
        val uriStr = state.exportedUri ?: return@LaunchedEffect

        runCatching {
            val uri = Uri.parse(uriStr)
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(Intent.createChooser(intent, "Buka PDF"))
        }.onFailure {
            snackbarHostState.showSnackbar("PDF tersimpan di Downloads, tapi gagal dibuka otomatis.")
        }
    }


    Scaffold(
        containerColor = greenBg,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            Modifier.fillMaxSize().background(greenBg)
        ) {
            Header(
                title = "Laporan",
                subtitle = "Kas Masjid",
                avatarLetter = avatarLetter,
                greenBg = greenBg
            )

            Spacer(Modifier.height(21.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .zIndex(0f)
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(45.dp)
                            .padding(horizontal = 17.dp),
                        colors = CardDefaults.cardColors(containerColor = filterBlue),
                        shape = RoundedCornerShape(5.dp),
                        onClick = {
                            showFilter = !showFilter
                            expandedBulan = false
                            expandedTahun = false
                        }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 70.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.calendar),
                                contentDescription = null,
                                tint = Color(0xFF608B62),
                                modifier = Modifier.size(20.dp)
                            )

                            Spacer(Modifier.width(13.dp))

                            Text(
                                "Pilih periode laporan transaksi",
                                color = Color.Black,
                                modifier = Modifier.weight(1f),
                                fontSize = 12.sp,
                                fontFamily = Poppins,
                                fontWeight = FontWeight.Normal,
                                maxLines = 1
                            )


                            Icon(
                                painter = painterResource(R.drawable.triangle),
                                contentDescription = null,
                                tint = Color(0x80000000),
                                modifier = Modifier
                                    .size(15.dp)
                                    .rotate(if (showFilter) 0f else 180f)
                            )
                        }
                    }

                    Spacer(Modifier.height(13.dp))

                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp)
                                .height(40.dp)
                                .background(
                                    panelBg,
                                    RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)
                                )
                                .padding(horizontal = 9.dp, vertical = 12.dp)
                        ) {
                            val bulanLabel = bulanOptions.first { it.second == selectedBulan }.first
                            Text(
                                text = "Ringkasan $bulanLabel $selectedTahun",
                                fontSize = 12.sp,
                                fontFamily = Poppins,
                                color = Color(0xFF000000),
                            )
                        }
                    }

                    if (state.isLoading) {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }

                    state.error?.let {
                        Text(
                            it,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(16.dp)
                        )
                    }

                    state.report?.let { report ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp)
                                .background(
                                    color = Color.White.copy(alpha = 0.85f),
                                    shape = RoundedCornerShape(
                                        bottomStart = 10.dp,
                                        bottomEnd = 10.dp
                                    )
                                )
                                .padding(horizontal = 12.dp, vertical = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            SummaryRow("Total Pemasukan", rupiah(report.totalPemasukan))
                            SummaryRow(
                                "Total Pengeluaran",
                                rupiah(report.totalPengeluaran),
                                pengeluaran = true
                            )
                            Divider(color = Color(0x22000000))
                            SummaryRow("Saldo Bersih", rupiah(report.saldoPeriode), bold = true)

                            Spacer(Modifier.height(6.dp))

                            Button(
                                onClick = { vm.exportPdf(context) },
                                enabled = !state.isExporting,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(42.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(
                                        0xFF3BA200
                                    )
                                )
                            ) {
                                if (state.isExporting) {
                                    CircularProgressIndicator(modifier = Modifier.size(18.dp))
                                } else {
                                    Icon(
                                        painter = painterResource(R.drawable.pdf),
                                        contentDescription = null,
                                        tint = Color(0xFFFFFFFF),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    "Ekspor PDF",
                                    fontFamily = Poppins,
                                    fontSize = 14.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight(500)
                                )
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp)
                                    .height(43.dp)
                                    .background(
                                        panelBg,
                                        RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)
                                    )
                                    .padding(horizontal = 9.dp, vertical = 12.dp)
                            ) {
                                Text(
                                    text = "Riwayat laporan",
                                    fontSize = 12.sp,
                                    fontFamily = Poppins,
                                    color = Color(0x99000000),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }

                        if (report.transaksi.isEmpty()) {
                            Text(
                                "Tidak ada transaksi pada periode ini.",
                                color = Color.White,
                                modifier = Modifier.padding(16.dp),
                                fontFamily = Poppins
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxWidth(),
                                contentPadding = PaddingValues(
                                    start = 20.dp,
                                    end = 20.dp,
                                    top = 1.dp,
                                    bottom = 16.dp
                                ),
                                verticalArrangement = Arrangement.spacedBy(1.dp)
                            ) {
                                items(report.transaksi, key = { it.id }) { t ->
                                    TransaksiRowLaporan(trx = t)
                                }
                            }
                        }
                    }
                }

                if (showFilter) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .zIndex(1f)
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) { showFilter = false }
                    )
                }

                androidx.compose.animation.AnimatedVisibility(
                    visible = showFilter,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically(),
                    modifier = Modifier
                        .zIndex(2f)
                        .align(Alignment.TopCenter)
                        .padding(top = 25.dp + 23.dp + 8.dp)
                ) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .heightIn(min = 220.dp, max = 320.dp)
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) { },
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFFFFFFF),
                        shadowElevation = 8.dp
                    ) {
                        Column(
                            Modifier.padding(23.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                "Bulan:",
                                fontSize = 10.sp,
                                fontFamily = Poppins,
                                color = Color(0x99000000)
                            )

                            ExposedDropdownMenuBox(
                                expanded = expandedBulan,
                                onExpandedChange = {
                                    expandedBulan = !expandedBulan
                                    expandedTahun = false
                                }
                            ) {
                                KasSmallField(
                                    value = bulanOptions.first { it.second == selectedBulan }.first,
                                    onValueChange = {},
                                    placeholder = "",
                                    readOnly = true,
                                    modifier = Modifier.menuAnchor(
                                        MenuAnchorType.PrimaryNotEditable,
                                        enabled = true
                                    ),
                                    trailing = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedBulan) }
                                )

                                ExposedDropdownMenu(
                                    expanded = expandedBulan,
                                    onDismissRequest = { expandedBulan = false },
                                    modifier = Modifier.background(Color.White)
                                ) {
                                    bulanOptions.forEach { (nama, idx) ->
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    nama,
                                                    fontFamily = Poppins,
                                                    fontSize = 12.sp,
                                                    color = Color.Black
                                                )
                                            },
                                            onClick = {
                                                selectedBulan = idx
                                                expandedBulan = false
                                            }
                                        )
                                    }
                                }
                            }

                            Text(
                                "Tahun:",
                                fontSize = 10.sp,
                                fontFamily = Poppins,
                                color = Color.Black
                            )

                            ExposedDropdownMenuBox(
                                expanded = expandedTahun,
                                onExpandedChange = {
                                    expandedTahun = !expandedTahun
                                    expandedBulan = false
                                }
                            ) {
                                KasSmallField(
                                    value = selectedTahun.toString(),
                                    onValueChange = {},
                                    placeholder = "",
                                    readOnly = true,
                                    modifier = Modifier.menuAnchor(
                                        MenuAnchorType.PrimaryNotEditable,
                                        enabled = true
                                    ),
                                    trailing = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTahun) }
                                )

                                ExposedDropdownMenu(
                                    expanded = expandedTahun,
                                    onDismissRequest = { expandedTahun = false },
                                    modifier = Modifier.background(Color.White)
                                ) {
                                    tahunOptions.forEach { yr ->
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    yr.toString(),
                                                    fontFamily = Poppins,
                                                    fontSize = 12.sp,
                                                    color = Color.Black
                                                )
                                            },
                                            onClick = {
                                                selectedTahun = yr
                                                expandedTahun = false
                                            }
                                        )
                                    }
                                }
                            }

                            Spacer(Modifier.height(6.dp))

                            Button(
                                onClick = {
                                    val (start, end) = monthYearToRange(
                                        selectedTahun,
                                        selectedBulan
                                    )
                                    vm.load(start, end)
                                    showFilter = false
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(
                                        0xFF3BA200
                                    )
                                ),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    "Terapkan",
                                    color = Color.White,
                                    fontFamily = Poppins,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

    @Composable
    private fun SummaryRow(
        label: String,
        value: String,
        bold: Boolean = false,
        pengeluaran: Boolean = false
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = label,
                fontFamily = Poppins,
                fontSize = 10.sp,
                color = Color.Black,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = value,
                fontFamily = Poppins,
                fontSize = 12.sp,
                fontWeight = if (bold) FontWeight.SemiBold else FontWeight.Normal,
                color = if (pengeluaran) Color(0xFFE51F1F) else Color(0xFF4DAC2F)
            )
        }
    }

    @Composable
    private fun KasSmallField(
        value: String,
        onValueChange: (String) -> Unit,
        placeholder: String,
        modifier: Modifier = Modifier,
        readOnly: Boolean = false,
        trailing: @Composable (() -> Unit)? = null,
    ) {
        val shape = RoundedCornerShape(6.dp)
        val interactionSource = remember { MutableInteractionSource() }

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            readOnly = readOnly,
            singleLine = true,
            textStyle = TextStyle(fontSize = 15.sp, fontFamily = Poppins, color = Color.Black),
            modifier = modifier
                .fillMaxWidth()
                .height(38.dp)
                .clip(shape)
                .background(Color.White)
                .padding(horizontal = 10.dp, vertical = 6.dp),
            decorationBox = { inner ->
                TextFieldDefaults.DecorationBox(
                    value = value,
                    innerTextField = inner,
                    enabled = true,
                    singleLine = true,
                    visualTransformation = androidx.compose.ui.text.input.VisualTransformation.None,
                    interactionSource = interactionSource,
                    placeholder = {
                        if (value.isEmpty()) {
                            Text(
                                text = placeholder,
                                fontSize = 12.sp,
                                fontFamily = Poppins,
                                color = Color(0x80000000)
                            )
                        }
                    },
                    trailingIcon = trailing,
                    contentPadding = PaddingValues(0.dp),
                    container = {}
                )
            }
        )
    }

    @Composable
    private fun TransaksiRowLaporan(trx: ReportTransaksiDto) {
        val isMasuk = trx.jenis.equals("PEMASUKAN", true)

        val iconBg = if (isMasuk) Color(0xFF87CE5E) else Color(0xFFE75D5D)
        val amountBg = Color(0xFFCFE1FF)
        val amountText = Color(0xFF000000)
        val badgeBg = if (isMasuk) Color(0xFFC5EEB8) else Color(0xFFEABDBD)
        val badgeText = Color(0xFF111827)

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(1.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.8f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 72.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 7.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(iconBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(
                                id = if (isMasuk) R.drawable.pemasukan else R.drawable.pengeluaran
                            ),
                            modifier = Modifier.size(20.dp),
                            contentDescription = null,
                            tint = Color.Black
                        )
                    }

                    Spacer(Modifier.width(21.dp))

                    Column(Modifier.weight(1f)) {
                        Text(
                            text = trx.kategoriNama ?: "Kategori",
                            fontWeight = FontWeight.Normal,
                            color = Color(0xFF000000),
                            fontSize = 15.sp,
                            lineHeight = 17.sp,
                            fontFamily = Poppins,
                            maxLines = 1
                        )

                        Text(
                            text = trx.keterangan?.takeIf { it.isNotBlank() } ?: "-",
                            color = Color(0x80000000),
                            fontSize = 10.sp,
                            lineHeight = 10.sp,
                            maxLines = 1,
                            fontFamily = Poppins
                        )

                        Spacer(Modifier.height(4.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(color = amountBg, shape = RoundedCornerShape(4.dp)) {
                                Text(
                                    text = (if (isMasuk) "+" else "-") + rupiah(trx.nominal),
                                    modifier = Modifier.padding(
                                        horizontal = 10.dp,
                                        vertical = 6.dp
                                    ),
                                    fontWeight = FontWeight.Normal,
                                    fontFamily = Poppins,
                                    fontSize = 12.sp,
                                    color = amountText
                                )
                            }

                            Spacer(Modifier.width(7.dp))

                            Surface(color = badgeBg, shape = RoundedCornerShape(4.dp)) {
                                Text(
                                    text = if (isMasuk) "PEMASUKAN" else "PENGELUARAN",
                                    modifier = Modifier.padding(
                                        horizontal = 10.dp,
                                        vertical = 6.dp
                                    ),
                                    fontWeight = FontWeight.Normal,
                                    fontFamily = Poppins,
                                    fontSize = 12.sp,
                                    color = badgeText
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun rupiah(x: Long): String {
        val nf = NumberFormat.getInstance(Locale("id", "ID"))
        return "Rp ${nf.format(x)}"
    }

    private fun bulanIndonesia() = listOf(
        "Januari" to 1, "Februari" to 2, "Maret" to 3, "April" to 4,
        "Mei" to 5, "Juni" to 6, "Juli" to 7, "Agustus" to 8,
        "September" to 9, "Oktober" to 10, "November" to 11, "Desember" to 12
    )

    private fun buildYearsRange(range: Int): List<Int> {
        val now = LocalDate.now().year
        return ((now - range)..(now + range)).toList()
    }

    private fun monthYearToRange(year: Int, month: Int): Pair<String, String> {
        val ym = YearMonth.of(year, month)
        return ym.atDay(1).toString() to ym.atEndOfMonth().toString()
    }
