package com.example.frontend.ui.ketua.sheet

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.frontend.R
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import com.example.frontend.ui.theme.Poppins

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TambahPengguna(
    bottomInset: Dp = 0.dp,
    onDismiss: () -> Unit,
    onSubmit: (
        username: String,
        email: String,
        password: String,
        role: String
    ) -> Unit
) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFFB1D0A7),
        shape = RoundedCornerShape(topStart = 80.dp, topEnd = 80.dp),

        // ✅ ini yang bikin sheet ga nutup bottom nav
        modifier = Modifier.padding(bottom = bottomInset),

        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 19.dp, bottom = 28.dp)
                    .width(80.dp)
                    .height(10.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(Color(0xFF8FA08A))
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { onDismiss() } // ⬅️ tap = close
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 280.dp) // 🔥 sheet ditinggikan
                .padding(horizontal = 20.dp)
                .padding(bottom = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            // ===== HEADER =====
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFE6F7E1))
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.variant2),
                    contentDescription = null,
                    tint = Color(0xFF608B62)
                )
                Text(
                    text = "Tambah Pengguna",
                    fontSize = 16.sp,
                    color = Color(0xFF111827)
                )
            }

            Spacer(Modifier.height(20.dp))

            // ===== INPUTS =====
            InputField(
                value = username,
                onValueChange = { username = it },
                placeholder = "Masukkan username",
                icon = Icons.Default.Person
            )

            Spacer(Modifier.height(18.dp))

            InputField(
                value = email,
                onValueChange = { email = it },
                placeholder = "Masukkan email",
                icon = Icons.Default.Email
            )

            Spacer(Modifier.height(18.dp))

            InputField(
                value = password,
                onValueChange = { password = it },
                placeholder = "Masukkan password minimal 8 karakter",
                icon = Icons.Default.Lock,
                isPassword = true
            )

            Spacer(Modifier.height(18.dp))

            InputField(
                value = role,
                onValueChange = { role = it },
                placeholder = "Masukkan role",
                icon = Icons.Default.Work
            )

            Spacer(Modifier.height(18.dp))

            // ===== BUTTON =====
            Button(
                onClick = {
                    if (
                        username.isNotBlank() &&
                        email.isNotBlank() &&
                        password.length >= 8 &&
                        role.isNotBlank()
                    ) {
                        onSubmit(
                            username.trim(),
                            email.trim(),
                            password,
                            role.trim().uppercase()
                        )
                    }
                },
                modifier = Modifier
                    .width(140.dp)
                    .height(42.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE6F7E1)
                )
            ) {
                Text("Simpan", fontSize = 14.sp, color = Color.Black)
            }
        }
    }
}

@Composable
private fun InputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    icon: ImageVector,
    isPassword: Boolean = false
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .width(300.dp)          // Figma: 300
            .height(42.dp)          // Figma: 42
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White)
            .padding(horizontal = 7.dp), // padding kecil seperti Figma
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
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
                color = Color(0xFF000000)
            ),
            visualTransformation = when {
                isPassword && !passwordVisible -> PasswordVisualTransformation()
                else -> VisualTransformation.None
            },
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

        if (isPassword) {
            IconButton(
                onClick = { passwordVisible = !passwordVisible },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                    contentDescription = null,
                    tint = Color(0xFF5E5E5E),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
