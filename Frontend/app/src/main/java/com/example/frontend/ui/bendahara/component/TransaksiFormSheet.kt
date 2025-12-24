@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.frontend.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.frontend.R
import com.example.frontend.data.model.KategoriResponse
import com.example.frontend.ui.theme.Poppins

data class TransaksiFormData(
    val tglTransaksi: String,
    val jenis: String,
    val kategoriId: Int,
    val nominal: Long,
    val keterangan: String?
)

data class TransaksiFormInitial(
    val tglTransaksi: String = "",
    val jenis: String = "PEMASUKAN",
    val kategoriId: Int? = null,
    val nominal: String = "",
    val keterangan: String = ""
)

@Composable
fun TransaksiFormSheet(
    title: String,
    headerIconRes: Int,
    kategoriItems: List<KategoriResponse>,
    initial: TransaksiFormInitial = TransaksiFormInitial(),
    isSubmitting: Boolean,
    submitError: String?,
    bottomInset: Dp = 0.dp,
    onDismiss: () -> Unit,
    onSubmit: (TransaksiFormData) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFFB1D0A7),
        shape = RoundedCornerShape(topStart = 80.dp, topEnd = 80.dp),
        modifier = Modifier.padding(bottom = bottomInset),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 19.dp, bottom = 28.dp)
                    .width(80.dp)
                    .height(10.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(Color(0xFF879192))
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { onDismiss() }
            )
        }
    ) {
        TransaksiFormSheetContent(
            title = title,
            headerIconRes = headerIconRes,
            kategoriItems = kategoriItems,
            initial = initial,
            isSubmitting = isSubmitting,
            submitError = submitError,
            onSubmit = onSubmit
        )
    }
}

@Composable
private fun TransaksiFormSheetContent(
    title: String,
    headerIconRes: Int,
    kategoriItems: List<KategoriResponse>,
    initial: TransaksiFormInitial,
    isSubmitting: Boolean,
    submitError: String?,
    onSubmit: (TransaksiFormData) -> Unit
) {
    var tgl by remember(initial.tglTransaksi) { mutableStateOf(initial.tglTransaksi) }

    var jenisText by remember(initial.jenis) { mutableStateOf(initial.jenis) }

    var kategoriText by remember(initial.kategoriId, kategoriItems) {
        mutableStateOf(
            kategoriItems.firstOrNull { it.id == initial.kategoriId }?.nama.orEmpty()
        )
    }

    var nominalText by remember(initial.nominal) { mutableStateOf(initial.nominal) }
    var ket by remember(initial.keterangan) { mutableStateOf(initial.keterangan) }

    val nominal = nominalText.toLongOrNull() ?: 0L

    val tglTrim = tgl.trim()
    val tglFormatValid = Regex("""\d{4}-\d{2}-\d{2}""").matches(tglTrim)
    val tglValid = tglTrim.isNotBlank() && tglFormatValid

    val jenisTrim = jenisText.trim()
    val jenisValid =
        jenisTrim.equals("PEMASUKAN", true) || jenisTrim.equals("PENGELUARAN", true)

    val matchedKategori = remember(kategoriText, kategoriItems) {
        val input = kategoriText.trim()
        if (input.isBlank()) null
        else kategoriItems.firstOrNull { it.nama.equals(input, ignoreCase = true) }
    }

    val canSubmit =
        matchedKategori != null &&
                nominal > 0 &&
                tglValid &&
                jenisValid &&
                !isSubmitting

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 280.dp)
            .padding(horizontal = 20.dp)
            .padding(bottom = 30.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFFE6F7E1))
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                painter = painterResource(id = headerIconRes),
                contentDescription = null,
                tint = Color(0xFF608B62)
            )

            Text(
                text = title,
                fontSize = 15.sp,
                color = Color(0xFF000000),
                fontFamily = Poppins,
                fontWeight = FontWeight(500)
            )
        }

        Spacer(Modifier.height(14.dp))

        submitError?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                fontFamily = Poppins,
                fontSize = 12.sp
            )
            Spacer(Modifier.height(8.dp))
        }

        if (isSubmitting) {
            LinearProgressIndicator(Modifier.fillMaxWidth())
            Spacer(Modifier.height(12.dp))
        }

        SheetField(
            value = tgl,
            onValueChange = { tgl = it },
            placeholder = "Masukkan tanggal transaksi",
            iconRes = R.drawable.calendar,
            supportingError = when {
                tglTrim.isBlank() -> null
                !tglFormatValid -> "(contoh: 2025-12-01)"
                else -> null
            }
        )

        Spacer(Modifier.height(18.dp))

        SheetField(
            value = jenisText,
            onValueChange = { jenisText = it.uppercase() },
            placeholder = "PEMASUKAN atau PENGELUARAN",
            iconRes = R.drawable.tag,
            supportingError = when {
                jenisTrim.isBlank() -> null
                !jenisValid -> "Jenis harus PEMASUKAN / PENGELUARAN"
                else -> null
            }
        )

        Spacer(Modifier.height(18.dp))

        SheetField(
            value = kategoriText,
            onValueChange = { kategoriText = it },
            placeholder = "Masukkan kategori transaksi",
            iconRes = R.drawable.categori,
            supportingError = when {
                kategoriText.trim().isBlank() -> null
                matchedKategori == null -> "Kategori tidak ditemukan (harus sama persis nama kategorinya)"
                else -> null
            }
        )

        Spacer(Modifier.height(18.dp))

        SheetField(
            value = ket,
            onValueChange = { ket = it },
            placeholder = "Masukkan keterangan transaksi",
            iconRes = R.drawable.paper
        )

        Spacer(Modifier.height(18.dp))

        SheetField(
            value = nominalText,
            onValueChange = { nominalText = it.filter(Char::isDigit) },
            placeholder = "Masukkan nominal transaksi",
            iconRes = R.drawable.edit,
            supportingError = when {
                nominalText.isBlank() -> null
                nominal <= 0L -> "Nominal harus lebih dari 0"
                else -> null
            }
        )

        Spacer(Modifier.height(18.dp))

        Button(
            onClick = {
                val kategori = matchedKategori ?: return@Button
                val jenisFix = jenisTrim.uppercase()

                onSubmit(
                    TransaksiFormData(
                        tglTransaksi = tglTrim,
                        jenis = jenisFix,
                        kategoriId = kategori.id,
                        nominal = nominal,
                        keterangan = ket.trim().ifBlank { null }
                    )
                )
            },
            enabled = canSubmit,
            modifier = Modifier
                .width(198.dp)
                .height(42.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE6F7E1))
        ) {
            Text(
                "Simpan Transaksi",
                fontSize = 15.sp,
                fontWeight = FontWeight(500),
                color = Color.Black,
                fontFamily = Poppins
            )
        }
    }
}

@Composable
private fun SheetField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    @DrawableRes iconRes: Int,
    supportingError: String? = null
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            modifier = Modifier
                .width(300.dp)
                .height(42.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color.White)
                .padding(horizontal = 7.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                tint = Color(0xFF608B62),
                modifier = Modifier.size(20.dp)
            )

            Spacer(Modifier.width(20.dp))

            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                textStyle = TextStyle(
                    fontSize = 16.sp,
                    color = Color(0xFF000000),
                    fontFamily = Poppins
                ),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                decorationBox = { inner ->
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (value.isEmpty()) {
                            Text(
                                text = placeholder,
                                fontSize = 11.sp,
                                fontFamily = Poppins,
                                fontWeight = FontWeight(500),
                                color = Color(0x4D000000),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        inner()
                    }
                }
            )
        }

        supportingError?.let {
            Spacer(Modifier.height(6.dp))
            Text(
                text = it,
                color = Color.Black,
                fontSize = 11.sp,
                fontFamily = Poppins,
                modifier = Modifier.width(300.dp)
            )
        }
    }
}
