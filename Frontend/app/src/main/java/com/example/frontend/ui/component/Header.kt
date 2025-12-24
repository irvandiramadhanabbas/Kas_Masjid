package com.example.frontend.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.frontend.R
import com.example.frontend.ui.theme.Hanuman
import com.example.frontend.ui.theme.Poppins

@Composable
fun Header(
    title: String,
    subtitle: String,
    avatarLetter: String,
    modifier: Modifier = Modifier,
    greenBg: Color = Color(0xFF608B62),
    masjidLogoRes: Int = R.drawable.logo_masjid,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(greenBg)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(greenBg)
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_aplikasi_kas_foreground),
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                contentScale = ContentScale.FillBounds
            )

            Spacer(Modifier.width(1.dp))

            Box(
                modifier = Modifier
                    .padding(end = 10.dp)
                    .width(3.dp)
                    .height(59.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color.White.copy(alpha = 0.9f))
            )

            Spacer(Modifier.width(9.dp))

            Text(
                text = "$title\n$subtitle",
                modifier = Modifier
                    .width(127.dp)
                    .height(45.dp),
                style = TextStyle(
                    fontSize = 15.sp,
                    fontFamily = Hanuman,
                    fontWeight = FontWeight.Normal,
                    color = Color.White
                )
            )

            Spacer(Modifier.weight(1f))

            Box(
                modifier = Modifier
                    .padding(end = 12.dp)
                    .size(46.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color(0x33000000), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ellipse_3),
                    contentDescription = null,
                    modifier = Modifier.matchParentSize(),
                    contentScale = ContentScale.Fit
                )

                Text(
                    text = avatarLetter,
                    fontSize = 30.sp,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Normal,
                    color = Color.Black,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }

        Spacer(Modifier.height(10.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(0.dp) // biar persis kayak punya kamu yang flat
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = masjidLogoRes),
                    contentDescription = "Logo Masjid",
                    modifier = Modifier
                        .height(55.dp)
                        .width(168.dp),
                    contentScale = ContentScale.Fit
                )
            }
        }
    }
}
