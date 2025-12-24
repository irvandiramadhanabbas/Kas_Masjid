@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.frontend.ui.bendahara.component

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.outlined.CleanHands
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.frontend.data.model.KategoriResponse
import com.example.frontend.ui.theme.Poppins
import com.example.frontend.viewmodel.KategoriViewModel

private enum class Mode { MAIN, TAMBAH, EDIT, HAPUS }

private val DropW = 207.dp
private val DropH = 28.dp
private val MenuItemH = 26.dp

private val BtnTambahW = 93.dp
private val BtnW = 93.dp
private val BtnH = 49.dp

private val Border20 = Color(0xFF000000).copy(alpha = 0.20f)
private val BtnTambah = Color(0xFF4DAC2F)
private val BtnEdit = Color(0xFF1F6FEB)
private val BtnHapus = Color(0xFFE51F1F)

@Composable
fun KategoriPopupDialog(
    onDismiss: () -> Unit,
    vm: KategoriViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsStateWithLifecycle()

    var mode by remember { mutableStateOf(Mode.MAIN) }
    var expanded by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf<KategoriResponse?>(null) }

    var tambahNama by remember { mutableStateOf(TextFieldValue("")) }
    var editNamaBaru by remember { mutableStateOf(TextFieldValue("")) }

    val tambahFocus = remember { FocusRequester() }
    var showTambahActions by remember { mutableStateOf(false) }

    fun clearMsgAndCloseMenu() {
        expanded = false
        vm.clearMessage()
    }

    fun go(to: Mode) {
        mode = to
        clearMsgAndCloseMenu()
        when (to) {
            Mode.TAMBAH -> {
                tambahNama = TextFieldValue("")
                showTambahActions = false
            }
            Mode.EDIT -> editNamaBaru = TextFieldValue(selected?.nama ?: "")
            else -> Unit
        }
    }

    val leftScrollEnabled = mode == Mode.TAMBAH || mode == Mode.EDIT

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    indication = null,
                    interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
                ) { onDismiss() }
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 420.dp)
                    .heightIn(min = 240.dp)
                    .clickable(
                        indication = null,
                        interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
                    ) {},
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        val leftModifier = if (leftScrollEnabled) {
                            Modifier.weight(1f).verticalScroll(rememberScrollState())
                        } else {
                            Modifier.weight(1f)
                        }

                        Column(
                            modifier = leftModifier,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            when (mode) {
                                Mode.MAIN -> {
                                    Text("Kategori")

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        CategoryDropdown(
                                            modifier = Modifier.width(DropW).height(DropH),
                                            items = state.items,
                                            expanded = expanded,
                                            selected = selected,
                                            errorText = state.error,
                                            onOpen = { expanded = true },
                                            onClose = { expanded = false },
                                            onSelectedChange = {
                                                Log.d("KATEGORI_DEBUG", "DIPILIH -> id=${it.id}, nama='${it.nama}'")
                                                selected = it
                                                vm.clearMessage()
                                            }
                                        )

                                        Button(
                                            onClick = { go(Mode.TAMBAH) },
                                            modifier = Modifier.width(BtnTambahW).height(BtnH),
                                            colors = ButtonDefaults.buttonColors(containerColor = BtnTambah),
                                            enabled = mode != Mode.TAMBAH
                                        ) { Text("Tambah") }
                                    }

                                    state.error?.let {
                                        Text(it, color = MaterialTheme.colorScheme.error)
                                    }
                                }

                                Mode.TAMBAH -> {
                                    Text("Nama kategori:")

                                    MiniOutlinedTextField(
                                        value = tambahNama.text,
                                        onValueChange = {
                                            tambahNama = TextFieldValue(it)
                                            vm.clearMessage()
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(28.dp)
                                            .focusRequester(tambahFocus)
                                            .onFocusChanged { fs ->
                                                if (fs.isFocused) showTambahActions = true
                                            },
                                        placeholder = "",
                                        leading = {
                                            Icon(
                                                imageVector = Icons.Outlined.Edit,
                                                contentDescription = null,
                                                tint = Color(0xFF111111).copy(alpha = 0.6f),
                                                modifier = Modifier.size(16.dp)
                                            )
                                        },
                                        trailing = {
                                            IconButton(
                                                onClick = {
                                                    showTambahActions = true
                                                    try { tambahFocus.requestFocus() } catch (_: Exception) {}
                                                },
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Outlined.Search,
                                                    contentDescription = null,
                                                    tint = Color(0xFF111111).copy(alpha = 0.6f),
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        },
                                        isError = state.error != null
                                    )

                                    state.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }

                                    if (showTambahActions) {
                                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                            Button(
                                                onClick = {
                                                    val nama = tambahNama.text.trim()
                                                    if (nama.isNotBlank()) {
                                                        vm.tambah(nama) {
                                                            tambahNama = TextFieldValue("")
                                                            showTambahActions = false
                                                            go(Mode.MAIN)
                                                        }
                                                    }
                                                },
                                                modifier = Modifier.width(BtnW).height(BtnH),
                                                contentPadding = PaddingValues(horizontal = 8.dp),
                                                colors = ButtonDefaults.buttonColors(containerColor = BtnTambah, contentColor = Color.Black)
                                            ) { Text("Simpan", fontFamily = Poppins) }

                                            OutlinedButton(
                                                onClick = {
                                                    showTambahActions = false
                                                    go(Mode.MAIN)
                                                },
                                                modifier = Modifier.width(BtnW).height(BtnH),
                                                contentPadding = PaddingValues(horizontal = 8.dp)
                                            ) { Text("Batal", color = Color.Black, fontFamily = Poppins) }
                                        }
                                    }
                                }

                                Mode.EDIT -> {
                                    val item = selected
                                    if (item == null) {
                                        Text("Pilih kategori terlebih dahulu", color = MaterialTheme.colorScheme.error)
                                    } else {
                                        Text("Nama kategori lama:")

                                        MiniOutlinedTextField(
                                            value = item.nama,
                                            onValueChange = {},
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(28.dp),
                                            placeholder = "",
                                            trailing = null,
                                            isError = false,
                                            readonly = true
                                        )

                                        Text("Nama kategori baru:")

                                        MiniOutlinedTextField(
                                            value = editNamaBaru.text,
                                            onValueChange = {
                                                editNamaBaru = TextFieldValue(it)
                                                vm.clearMessage()
                                            },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(28.dp),
                                            placeholder = "Donasi Acara Milad",
                                            leading = {
                                                Icon(
                                                    imageVector = Icons.Outlined.Edit,
                                                    contentDescription = null,
                                                    tint = Color(0xFF111111).copy(alpha = 0.6f),
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            },
                                            isError = state.error != null
                                        )

                                        state.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }

                                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                            Button(
                                                onClick = {
                                                    val nama = editNamaBaru.text.trim()
                                                    if (nama.isNotBlank()) {
                                                        vm.update(item.id, nama) {
                                                            editNamaBaru = TextFieldValue("")
                                                            go(Mode.MAIN)
                                                        }
                                                    }
                                                },
                                                modifier = Modifier.width(BtnW).height(BtnH),
                                                contentPadding = PaddingValues(horizontal = 8.dp),
                                                colors = ButtonDefaults.buttonColors(containerColor = BtnTambah, contentColor = Color.Black)
                                            ) { Text("Simpan", fontFamily = Poppins) }

                                            OutlinedButton(
                                                onClick = { go(Mode.MAIN) },
                                                modifier = Modifier.width(BtnW).height(BtnH),
                                                contentPadding = PaddingValues(horizontal = 8.dp),
                                                border = BorderStroke(1.dp, Color.Black)
                                            ) { Text("Batal", color = Color.Black, fontFamily = Poppins) }
                                        }
                                    }
                                }

                                Mode.HAPUS -> {
                                    Text("Kategori")

                                    CategoryDropdown(
                                        modifier = Modifier.width(DropW).height(DropH),
                                        items = state.items,
                                        expanded = expanded,
                                        selected = selected,
                                        errorText = state.error,
                                        onOpen = { expanded = true },
                                        onClose = { expanded = false },
                                        onSelectedChange = {
                                            selected = it
                                            vm.clearMessage()
                                        }
                                    )

                                    Spacer(Modifier.height(8.dp))

                                    if (selected != null) {

                                        Surface(
                                            color = MaterialTheme.colorScheme.errorContainer,
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(
                                                "Apakah Anda yakin ingin menghapus kategori ini?",
                                                modifier = Modifier.padding(10.dp),
                                                color = MaterialTheme.colorScheme.onErrorContainer
                                            )
                                        }

                                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                            Button(
                                                onClick = {
                                                    val id = selected?.id ?: return@Button
                                                    vm.hapus(id) {
                                                        selected = null
                                                        go(Mode.MAIN)
                                                    }
                                                },
                                                modifier = Modifier.width(BtnW).height(BtnH),
                                                contentPadding = PaddingValues(horizontal = 8.dp),
                                                colors = ButtonDefaults.buttonColors(containerColor = BtnHapus, contentColor = Color.Black)
                                            ) { Text("Ya") }

                                            OutlinedButton(
                                                onClick = { go(Mode.MAIN) },
                                                modifier = Modifier.width(BtnW).height(BtnH),
                                                contentPadding = PaddingValues(horizontal = 8.dp)
                                            ) { Text("Batal", color = Color.Black, fontFamily = Poppins) }
                                        }

                                    } else {
                                        Text(
                                            "Pilih kategori yang ingin dihapus dulu.",
                                            color = Color.Black
                                        )
                                    }
                                }
                            }
                        }

                        Column(
                            modifier = Modifier.width(120.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            horizontalAlignment = Alignment.End
                        ) {
                            Spacer(Modifier.height(8.dp))
                            Spacer(Modifier.height(4.dp))

                            Button(
                                onClick = { go(Mode.TAMBAH) },
                                modifier = Modifier.width(BtnW).height(BtnH),
                                contentPadding = PaddingValues(horizontal = 8.dp),
                                enabled = mode != Mode.TAMBAH,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = BtnTambah,
                                    contentColor = Color.Black,
                                    disabledContainerColor = BtnTambah,
                                    disabledContentColor = Color.Black
                                )
                            ) { Text("Tambah") }

                            Button(
                                onClick = { go(Mode.EDIT) },
                                enabled = selected != null && mode != Mode.EDIT,
                                modifier = Modifier.width(BtnW).height(BtnH),
                                contentPadding = PaddingValues(horizontal = 8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = BtnEdit,
                                    contentColor = Color.Black,
                                    disabledContainerColor = BtnEdit,
                                    disabledContentColor = Color.Black
                                )
                            ) { Text("Edit") }

                            Button(
                                onClick = { go(Mode.HAPUS) },
                                enabled = state.items.isNotEmpty() && mode != Mode.HAPUS,
                                modifier = Modifier.width(BtnW).height(BtnH),
                                contentPadding = PaddingValues(horizontal = 8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = BtnHapus,
                                    contentColor = Color.Black,
                                    disabledContainerColor = BtnHapus,
                                    disabledContentColor = Color.Black
                                )
                            ) { Text("Hapus") }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryDropdown(
    modifier: Modifier = Modifier,
    items: List<KategoriResponse>,
    expanded: Boolean,
    selected: KategoriResponse?,
    errorText: String?,
    onOpen: () -> Unit,
    onClose: () -> Unit,
    onSelectedChange: (KategoriResponse) -> Unit
) {
    val toggle = { if (expanded) onClose() else onOpen() }

    val nama = selected?.nama?.trim().orEmpty()
    val isPlaceholder = nama.isBlank()
    val displayText = if (isPlaceholder) "Kategori yang sudah ada" else nama
    val displayColor =
        if (isPlaceholder) Color(0xFF111111).copy(alpha = 0.6f) else Color(0xFF111111)

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { toggle() }
    ) {
        Box(
            modifier = modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                .border(1.dp, Border20, RoundedCornerShape(8.dp))
                .background(Color.White, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { toggle() }
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = displayText,
                    color = displayColor,
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1
                )
                Spacer(Modifier.weight(1f))
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = null,
                    tint = Color(0xFF111111).copy(alpha = 0.6f),
                    modifier = Modifier.rotate(if (expanded) 180f else 0f)
                )
            }
        }

        errorText?.let { Text(it, color = MaterialTheme.colorScheme.error) }

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = onClose,
            modifier = Modifier
                .width(DropW)
                .background(Color.White)
                .border(1.dp, Border20, RoundedCornerShape(8.dp))
        ) {
            items.forEachIndexed { idx, item ->
                DropdownMenuItem(
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.CleanHands,
                                contentDescription = null,
                                tint = Color(0xFF9BCF8E),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(item.nama, color = Color(0xFF111111))
                        }
                    },
                    onClick = {
                        onSelectedChange(item)
                        onClose()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(MenuItemH),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                )

                if (idx != items.lastIndex) {
                    HorizontalDivider(thickness = 1.dp, color = Border20)
                }
            }
        }
    }
}

@Composable
private fun MiniOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String,
    leading: @Composable (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    readonly: Boolean = false
) {
    val shape = RoundedCornerShape(8.dp)

    Box(
        modifier = modifier
            .border(
                width = 1.dp,
                color = if (isError) MaterialTheme.colorScheme.error else Border20,
                shape = shape
            )
            .background(Color.White, shape),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (leading != null) {
                leading()
                Spacer(Modifier.width(8.dp))
            }

            Box(modifier = Modifier.weight(1f)) {
                if (value.isBlank()) {
                    Text(
                        text = placeholder,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF111111).copy(alpha = 0.6f),
                        maxLines = 1
                    )
                }

                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    singleLine = true,
                    textStyle = MaterialTheme.typography.labelSmall.copy(color = Color.Black),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp)
                )
            }

            if (trailing != null) {
                Spacer(Modifier.width(8.dp))
                trailing()
            }
        }
    }
}
