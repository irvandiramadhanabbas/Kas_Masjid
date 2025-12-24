package com.example.frontend.ui.ketua.sheet

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ToggleOn
import androidx.compose.material.icons.filled.Work
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.frontend.R
import com.example.frontend.data.model.Pengguna
import com.example.frontend.ui.theme.Poppins

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdatePengguna(
    user: Pengguna,
    bottomInset: Dp = 0.dp,
    onDismiss: () -> Unit,
    onSubmit: (
        username: String?,
        email: String?,
        role: String?,
        status: String?
    ) -> Unit
) {
    var username by remember { mutableStateOf(user.username) }
    var email by remember { mutableStateOf(user.email) }
    var role by remember { mutableStateOf(user.role) }
    var status by remember { mutableStateOf(user.status) }

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
                .heightIn(min = 280.dp)
                .padding(horizontal = 20.dp)
                .padding(bottom = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
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
                    painter = painterResource(R.drawable.variant2),
                    contentDescription = null,
                    tint = Color(0xFF608B62)
                )
                Text(
                    text = "Update Pengguna",
                    fontSize = 16.sp,
                    color = Color(0xFF111827)
                )
            }

            Spacer(Modifier.height(20.dp))

            InputFieldSimpleSameAsTambah(
                value = username,
                onValueChange = { username = it },
                placeholder = "Masukkan username",
                icon = Icons.Default.Person
            )

            Spacer(Modifier.height(18.dp))

            InputFieldSimpleSameAsTambah(
                value = email,
                onValueChange = { email = it },
                placeholder = "Masukkan email",
                icon = Icons.Default.Email
            )

            Spacer(Modifier.height(18.dp))

            InputFieldSimpleSameAsTambah(
                value = role,
                onValueChange = { role = it },
                placeholder = "Masukkan role",
                icon = Icons.Default.Work
            )

            Spacer(Modifier.height(18.dp))

            InputFieldSimpleSameAsTambah(
                value = status,
                onValueChange = { status = it },
                placeholder = "Masukkan status (Aktif / Nonaktif)",
                icon = Icons.Default.ToggleOn
            )

            Spacer(Modifier.height(18.dp))

            Button(
                onClick = {
                    onSubmit(
                        username.trim().ifBlank { null },
                        email.trim().ifBlank { null },
                        role.trim().uppercase().ifBlank { null },
                        status.trim().replaceFirstChar { it.uppercase() }.ifBlank { null }
                    )
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
private fun InputFieldSimpleSameAsTambah(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    icon: ImageVector
) {
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
}
