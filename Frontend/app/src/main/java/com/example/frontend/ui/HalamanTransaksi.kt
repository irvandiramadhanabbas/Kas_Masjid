package com.example.frontend.ui

import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.frontend.R
import com.example.frontend.data.model.KategoriResponse
import com.example.frontend.data.model.TransaksiDto
import com.example.frontend.ui.theme.Poppins
import com.example.frontend.viewmodel.KategoriViewModel
import com.example.frontend.viewmodel.ProfileViewModel
import com.example.frontend.viewmodel.TransaksiFilter
import com.example.frontend.viewmodel.TransaksiViewModel
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.snapshotFlow
import com.example.frontend.ui.component.Header
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanTransaksi() {
    val greenBg = Color(0xFF5F8E65)
    val filterBlue = Color(0xCC3876F3)
    val panelBg = Color(0xCCFFFFFF)

    var showFilter by remember { mutableStateOf(false) }

    val trxVm: TransaksiViewModel = hiltViewModel()
    val trxState by trxVm.state.collectAsStateWithLifecycle()

    DisposableEffect(Unit) {
        onDispose {
            trxVm.resetFilter()
        }
    }

    val profileVm: ProfileViewModel = hiltViewModel()
    val profileState by profileVm.state.collectAsStateWithLifecycle()

    val kategoriVm: KategoriViewModel = hiltViewModel()
    val kategoriState by kategoriVm.state.collectAsStateWithLifecycle()

    val avatarLetter = profileState.username
        .trim()
        .firstOrNull()
        ?.uppercaseChar()
        ?.toString() ?: "A"

    val filteredItems = remember(trxState.items, trxState.filter) {
        trxState.items.filterBy(trxState.filter)
    }

    var openDetailId by remember { mutableStateOf<Int?>(null) }

    val listState = rememberLazyListState()

    val maxDisplay = 100
    val initialLoad = 50
    val stepLoad = 25

    val cappedItems = remember(filteredItems) { filteredItems.take(maxDisplay) }

    var visibleCount by remember { mutableStateOf(initialLoad) }

    LaunchedEffect(cappedItems.size) {
        visibleCount = minOf(initialLoad, cappedItems.size)
    }

    LaunchedEffect(listState, cappedItems.size) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0 }
            .map { lastVisible -> lastVisible >= (visibleCount - 8) }
            .distinctUntilChanged()
            .collect { shouldLoadMore ->
                if (shouldLoadMore && visibleCount < cappedItems.size) {
                    visibleCount = minOf(visibleCount + stepLoad, cappedItems.size)
                }
            }
    }

    val shownItems = remember(cappedItems, visibleCount) { cappedItems.take(visibleCount) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(greenBg)
    ) {
        Header(
            title = "Transaksi",
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
                    shape = RoundedCornerShape(10.dp),
                    onClick = { showFilter = !showFilter }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.filter),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )

                        Spacer(Modifier.width(6.dp))

                        Text(
                            "Filter Transaksi",
                            color = Color.White,
                            modifier = Modifier.weight(1f),
                            fontSize = 12.sp,
                            fontFamily = Poppins,
                            fontWeight = FontWeight.Normal
                        )

                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.triangle),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier
                                .size(15.dp)
                                .rotate(if (showFilter) 0f else 180f)
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .width(380.dp)
                            .height(43.dp)
                            .background(
                                color = panelBg,
                                shape = RoundedCornerShape(
                                    topStart = 10.dp,
                                    topEnd = 10.dp
                                )
                            )
                            .padding(horizontal = 9.dp, vertical = 12.dp)
                    ) {
                        Text(
                            text = "Riwayat transaksi",
                            fontSize = 12.sp,
                            fontFamily = Poppins,
                            color = Color(0x99000000),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                if (trxState.isLoading) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }

                trxState.error?.let {
                    Text(
                        it,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                if (!trxState.isLoading) {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(
                            start = 20.dp,
                            end = 20.dp,
                            top = 1.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(1.dp)
                    ) {
                        items(shownItems, key = { it.id }) { trx ->
                            val isOpen = openDetailId == trx.id

                            TransaksiItem(
                                trx = trx,
                                isDetailOpen = isOpen,
                                onToggleDetail = { openDetailId = if (isOpen) null else trx.id },
                                onCloseDetail = { if (openDetailId == trx.id) openDetailId = null }
                            )
                        }

                        if (visibleCount < cappedItems.size) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                        }

                        if (filteredItems.size > maxDisplay) {
                            item {
                                Text(
                                    text = "Menampilkan $maxDisplay transaksi teratas dari ${filteredItems.size} hasil filter",
                                    fontSize = 10.sp,
                                    color = Color(0x80000000),
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
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
                        .width(386.dp)
                        .height(400.dp)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) { },
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF8EA1D1),
                    shadowElevation = 8.dp
                ) {
                    Column(Modifier.padding(23.dp)) {
                        FilterTransaksiPanelFlat(
                            kategoriItems = kategoriState.items,
                            initial = trxState.filter,
                            onApply = { f ->
                                trxVm.applyFilter(f)
                                showFilter = false
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterTransaksiPanelFlat(
    kategoriItems: List<KategoriResponse>,
    initial: TransaksiFilter,
    onApply: (TransaksiFilter) -> Unit
) {
    var from by remember { mutableStateOf(initial.from ?: "") }
    var to by remember { mutableStateOf(initial.to ?: "") }

    val jenisOptions = listOf("Semua", "Pemasukan", "Pengeluaran")
    var jenisExpanded by remember { mutableStateOf(false) }
    var jenisLabel by remember {
        mutableStateOf(
            when (initial.jenis?.uppercase(Locale.ROOT)) {
                "PEMASUKAN" -> "Pemasukan"
                "PENGELUARAN" -> "Pengeluaran"
                else -> "Semua"
            }
        )
    }

    val kategoriOptions = remember(kategoriItems) {
        listOf(KategoriResponse(0, "Semua")) + kategoriItems
    }
    var kategoriExpanded by remember { mutableStateOf(false) }
    var selectedKategoriId by remember { mutableStateOf(initial.kategoriId ?: 0) }
    var kategoriLabel by remember {
        mutableStateOf(
            kategoriOptions.firstOrNull { it.id == (initial.kategoriId ?: 0) }?.nama ?: "Semua"
        )
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(1.dp)
    ) {
        Text("Tanggal mulai:", fontSize = 10.sp, fontFamily = Poppins, color = Color.Black)
        KasSmallField(
            value = from,
            onValueChange = { from = it },
            placeholder = "dd/mm/yy"
        )

        Spacer(Modifier.height(5.dp))

        Text("Tanggal Akhir:", fontSize = 10.sp, fontFamily = Poppins, color = Color.Black)
        KasSmallField(
            value = to,
            onValueChange = { to = it },
            placeholder = "dd/mm/yy"
        )

        Spacer(Modifier.height(5.dp))

        Text("Jenis Transaksi:", fontSize = 10.sp, fontFamily = Poppins, color = Color.Black)
        ExposedDropdownMenuBox(
            expanded = jenisExpanded,
            onExpandedChange = { jenisExpanded = !jenisExpanded }
        ) {
            KasSmallField(
                value = jenisLabel,
                onValueChange = {},
                placeholder = "",
                readOnly = true,
                modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true),
                trailing = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = jenisExpanded) }
            )

            ExposedDropdownMenu(
                expanded = jenisExpanded,
                onDismissRequest = { jenisExpanded = false },
                modifier = Modifier
                    .heightIn(max = 150.dp)
                    .background(color = Color.White)
            ) {
                jenisOptions.forEach { opt ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                opt,
                                fontFamily = Poppins,
                                fontSize = 12.sp,
                                color = Color(0x80000000)
                            )
                        },
                        onClick = {
                            jenisLabel = opt
                            jenisExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(5.dp))

        Text("Kategori:", fontSize = 10.sp, fontFamily = Poppins, color = Color.Black)
        ExposedDropdownMenuBox(
            expanded = kategoriExpanded,
            onExpandedChange = { kategoriExpanded = !kategoriExpanded }
        ) {
            KasSmallField(
                value = kategoriLabel,
                onValueChange = {},
                placeholder = "Masukkan kategori",
                readOnly = true,
                modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true),
                trailing = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = kategoriExpanded) }
            )

            ExposedDropdownMenu(
                expanded = kategoriExpanded,
                onDismissRequest = { kategoriExpanded = false },
                modifier = Modifier
                    .heightIn(max = 150.dp)
                    .background(color = Color.White)
            ) {
                kategoriOptions.forEach { opt ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                opt.nama,
                                fontFamily = Poppins,
                                fontSize = 12.sp,
                                color = Color(0x80000000)
                            )
                        },
                        onClick = {
                            selectedKategoriId = opt.id
                            kategoriLabel = opt.nama
                            kategoriExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(35.dp))

        Button(
            onClick = {
                val jenisValue: String? = when (jenisLabel) {
                    "Pemasukan" -> "PEMASUKAN"
                    "Pengeluaran" -> "PENGELUARAN"
                    else -> null
                }
                val kategoriIdValue: Int? = if (selectedKategoriId == 0) null else selectedKategoriId

                onApply(
                    initial.copy(
                        from = from.takeIf { it.isNotBlank() },
                        to = to.takeIf { it.isNotBlank() },
                        jenis = jenisValue,
                        kategoriId = kategoriIdValue
                    )
                )
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            shape = RoundedCornerShape(6.dp)
        ) {
            Text("Terapkan filter", color = Color.Black, fontFamily = Poppins, fontSize = 12.sp)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
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
        textStyle = TextStyle(
            fontSize = 15.sp,
            fontFamily = Poppins,
            color = Color.Black
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(38.dp)
            .clip(shape)
            .background(Color.White)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        decorationBox = { innerTextField ->
            TextFieldDefaults.DecorationBox(
                value = value,
                innerTextField = innerTextField,
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
                container = { }
            )
        }
    )
}

@Composable
private fun TransaksiItem(
    trx: TransaksiDto,
    isDetailOpen: Boolean,
    onToggleDetail: () -> Unit,
    onCloseDetail: () -> Unit
) {
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
                        .shadow(
                            elevation = 2.dp,
                            spotColor = Color(0x40000000),
                            ambientColor = Color(0x40000000)
                        )
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
                        maxLines = 1
                    )

                    Spacer(Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(color = amountBg, shape = RoundedCornerShape(4.dp)) {
                            Text(
                                text = (if (isMasuk) "+" else "-") + rupiah(trx.nominal),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
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
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                fontWeight = FontWeight.Normal,
                                fontFamily = Poppins,
                                fontSize = 12.sp,
                                color = badgeText
                            )
                        }
                    }
                }

                IconButton(onClick = onToggleDetail) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Detail",
                        tint = Color(0xFF6B7280)
                    )
                }
            }

            androidx.compose.animation.AnimatedVisibility(
                visible = isDetailOpen,
                enter = slideInHorizontally(initialOffsetX = { fullWidth -> fullWidth }) + fadeIn(),
                exit = slideOutHorizontally(targetOffsetX = { fullWidth -> fullWidth }) + fadeOut(),
                modifier = Modifier.matchParentSize()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) { onCloseDetail() }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Keterangan:",
                            fontSize = 10.sp,
                            color = Color(0xFF6B7280),
                            fontFamily = Poppins
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(R.drawable.calendar),
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = Color(0xFF5F8E65)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = trx.tglTransaksi ?: "-",
                                fontSize = 12.sp,
                                color = Color.Black,
                                fontFamily = Poppins
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(R.drawable.update),
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = Color(0xFF5F8E65)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "Dicatat oleh ${trx.dicatatOleh ?: "-"}",
                                fontSize = 12.sp,
                                color = Color.Black,
                                fontFamily = Poppins
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun List<TransaksiDto>.filterBy(f: TransaksiFilter): List<TransaksiDto> {
    val fromNorm = normalizeToBackendDate(f.from)
    val toNorm = normalizeToBackendDate(f.to)

    fun inRange(backendDate: String?, from: String?, to: String?): Boolean {
        if (backendDate.isNullOrBlank()) return false
        if (!from.isNullOrBlank() && backendDate < from) return false
        if (!to.isNullOrBlank() && backendDate > to) return false
        return true
    }

    return this.filter { trx ->
        (f.jenis == null || trx.jenis.equals(f.jenis, true)) &&
                (f.kategoriId == null || trx.kategoriIdDb == f.kategoriId) &&
                ((fromNorm == null && toNorm == null) || inRange(trx.tglTransaksi, fromNorm, toNorm))
    }
}

private fun normalizeToBackendDate(input: String?): String? {
    val s = input?.trim().orEmpty()
    if (s.isBlank()) return null

    if (Regex("""\d{4}-\d{2}-\d{2}""").matches(s)) return s

    val parts = s.replace('-', '/').split('/')
    if (parts.size != 3) return null

    val d = parts[0].toIntOrNull() ?: return null
    val m = parts[1].toIntOrNull() ?: return null
    if (d !in 1..31 || m !in 1..12) return null

    val dd = parts[0].padStart(2, '0')
    val mm = parts[1].padStart(2, '0')

    val yyyy = when (parts[2].length) {
        2 -> "20${parts[2]}"
        4 -> parts[2]
        else -> return null
    }

    return "$yyyy-$mm-$dd"
}

private val rupiahFormatter = NumberFormat.getCurrencyInstance(Locale("in", "ID"))

private fun rupiah(v: Long): String = rupiahFormatter.format(v)

