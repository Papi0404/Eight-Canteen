package com.januarzidanetinendeng.eightcanteen.ui.points

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.januarzidanetinendeng.eightcanteen.ui.components.EKantinLogoIcon
import com.januarzidanetinendeng.eightcanteen.ui.components.ShieldCheckIcon
import com.januarzidanetinendeng.eightcanteen.ui.theme.BlueLightBg
import com.januarzidanetinendeng.eightcanteen.ui.theme.BluePrimary
import com.januarzidanetinendeng.eightcanteen.ui.theme.BorderColor
import com.januarzidanetinendeng.eightcanteen.ui.theme.EightCanteenTheme
import com.januarzidanetinendeng.eightcanteen.ui.theme.InputBg
import com.januarzidanetinendeng.eightcanteen.ui.theme.ScreenBg
import com.januarzidanetinendeng.eightcanteen.ui.theme.TextMuted
import com.januarzidanetinendeng.eightcanteen.ui.theme.TextPrimary
import com.januarzidanetinendeng.eightcanteen.ui.theme.TextSecondary

data class RewardItem(
    val id: String,
    val name: String,
    val standName: String,
    val pointsCost: Int,
    val categoryTag: String,
    val foodEmoji: String
)

data class PointHistoryItem(
    val id: String,
    val title: String,
    val date: String,
    val pointsChange: Int,
    val emoji: String
)

@Composable
fun PointsRewardScreen(
    studentName: String = "Fajar Pratama",
    studentClass: String = "XI RPL 2",
    currentPoints: Int = 25,
    onBackClick: () -> Unit = {},
    onNavigateToHome: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    var selectedNavTab by remember { mutableIntStateOf(0) } // Beranda

    val myVouchers = remember { mutableStateListOf<RewardItem>() }

    val otherRewards = remember {
        listOf(
            RewardItem("r1", "Es Teh Manis Jumbo", "Stand Minuman D & Bu Ani", 5, "Segar", "🥤"),
            RewardItem("r2", "2 Pcs Gorengan...", "Stand Gorengan Pak Kumis", 3, "Camilan", "🧆"),
            RewardItem("r3", "Voucher Diskon Rp...", "Berlaku di Semua Stand", 10, "Potongan", "🎫"),
            RewardItem("r4", "Ekstra Keju / Sambal", "Stand Kebab & Ayam...", 2, "Add-on", "🧀")
        )
    }

    val pointHistory = remember {
        listOf(
            PointHistoryItem("h1", "Pre-order Kebab Jumbo (Kebab Bang Ali)", "Hari ini • 09.42 WIB", 2, "🍱"),
            PointHistoryItem("h2", "Makan Siang Ayam Geprek (Stand A)", "Kemarin • 12.15 WIB", 3, "🍴"),
            PointHistoryItem("h3", "Penukaran Makanan di Stand B", "5 Okt 2025 • 10.05 WIB", -20, "🎁"),
            PointHistoryItem("h4", "Bonus Siswa Baru & Profil", "1 Okt 2025 • 07.30 WIB", 5, "🎉")
        )
    }

    Scaffold(
        containerColor = ScreenBg,
        topBar = {
            // Top Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left Logo Title
                Row(verticalAlignment = Alignment.CenterVertically) {
                    EKantinLogoIcon(size = 36.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Kantin 8",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = BluePrimary
                        )
                        Text(
                            text = "SISWA",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextMuted,
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                // Right Action Icons
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { Toast.makeText(context, "Pencarian Poin", Toast.LENGTH_SHORT).show() }) {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = TextPrimary)
                    }
                    IconButton(onClick = { Toast.makeText(context, "Notifikasi Poin", Toast.LENGTH_SHORT).show() }) {
                        Icon(imageVector = Icons.Default.Notifications, contentDescription = "Notify", tint = TextPrimary)
                    }
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(BluePrimary)
                            .clickable { onNavigateToProfile() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Person, contentDescription = "Profile", tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                }
            }
        },
        bottomBar = {
            // Bottom Navigation Bar (Student Only - STRICT ISOLATION)
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = selectedNavTab == 0,
                    onClick = {
                        selectedNavTab = 0
                        onNavigateToHome()
                    },
                    icon = { Icon(imageVector = Icons.Default.Storefront, contentDescription = "Beranda") },
                    label = { Text("Beranda", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = BluePrimary, selectedTextColor = BluePrimary, indicatorColor = BlueLightBg)
                )
                NavigationBarItem(
                    selected = selectedNavTab == 1,
                    onClick = {
                        selectedNavTab = 1
                        Toast.makeText(context, "Membuka Riwayat Pesanan...", Toast.LENGTH_SHORT).show()
                    },
                    icon = { Icon(imageVector = Icons.AutoMirrored.Filled.ReceiptLong, contentDescription = "Pesanan") },
                    label = { Text("Pesanan", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = BluePrimary, selectedTextColor = BluePrimary, indicatorColor = BlueLightBg)
                )
                NavigationBarItem(
                    selected = selectedNavTab == 2,
                    onClick = {
                        selectedNavTab = 2
                        onNavigateToProfile()
                    },
                    icon = { Icon(imageVector = Icons.Default.Person, contentDescription = "Profil") },
                    label = { Text("Profil", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = BluePrimary, selectedTextColor = BluePrimary, indicatorColor = BlueLightBg)
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Sub-Header Back Navigation Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .border(1.dp, BorderColor, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = TextPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = "Poin & Hadiah",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFEAB308))
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "SMKN 8 Jakarta • Program Siswa Cermat",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }

                IconButton(onClick = { Toast.makeText(context, "Informasi Program Poin Kantin SMKN 8", Toast.LENGTH_SHORT).show() }) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.HelpOutline, contentDescription = "Help", tint = TextSecondary, modifier = Modifier.size(20.dp))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 2. Greeting Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "✍️", fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Halo, $studentName!",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(BlueLightBg)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = studentClass,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = BluePrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 3. Points Summary Banner Card (Kantin Points Siswa)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF1D4ED8),
                                Color(0xFF2563EB),
                                Color(0xFF9333EA)
                            )
                        )
                    )
                    .padding(18.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🪙", fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Kantin Points Siswa",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White.copy(alpha = 0.2f))
                                .clickable { Toast.makeText(context, "Dapatkan +1 Poin tiap kelipatan Rp 10.000", Toast.LENGTH_SHORT).show() }
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(text = "Cara Kumpul ℹ️", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "$currentPoints",
                                fontSize = 36.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Poin",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White.copy(alpha = 0.9f),
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0xFFD97706))
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = "= Setara Rp 12.500",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Progress Bar to Hadiah Utama
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Target Hadiah Utama 20 / 20 Poin",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.White)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "🎉 Siap Ditukar!",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFD97706)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    LinearProgressIndicator(
                        progress = { 1.0f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = Color(0xFFFBBF24),
                        trackColor = Color.White.copy(alpha = 0.3f)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🎖️", fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Dapatkan +1 Poin tiap transaksi Rp 10.000 di seluruh stand kantin.",
                            fontSize = 10.sp,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // NEW: Voucher Saya Section
            if (myVouchers.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "Voucher Saya", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = BluePrimary)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "🎫", fontSize = 14.sp)
                        }
                        Text(text = "Siap digunakan saat jajan berikutnya", fontSize = 11.sp, color = TextSecondary)
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFDCFCE7))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(text = "${myVouchers.size} Tersedia", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF16A34A))
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    myVouchers.forEach { voucher ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7)),
                            border = BorderStroke(1.dp, Color(0xFFFDE047))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                    Box(
                                        modifier = Modifier.size(44.dp).clip(CircleShape).background(Color.White),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = voucher.foodEmoji, fontSize = 22.sp)
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(text = voucher.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                        Text(text = voucher.standName, fontSize = 11.sp, color = TextSecondary)
                                    }
                                }
                                Button(
                                    onClick = { Toast.makeText(context, "Membuka QR Code Klaim...", Toast.LENGTH_SHORT).show() },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                    modifier = Modifier.height(34.dp)
                                ) {
                                    Text(text = "Pakai", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(20.dp))
            }

            // 4. Tukar Hadiah Utama Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Tukar Hadiah Utama", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "🎁", fontSize = 14.sp)
                    }
                    Text(text = "Paling banyak ditukarkan oleh siswa minggu ini", fontSize = 11.sp, color = TextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Featured Reward Card Component
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, BorderColor)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    // Food Image Thumbnail Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFFEF3C7)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🍱", fontSize = 64.sp)

                        // Top Badges
                        Row(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFFD97706))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(text = "⭐ Favorit Siswa", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color.White.copy(alpha = 0.9f))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(text = "Stand A, B, C", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            }
                        }

                        // Points Tag Overlay
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(10.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFFD97706))
                                .padding(horizontal = 12.dp, vertical = 5.dp)
                        ) {
                            Text(text = "✪ 20 P", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(text = "VOUCHER MAKAN SIANG", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = TextMuted, letterSpacing = 0.5.sp)
                    Text(text = "1 Porsi Makan Gratis", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "🍱 Maks. subsidi senilai Rp 10.000", fontSize = 11.sp, color = TextSecondary)
                        Text(text = "⏱️ Istirahat ke-1 & 2", fontSize = 11.sp, color = TextSecondary)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        ShieldCheckIcon(size = 12.dp, tint = BluePrimary)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Tunjukkan kode QR klaim ke petugas kasir stand sebelum jam istirahat berakhir.",
                            fontSize = 10.sp,
                            color = TextMuted
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Big Action Claim Button
                    Button(
                        onClick = {
                            myVouchers.add(RewardItem("v0", "1 Porsi Makan Siang Gratis", "Kantin SMKN 8", 20, "Voucher", "🍱"))
                            Toast.makeText(context, "Voucher 1 Porsi Makan Gratis Berhasil Ditambahkan ke Voucher Saya!", Toast.LENGTH_LONG).show()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFD97706),
                            contentColor = Color.White
                        )
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.CardGiftcard, contentDescription = "Gift", modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Tukarkan Sekarang (20 Poin)", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 5. Pilihan Hadiah Lainnya Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "Pilihan Hadiah Lainnya", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text(text = "Jajan hemat mulai dari 2 poin saja", fontSize = 11.sp, color = TextSecondary)
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(BlueLightBg)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(text = "4 Pilihan", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = BluePrimary)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 2x2 Grid Rewards
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Card 1
                    RewardCard(
                        item = otherRewards[0],
                        modifier = Modifier.weight(1f),
                        onClaimClick = { 
                            myVouchers.add(otherRewards[0])
                            Toast.makeText(context, "Klaim Es Teh Manis Jumbo (5 Poin) Berhasil!", Toast.LENGTH_SHORT).show() 
                        }
                    )
                    // Card 2
                    RewardCard(
                        item = otherRewards[1],
                        modifier = Modifier.weight(1f),
                        onClaimClick = { 
                            myVouchers.add(otherRewards[1])
                            Toast.makeText(context, "Klaim 2 Pcs Gorengan (3 Poin) Berhasil!", Toast.LENGTH_SHORT).show() 
                        }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Card 3
                    RewardCard(
                        item = otherRewards[2],
                        modifier = Modifier.weight(1f),
                        onClaimClick = { 
                            myVouchers.add(otherRewards[2])
                            Toast.makeText(context, "Klaim Voucher Diskon Rp 5.000 Berhasil!", Toast.LENGTH_SHORT).show() 
                        }
                    )
                    // Card 4
                    RewardCard(
                        item = otherRewards[3],
                        modifier = Modifier.weight(1f),
                        onClaimClick = { 
                            myVouchers.add(otherRewards[3])
                            Toast.makeText(context, "Klaim Ekstra Keju/Sambal Berhasil!", Toast.LENGTH_SHORT).show() 
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // 6. Riwayat Perolehan Poin Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Riwayat Perolehan Poin", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text(
                    text = "Lihat Semua",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = BluePrimary,
                    modifier = Modifier.clickable { Toast.makeText(context, "Membuka seluruh riwayat poin", Toast.LENGTH_SHORT).show() }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // History List
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                pointHistory.forEach { h ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, BorderColor)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(InputBg),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = h.emoji, fontSize = 18.sp)
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Column {
                                    Text(text = h.title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary, maxLines = 1)
                                    Text(text = h.date, fontSize = 10.sp, color = TextMuted)
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (h.pointsChange > 0) BlueLightBg else Color(0xFFFEE2E2))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = if (h.pointsChange > 0) "+${h.pointsChange} POIN" else "${h.pointsChange} POIN",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (h.pointsChange > 0) BluePrimary else Color(0xFFDC2626)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun RewardCard(
    item: RewardItem,
    modifier: Modifier = Modifier,
    onClaimClick: () -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, BorderColor)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFFEF3C7)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = item.foodEmoji, fontSize = 36.sp)

                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(4.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White.copy(alpha = 0.9f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(text = item.categoryTag, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(4.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFD97706))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(text = "${item.pointsCost} Poin", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = item.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary, maxLines = 1)
            Text(text = item.standName, fontSize = 10.sp, color = TextMuted, maxLines = 1)

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(BlueLightBg)
                    .clickable { onClaimClick() }
                    .padding(vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "✪ Tukar ${item.pointsCost} P", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BluePrimary)
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PointsRewardScreenPreview() {
    EightCanteenTheme {
        PointsRewardScreen()
    }
}
