package com.example.frontend.ui.ketua.sheet

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.frontend.R
import com.example.frontend.ui.theme.Poppins
import androidx.compose.material.icons.filled.Check
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResetPasswordPengguna(
    bottomInset: Dp = 0.dp,
    onDismiss: () -> Unit,
    onSubmit: suspend (String) -> Boolean
) {
    var pass1 by remember { mutableStateOf("") }
    var pass2 by remember { mutableStateOf("") }

    var errorText by remember { mutableStateOf<String?>(null) }
    var showSuccess by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    LaunchedEffect(Unit) { sheetState.expand() }

    Box(Modifier.fillMaxSize()) {

        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            containerColor = Color(0xFFB1D0A7),
            shape = RoundedCornerShape(topStart = 80.dp, topEnd = 80.dp),
            modifier = Modifier.padding(bottom = bottomInset),
            dragHandle = {
                Box(
                    modifier = Modifier
                        .padding(bottom = 28.dp, top = 19.dp)
                        .width(80.dp)
                        .height(10.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(Color(0xFF8FA08A))
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) { onDismiss() }
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 380.dp)
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
                        painter = painterResource(R.drawable.reset),
                        contentDescription = null,
                        tint = Color(0xFF608B62)
                    )
                    Text("Update password", fontSize = 16.sp, color = Color(0xFF111827))
                }

                Spacer(Modifier.height(14.dp))

                PasswordField(
                    value = pass1,
                    onValueChange = {
                        pass1 = it
                        errorText = null
                    },
                    placeholder = "Password Baru",
                    icon = Icons.Default.Lock
                )

                Spacer(Modifier.height(14.dp))

                PasswordField(
                    value = pass2,
                    onValueChange = {
                        pass2 = it
                        errorText = null
                    },
                    placeholder = "Konfirmasi Password Baru",
                    icon = Icons.Default.Lock
                )

                if (errorText != null) {
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = errorText!!,
                        color = Color(0xFFE51F1F),
                        fontSize = 12.sp,
                        fontFamily = Poppins,
                        fontWeight = FontWeight(500)
                    )
                }

                Spacer(Modifier.height(24.dp))

                Button(
                    enabled = !loading,
                    onClick = {
                        if (pass1.length < 8 || pass2.length < 8) {
                            errorText = "Password minimal 8 karakter."
                            return@Button
                        }
                        if (pass1 != pass2) {
                            errorText = "Password tidak sesuai."
                            return@Button
                        }

                        loading = true
                        scope.launch {
                            val ok = onSubmit(pass1)
                            loading = false

                            if (ok) {
                                showSuccess = true
                                delay(1200)
                                onDismiss()
                                showSuccess = false
                            } else {
                                errorText = "Gagal reset password. Coba lagi."
                            }
                        }
                    },
                    modifier = Modifier
                        .width(140.dp)
                        .height(42.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE6F7E1))
                ) {
                    if (loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Simpan", fontSize = 14.sp, color = Color.Black)
                    }
                }
            }
        }

        if (showSuccess) {
            Dialog(
                onDismissRequest = { },
                properties = DialogProperties(
                    usePlatformDefaultWidth = false,
                    dismissOnBackPress = false,
                    dismissOnClickOutside = false
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Transparent),
                    contentAlignment = Alignment.Center
                ) {
                    AnimatedVisibility(
                        visible = showSuccess,
                        enter = fadeIn() + scaleIn(initialScale = 0.95f),
                        exit = fadeOut() + scaleOut(targetScale = 0.95f)
                    ) {
                        TopBannerNotif("Password berhasil direset")
                    }
                }
            }
        }




    }
}




@Composable
private fun PasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    icon: ImageVector
) {
    var visible by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .width(328.dp)
            .height(42.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White)
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF608B62),
            modifier = Modifier.size(20.dp)
        )

        Spacer(Modifier.width(10.dp))

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = TextStyle(fontSize = 16.sp, color = Color(0xFF000000)),
            visualTransformation = if (!visible) PasswordVisualTransformation() else VisualTransformation.None,
            modifier = Modifier.weight(1f),
            decorationBox = { inner ->
                Box(contentAlignment = Alignment.CenterStart) {
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

        IconButton(onClick = { visible = !visible }) {
            Icon(
                modifier = Modifier.size(18.dp),
                imageVector = if (visible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                contentDescription = null,
                tint = Color(0xFF000000)
            )
        }
    }
}

@Composable
private fun TopBannerNotif(
    text: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .width(302.dp)
            .height(87.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFFA8EFA5))
            .padding(horizontal = 18.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                fontSize = 14.sp,
                color = Color.Black,
                modifier = Modifier.weight(1f)
            )

            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = Color(0xFF2E7D32),
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

