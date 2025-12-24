@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.frontend.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.frontend.data.model.KategoriResponse
import com.example.frontend.viewmodel.TransaksiFilter

@Composable
fun FilterTransaksiPanel(
    kategoriItems: List<KategoriResponse>,
    initial: TransaksiFilter,
    onApply: (TransaksiFilter) -> Unit,
    onClose: () -> Unit
) {
    var tglMulai by remember { mutableStateOf(initial.from?.let(::isoToDdMmYyyy) ?: "") }
    var tglAkhir by remember { mutableStateOf(initial.to?.let(::isoToDdMmYyyy) ?: "") }

    // jenis dropdown
    val jenisOpts = listOf("Semua", "PEMASUKAN", "PENGELUARAN")
    var jenisExpanded by remember { mutableStateOf(false) }
    var jenisLabel by remember {
        mutableStateOf(
            when (initial.jenis) {
                null -> "Semua"
                else -> initial.jenis
            }
        )
    }

    var kategoriExpanded by remember { mutableStateOf(false) }
    var selectedKategori by remember {
        mutableStateOf(kategoriItems.firstOrNull { it.id == initial.kategoriId })
    }

    Card(
        modifier = Modifier.padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            OutlinedTextField(
                value = tglMulai,
                onValueChange = { tglMulai = it },
                label = { Text("Tanggal Mulai") },
                placeholder = { Text("dd/mm/yyyy") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = tglAkhir,
                onValueChange = { tglAkhir = it },
                label = { Text("Tanggal Akhir") },
                placeholder = { Text("dd/mm/yyyy") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Text("Jenis Transaksi:", style = MaterialTheme.typography.labelMedium)
            ExposedDropdownMenuBox(
                expanded = jenisExpanded,
                onExpandedChange = { jenisExpanded = !jenisExpanded }
            ) {
                OutlinedTextField(
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    value = jenisLabel,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(jenisExpanded) },
                    singleLine = true
                )
                ExposedDropdownMenu(
                    expanded = jenisExpanded,
                    onDismissRequest = { jenisExpanded = false }
                ) {
                    jenisOpts.forEach { opt ->
                        DropdownMenuItem(
                            text = { Text(opt) },
                            onClick = {
                                jenisLabel = opt
                                jenisExpanded = false
                            }
                        )
                    }
                }
            }

            Text("Kategori:", style = MaterialTheme.typography.labelMedium)
            ExposedDropdownMenuBox(
                expanded = kategoriExpanded,
                onExpandedChange = { kategoriExpanded = !kategoriExpanded }
            ) {
                OutlinedTextField(
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    value = selectedKategori?.nama ?: "Masukan Kategori",
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(kategoriExpanded) },
                    singleLine = true
                )
                ExposedDropdownMenu(
                    expanded = kategoriExpanded,
                    onDismissRequest = { kategoriExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Semua") },
                        onClick = {
                            selectedKategori = null
                            kategoriExpanded = false
                        }
                    )
                    kategoriItems.forEach { k ->
                        DropdownMenuItem(
                            text = { Text(k.nama) },
                            onClick = {
                                selectedKategori = k
                                kategoriExpanded = false
                            }
                        )
                    }
                }
            }

            Button(
                onClick = {
                    onApply(
                        TransaksiFilter(
                            jenis = if (jenisLabel == "Semua") null else jenisLabel,
                            kategoriId = selectedKategori?.id,
                            from = ddMmYyyyToIsoOrNull(tglMulai),
                            to = ddMmYyyyToIsoOrNull(tglAkhir)
                        )
                    )
                    onClose()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Terapkan filter")
            }
        }
    }
}

private fun ddMmYyyyToIsoOrNull(s: String): String? {
    val t = s.trim()
    if (t.isBlank()) return null
    val m = Regex("""^(\d{2})/(\d{2})/(\d{4})$""").find(t) ?: return null
    val (dd, mm, yyyy) = m.destructured
    return "$yyyy-$mm-$dd"
}

private fun isoToDdMmYyyy(iso: String): String {
    val m = Regex("""^(\d{4})-(\d{2})-(\d{2})$""").find(iso) ?: return iso
    val (yyyy, mm, dd) = m.destructured
    return "$dd/$mm/$yyyy"
}
