package com.example.frontend.ui.ketua.component

import android.annotation.SuppressLint
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.frontend.R
import com.example.frontend.data.model.Pengguna
import com.example.frontend.ui.theme.Poppins

@SuppressLint("UnrememberedMutableInteractionSource")
@Composable
fun PenggunaCard(
    modifier: Modifier = Modifier,
    user: Pengguna,

    isMenuOpen: Boolean,
    onOpenMenu: () -> Unit,
    onCloseMenu: () -> Unit,

    onTambah: () -> Unit,
    onUpdate: (Pengguna) -> Unit,
    onUpdatePassword: (Pengguna) -> Unit,
    onDelete: (Pengguna) -> Unit
) {
    val cardBg = Color(0xFFE7ECE7)
    val avatarBg = Color(0xFF8FB894)

    val roleBg = Color(0xFFD6EAFB)
    val statusAktifBg = Color(0xFFCDEFCF)
    val statusNonBg = Color(0xFFE5E7EB)

    val isAktif = user.status.equals("Aktif", true)
    val dotColor = if (isAktif) Color(0xFF2E7D32) else Color(0xFF9CA3AF)
    val statusBg = if (isAktif) statusAktifBg else statusNonBg

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = cardBg),
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 1.dp, vertical = 1.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.size(40.dp)) {

                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clip(RoundedCornerShape(8.dp))
                            .background(avatarBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = user.username.firstOrNull()?.uppercase() ?: "U",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                    }


                    StatusDot(
                        isActive = isAktif,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .offset(x = 4.dp, y = 1.dp)
                    )
                }

                Spacer(Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp) // ⬅️ jarak antar Text
                ) {
                    Text(
                        text = user.username,
                        fontSize = 12.sp,
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Normal,
                        lineHeight = 14.sp, // ⬅️ penting
                        color = Color(0xFF000000),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = user.email,
                        fontSize = 10.sp,
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Normal,
                        lineHeight = 12.sp, // ⬅️ penting
                        color = Color(0x80000000),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                Spacer(Modifier.height(1.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Pill(text = user.role, bg = roleBg)
                        Pill(
                            text = if (isAktif) "Aktif" else "Nonaktif",
                            bg = statusBg,
                            leadingDotColor = dotColor
                        )
                    }
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Box(
                        modifier = Modifier
                            .size(35.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0x80EEB8B9))
                            .clickable { onDelete(user) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Hapus",
                            tint = Color(0xFFE51F1F),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(35.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0x80B8C8EE))
                            .clickable { onOpenMenu() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = Color(0xFF2A64AF),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            if (isMenuOpen) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable(
                            interactionSource = MutableInteractionSource(),
                            indication = null
                        ) { onCloseMenu() }
                )
            }

            androidx.compose.animation.AnimatedVisibility(
                visible = isMenuOpen,
                enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn(),
                exit = slideOutHorizontally(targetOffsetX = { it }) + fadeOut(),
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .fillMaxWidth()
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(2.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column {
                        Row(Modifier.fillMaxWidth()) {
                            ActionCell(
                                modifier = Modifier.weight(1f),
                                iconRes = R.drawable.tambah,
                                text = "Tambah Pengguna",
                                onClick = {
                                    onCloseMenu()
                                    onTambah()
                                }
                            )
                            DividerV()
                            ActionCell(
                                modifier = Modifier.weight(1f),
                                iconRes = R.drawable.reset, // icon update password
                                text = "Update Password",
                                onClick = {
                                    onCloseMenu()
                                    onUpdatePassword(user)
                                }
                            )
                        }
                        DividerH()
                        Row(Modifier.fillMaxWidth()) {
                            ActionCell(
                                modifier = Modifier.weight(1f),
                                iconRes = R.drawable.update, // icon update pengguna
                                text = "Update Pengguna",
                                onClick = {
                                    onCloseMenu()
                                    onUpdate(user)
                                }
                            )
                            DividerV()
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Pill(
    text: String,
    bg: Color,
    leadingDotColor: Color? = null
) {
    Row(
        modifier = Modifier
            .heightIn(min = 34.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(bg)
            .padding(horizontal = 20.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (leadingDotColor != null) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(leadingDotColor)
            )
            Spacer(Modifier.width(8.dp))
        }

        Text(
            text = text,
            fontSize = 12.sp,
            fontFamily = Poppins,
            fontWeight = FontWeight.Normal,
            color = Color(0x80000000),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun ActionCell(
    modifier: Modifier,
    iconRes: Int,
    text: String,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .height(58.dp)
            .clickable { onClick() }
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            tint = Color.Unspecified, // svg sudah berwarna
            modifier = Modifier.size(16.dp)
        )

        Text(
            text = text,
            fontFamily = Poppins,
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
            color = Color(0xFF1F2937),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}


@Composable
private fun DividerH() {
    HorizontalDivider(thickness = 1.dp, color = Color(0xFFE5E7EB))
}

@Composable
private fun DividerV() {
    Box(
        modifier = Modifier
            .width(1.dp)
            .height(58.dp)
            .background(Color(0xFFE5E7EB))
    )
}

@Composable
private fun StatusDot(
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    val fill = if (isActive) Color(0xFF4DAC2F) else Color(0xFF879192)
    val border = Color(0xFFFFFFFF)

    Box(
        modifier = modifier
            .size(10.dp)
            .clip(CircleShape)
            .background(fill)
            .border(1.dp, border, CircleShape)
    )
}
