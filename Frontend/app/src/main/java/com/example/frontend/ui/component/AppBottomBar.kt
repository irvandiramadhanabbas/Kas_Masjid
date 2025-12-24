package com.example.frontend.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.frontend.R
import com.example.frontend.navigation.Routes
import com.example.frontend.ui.theme.Poppins

@Composable
fun AppBottomBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    selectedColor: Color = Color(0xFF608B62),
    unselectedColor: Color = Color(0xFF879192),
    bgColor: Color = Color.White,
) {
    Surface(
        color = bgColor,
        shadowElevation = 12.dp,
        shape = RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(108.dp)
    ) {
        Box(Modifier.fillMaxSize()) {

            Box(
                Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color(0xFFEAEAEA))
                    .align(Alignment.TopCenter)
            )

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .windowInsetsPadding(WindowInsets.navigationBars),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppBottomItem(
                    label = "Home",
                    icon = Icons.Default.Home,
                    selected = currentRoute == Routes.TAB_DASHBOARD,
                    selectedColor = selectedColor,
                    unselectedColor = unselectedColor,
                    onClick = { onNavigate(Routes.TAB_DASHBOARD) }
                )

                AppBottomItem(
                    label = "Transaksi",
                    icon = ImageVector.vectorResource(R.drawable.transaksi),
                    selected = currentRoute == Routes.TAB_TRANSAKSI,
                    selectedColor = selectedColor,
                    unselectedColor = unselectedColor,
                    onClick = { onNavigate(Routes.TAB_TRANSAKSI) }
                )

                AppBottomItem(
                    label = "Profil",
                    icon = Icons.Default.Person,
                    selected = currentRoute == Routes.TAB_PROFIL,
                    selectedColor = selectedColor,
                    unselectedColor = unselectedColor,
                    onClick = { onNavigate(Routes.TAB_PROFIL) }
                )
            }
        }
    }
}

@Composable
private fun AppBottomItem(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    selectedColor: Color,
    unselectedColor: Color,
    onClick: () -> Unit,
) {
    val contentColor = if (selected) selectedColor else unselectedColor

    Box(
        modifier = Modifier
            .sizeIn(minWidth = 92.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = contentColor,
                modifier = Modifier.size(40.dp)
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = label,
                color = contentColor,
                fontSize = 15.sp,
                fontFamily = Poppins,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
            )
        }
    }
}
