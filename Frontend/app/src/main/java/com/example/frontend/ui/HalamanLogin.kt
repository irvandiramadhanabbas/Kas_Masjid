package com.example.frontend.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.frontend.R
import com.example.frontend.data.model.LoginUser
import com.example.frontend.ui.theme.Gudea
import com.example.frontend.ui.theme.Poppins
import com.example.frontend.ui.theme.SetSystemBars
import com.example.frontend.viewmodel.AuthState
import com.example.frontend.viewmodel.AuthViewModel

@Composable
fun HalamanLogin(
    viewModel: AuthViewModel,
    onLoginSuccess: (LoginUser) -> Unit
) {
    val state by viewModel.authState.collectAsStateWithLifecycle()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(state) {
        if (state is AuthState.Success) {
            onLoginSuccess((state as AuthState.Success).user)
        }
    }

    SetSystemBars(
        darkIcons = true
    )

    Scaffold { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .zIndex(0f)
            ) {
                drawRect(Color(0xFFE6F7E1))

                val ovalFrontW = 583.dp.toPx()
                val ovalFrontH = 880.dp.toPx()
                val ovalBackW = 560.dp.toPx()
                val ovalBackH = 736.dp.toPx()

                val centerXFront = (size.width - ovalFrontW) / 2f
                val centerXBack = (size.width - ovalBackW) / 2f

                drawOval(
                    color = Color(0xFF82B285).copy(alpha = 0.55f),
                    topLeft = Offset(centerXBack, 110.dp.toPx()),
                    size = androidx.compose.ui.geometry.Size(ovalBackW, ovalBackH)
                )

                drawOval(
                    color = Color(0xFF608B62),
                    topLeft = Offset(centerXFront, 180.dp.toPx()),
                    size = androidx.compose.ui.geometry.Size(ovalFrontW, ovalFrontH)
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .statusBarsPadding()
                    .padding(top = 150.dp)
                    .zIndex(2f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_aplikasi_kas_foreground),
                    contentDescription = null,
                    modifier = Modifier
                        .size(350.dp)
                        .offset(y = 20.dp)
                )

                Text(
                    text = "Kelola keuangan masjid dengan\nmudah dan transparan",
                    modifier = Modifier
                        .offset(y = (-60).dp)
                        .alpha(0.7f)
                        .width(204.dp)
                        .height(38.dp),
                    style = TextStyle(
                        fontSize = 15.sp,
                        fontFamily = Gudea,
                        fontWeight = FontWeight.Normal,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(padding)
                    .padding(horizontal = 24.dp)
                    .padding(top = 520.dp)
                    .zIndex(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextField(
                    value = email,
                    onValueChange = { email = it },
                    modifier = Modifier
                        .width(290.dp)
                        .height(60.dp),
                    placeholder = {
                        Text(
                            "Masukkan Email",
                            color = Color(0xFF9CA3AF),
                            fontFamily = Poppins,
                            fontWeight = FontWeight.Normal,
                            fontSize = 16.sp
                        )
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Email, null, tint = Color(0xFF608B62))
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(18.dp),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )

                Spacer(Modifier.height(14.dp))

                TextField(
                    value = password,
                    onValueChange = { password = it },
                    modifier = Modifier
                        .width(290.dp)
                        .height(60.dp),
                    placeholder = {
                        Text(
                            "Masukkan Password",
                            color = Color(0xFF9CA3AF),
                            fontFamily = Poppins,
                            fontWeight = FontWeight.Normal,
                            fontSize = 16.sp
                        )
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Lock, null, tint = Color(0xFF608B62))
                    },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = null,
                                tint = Color(0xFF000000)
                            )
                        }
                    },
                    visualTransformation =
                    if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    singleLine = true,
                    shape = RoundedCornerShape(18.dp),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )

                Spacer(Modifier.height(22.dp))

                Button(
                    onClick = { viewModel.login(email.trim(), password) },
                    modifier = Modifier
                        .width(290.dp)
                        .height(60.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF365626)),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = "Masuk",
                        color = Color.White,
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Normal,
                        fontSize = 20.sp
                    )
                }

                if (state is AuthState.Error) {
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = (state as AuthState.Error).message,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
