package com.januarzidanetinendeng.eightcanteen.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.januarzidanetinendeng.eightcanteen.ui.theme.BlueLightBg
import com.januarzidanetinendeng.eightcanteen.ui.theme.BluePrimary
import com.januarzidanetinendeng.eightcanteen.ui.theme.BorderColor
import com.januarzidanetinendeng.eightcanteen.ui.theme.EightCanteenTheme
import com.januarzidanetinendeng.eightcanteen.ui.theme.ScreenBg
import com.januarzidanetinendeng.eightcanteen.ui.theme.TextMuted
import com.januarzidanetinendeng.eightcanteen.ui.theme.TextPrimary
import com.januarzidanetinendeng.eightcanteen.ui.theme.TextSecondary

data class NotificationItem(
    val id: String,
    val title: String,
    val message: String,
    val time: String,
    val isRead: Boolean
)

@Composable
fun NotificationScreen(
    onBackClick: () -> Unit = {}
) {
    val notifications = listOf(
        NotificationItem("1", "Pesanan Siap Diambil!", "Kebab Beef Jumbo (Stand 04) sudah siap. Segera ambil pesananmu sebelum istirahat selesai.", "2 mnt lalu", false),
        NotificationItem("2", "Voucher Baru Ditambahkan", "Voucher Makan Siang Gratis (20 Poin) berhasil ditukarkan dan masuk ke menu Voucher Saya.", "10 mnt lalu", false),
        NotificationItem("3", "Pesanan Diproses", "Pesanan Kebab Beef Jumbo sedang disiapkan oleh penjual.", "15 mnt lalu", true),
        NotificationItem("4", "Bonus Poin Pendaftaran", "Selamat datang! Kamu mendapatkan +5 Loyalty Poin dari Koperasi SMKN 8.", "2 hari lalu", true)
    )

    Scaffold(
        containerColor = ScreenBg,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Notifikasi", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            items(notifications) { notif ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(if (notif.isRead) Color.Transparent else BlueLightBg.copy(alpha = 0.5f))
                        .clickable { }
                        .padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(if (notif.isRead) BorderColor else BluePrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notif",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(
                                text = notif.title,
                                fontSize = 14.sp,
                                fontWeight = if (notif.isRead) FontWeight.SemiBold else FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = notif.time,
                                fontSize = 11.sp,
                                color = if (notif.isRead) TextMuted else BluePrimary,
                                fontWeight = if (notif.isRead) FontWeight.Normal else FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = notif.message,
                            fontSize = 12.sp,
                            color = TextSecondary,
                            lineHeight = 16.sp
                        )
                    }
                }
                HorizontalDivider(color = BorderColor)
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun NotificationScreenPreview() {
    EightCanteenTheme {
        NotificationScreen()
    }
}
