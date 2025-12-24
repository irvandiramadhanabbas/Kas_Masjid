@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.frontend.ui.ketua

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.frontend.R
import com.example.frontend.ui.component.Header
import com.example.frontend.ui.theme.Poppins
import com.example.frontend.viewmodel.ProfileViewModel

@Composable
fun HalamanProfil(
    onLogoutToLogin: () -> Unit,
    onBack: (() -> Unit)? = null,
    vm: ProfileViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsStateWithLifecycle()

    val screenBg = Color(0xFFD9D9D9)
    val green = Color(0xFF608B62)

    val greenPanelHeight = 105.dp

    val avatarSize = 80.dp
    val avatarOverlap = 40.dp

    val avatarLetter = state.username
        .trim()
        .firstOrNull()
        ?.uppercaseChar()
        ?.toString() ?: "A"

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(screenBg)
        ) {
            Header(
                title = "Profil",
                subtitle = "Kas Masjid",
                avatarLetter = avatarLetter,
                greenBg = green
            )

            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(greenPanelHeight)
                        .clip(RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp))
                        .background(green)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Card(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .widthIn(max = 350.dp)
                            .padding(top = greenPanelHeight)
                            .zIndex(0f),
                        shape = RoundedCornerShape(bottomEnd = 30.dp, bottomStart = 30.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = (avatarSize / 2) + 8.dp)
                                .padding(start = 23.dp, end = 20.dp, bottom = 30.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = state.username.ifBlank { "-" },
                                fontFamily = Poppins,
                                fontWeight = FontWeight.Normal,
                                color = Color(0xFF000000),
                                fontSize = 20.sp
                            )
                            Text(
                                text = state.email.ifBlank { "-" },
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    fontFamily = Poppins,
                                    color = Color(0x80000000),
                                    textDecoration = TextDecoration.Underline
                                )
                            )

                            Spacer(Modifier.height(14.dp))
                            HorizontalDivider(color = Color(0x33000000), modifier = Modifier.width(240.dp))
                            Spacer(Modifier.height(14.dp))

                            Text(
                                text = "Informasi Pribadi:",
                                modifier = Modifier.width(240.dp),
                                color = Color(0xFF000000),
                                fontFamily = Poppins,
                                fontWeight = FontWeight.Normal,
                                fontSize = 12.sp
                            )

                            Spacer(Modifier.height(8.dp))

                            InfoCardRow(R.drawable.usname, "Username", state.username)
                            Spacer(Modifier.height(14.dp))
                            InfoCardRow(R.drawable.email, "Email", state.email)
                            Spacer(Modifier.height(14.dp))
                            InfoCardRow(R.drawable.role, "Role", state.role)
                            Spacer(Modifier.height(14.dp))
                            StatusRow(label = "Status Akun", value = state.status)
                        }
                    }
                    Box(
                        modifier = Modifier
                            .padding(top = greenPanelHeight - avatarOverlap) // ✅ nempel di batas panel
                            .size(avatarSize)
                            .zIndex(1f)
                            .border(2.dp, Color(0xFF9FB59B), CircleShape)
                            .padding(2.dp)
                            .background(Color(0xFFC5EEB8), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = avatarLetter,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF111827)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoCardRow(
    iconRes: Int,
    label: String,
    value: String
) {
    val bg = Color(0x80B8C8EE)

    Card(
        modifier = Modifier.width(240.dp).height(50.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = bg),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 14.dp,
                    end = 14.dp,
                    top = 1.dp,
                    bottom = 1.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                tint = Color(0xFF4DAC2F),
                modifier = Modifier.size(20.dp)
            )

            Spacer(Modifier.width(10.dp))

            Column {
                Text(
                    label,
                    color = Color(0x80000000),
                    fontFamily = Poppins,
                    fontSize = 13.sp
                )
                Text(
                    value.ifBlank { "-" },
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF000000),
                    fontFamily = Poppins,
                    fontSize = 15.sp
                )
            }
        }
    }
}

@Composable
private fun StatusRow(
    label: String,
    value: String
) {
    val bg = Color(0x80B8C8EE)
    val statusText = value.ifBlank { "-" }
    val isAktif = statusText.equals("AKTIF", true)

    Card(
        modifier = Modifier.width(240.dp).height(50.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = bg),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 1.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(R.drawable.status),
                contentDescription = null,
                colorFilter = ColorFilter.tint(Color(0xFF4DAC2F))
            )
            Spacer(Modifier.width(9.dp))

            Column(Modifier.weight(1f)) {
                Text(label, color = Color(0xFF6B7280), fontSize = 13.sp)

                Surface(
                    modifier = Modifier.heightIn(min = 28.dp),
                    shape = RoundedCornerShape(5.dp),
                    color = if (isAktif) Color(0xFFC5EEB8) else Color(0xFFFFFFFF),
                    border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp), contentColor = Color(0xFF75BA64)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 0.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(if (isAktif) Color(0xFF4DAC2F) else Color(0xFF6B7280))
                        )
                        Spacer(Modifier.width(7.dp))
                        Text(
                            if (isAktif) "Aktif" else "Nonaktif",
                            color = Color(0xFF111827),
                            fontSize = 10.sp,
                            maxLines = 19,
                            modifier = Modifier.alignByBaseline()
                        )
                    }
                }
            }
        }
    }
}
