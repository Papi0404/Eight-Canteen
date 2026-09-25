package com.januarzidanetinendeng.eightcanteen.ui.seller

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.januarzidanetinendeng.eightcanteen.ui.components.EKantinLogoIcon
import com.januarzidanetinendeng.eightcanteen.ui.components.ShieldCheckIcon
import com.januarzidanetinendeng.eightcanteen.ui.theme.BlueChipBg
import com.januarzidanetinendeng.eightcanteen.ui.theme.BlueLightBg
import com.januarzidanetinendeng.eightcanteen.ui.theme.BluePrimary
import com.januarzidanetinendeng.eightcanteen.ui.theme.BorderColor
import com.januarzidanetinendeng.eightcanteen.ui.theme.EightCanteenTheme
import com.januarzidanetinendeng.eightcanteen.ui.theme.InputBg
import com.januarzidanetinendeng.eightcanteen.ui.theme.ScreenBg
import com.januarzidanetinendeng.eightcanteen.ui.theme.TextMuted
import com.januarzidanetinendeng.eightcanteen.ui.theme.TextPrimary
import com.januarzidanetinendeng.eightcanteen.ui.theme.TextSecondary
import java.util.Locale

data class SellerMenuItem(
    val id: String,
    val name: String,
    val price: Int,
    var stock: Int,
    var isAvailable: Boolean,
    val foodEmoji: String
)

@Composable
fun SellerDashboardScreen(
    standName: String = "Kebab Bang Ali",
    counterSlot: String = "Stand 04",
    todayIncome: Int = 150000,
    completedOrders: Int = 18,
    activeQueueCount: Int = 12,
    readyCount: Int = 4,
    cookingCount: Int = 8,
    averagePrepMinutes: Int = 7,
    onScanQrClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var isStoreOpen by remember { mutableStateOf(true) }
    var showNewOrderAlert by remember { mutableStateOf(true) }
    var selectedNavTab by remember { mutableIntStateOf(0) } // Stand tab active (beranda)

    // Managed stock list state
    var menuList by remember {
        mutableStateOf(
            listOf(
                SellerMenuItem("1", "Kebab Beef Jumbo", 15000, 4, true, "🥙"),
                SellerMenuItem("2", "Kebab Ayam Crispy", 12000, 0, false, "🍗"),
                SellerMenuItem("3", "Kebab Sosis BBQ", 10000, 15, true, "🌭"),
                SellerMenuItem("4", "Roti Maryam Coklat", 8000, 9, true, "🫓")
            )
        )
    }

    Scaffold(
        containerColor = ScreenBg,
        topBar = {
            // Top Bar Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left Brand Title
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
                            text = "SELLER",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextMuted,
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                // Right Action Icons
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { Toast.makeText(context, "Pencarian Menu Stand", Toast.LENGTH_SHORT).show() }) {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = TextPrimary)
                    }

                    // Notification Bell with Active Indicator Dot
                    Box {
                        IconButton(onClick = { Toast.makeText(context, "Pesanan Baru #A-145 Masuk!", Toast.LENGTH_SHORT).show() }) {
                            Icon(imageVector = Icons.Default.Notifications, contentDescription = "Notify", tint = TextPrimary)
                        }
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(top = 10.dp, end = 10.dp)
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFEA580C))
                        )
                    }

                    // Logout Action
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFEE2E2))
                            .clickable { onLogoutClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.Logout, contentDescription = "Logout", tint = Color(0xFFDC2626), modifier = Modifier.size(18.dp))
                    }
                }
            }
        },
        bottomBar = {
            // Bottom Navigation Bar (Seller Exclusive)
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = selectedNavTab == 0,
                    onClick = {
                        selectedNavTab = 0
                    },
                    icon = { Icon(imageVector = Icons.Default.Storefront, contentDescription = "Beranda") },
                    label = { Text("Beranda", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = BluePrimary, selectedTextColor = BluePrimary, indicatorColor = BlueLightBg)
                )
                NavigationBarItem(
                    selected = selectedNavTab == 1,
                    onClick = {
                        selectedNavTab = 1
                        Toast.makeText(context, "Membuka Daftar Pesanan Masuk...", Toast.LENGTH_SHORT).show()
                    },
                    icon = { Icon(imageVector = Icons.AutoMirrored.Filled.ReceiptLong, contentDescription = "Pesanan") },
                    label = { Text("Pesanan", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = BluePrimary, selectedTextColor = BluePrimary, indicatorColor = BlueLightBg)
                )
                NavigationBarItem(
                    selected = selectedNavTab == 2,
                    onClick = { selectedNavTab = 2 },
                    icon = { Icon(imageVector = Icons.Default.ShoppingBag, contentDescription = "Menu") },
                    label = { Text("Manajemen Menu", fontSize = 11.sp) },
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
            // 1. New Incoming Order Alert Banner
            AnimatedVisibility(
                visible = showNewOrderAlert,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFF97316))
                        .padding(horizontal = 14.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = "🔔", fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Pesanan Baru #A-145",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "1x Kebab Beef Jumbo Keju (Istirahat 1)",
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            }
                        }

                        IconButton(
                            onClick = { showNewOrderAlert = false },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Dismiss",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            if (showNewOrderAlert) Spacer(modifier = Modifier.height(14.dp))

            // 2. Stand Info & Open/Close Toggle Header Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, BorderColor)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFEF3C7))
                                    .border(1.5.dp, BluePrimary, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "👨‍🍳", fontSize = 32.sp)
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = standName,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )

                                Spacer(modifier = Modifier.height(3.dp))

                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(BlueLightBg)
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text(text = counterSlot, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = BluePrimary)
                                    }

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFFFEF3C7))
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            ShieldCheckIcon(size = 10.dp, tint = Color(0xFFB45309))
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Text(text = "Koperasi", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB45309))
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Open/Closed Switch Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(InputBg)
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(if (isStoreOpen) Color(0xFF16A34A) else TextMuted)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = if (isStoreOpen) "Toko Buka" else "Toko Tutup",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = if (isStoreOpen) "Menerima pesanan pre-order" else "Tidak menerima pesanan baru",
                                    fontSize = 10.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        Switch(
                            checked = isStoreOpen,
                            onCheckedChange = { isStoreOpen = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = BluePrimary,
                                uncheckedThumbColor = TextMuted,
                                uncheckedTrackColor = BorderColor
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 3. SCAN QR SISWA Banner Card Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(6.dp, RoundedCornerShape(20.dp), ambientColor = Color(0x332563EB))
                    .clip(RoundedCornerShape(20.dp))
                    .background(BluePrimary)
                    .clickable {
                        onScanQrClick()
                        Toast.makeText(context, "Membuka Kamera QR Scanner Siswa...", Toast.LENGTH_SHORT).show()
                    }
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.QrCodeScanner,
                                contentDescription = "Scan QR",
                                tint = Color.White,
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "SCAN QR SISWA",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "Verifikasi instan pengambilan pesanan",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Scan",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 4. Revenue Today Card (Pendapatan Hari Ini)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, BorderColor)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "PENDAPATAN HARI INI",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextMuted,
                            letterSpacing = 0.5.sp
                        )

                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(BlueLightBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CreditCard,
                                contentDescription = "Report",
                                tint = BluePrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Rp ${String.format(Locale.GERMANY, "%,d", todayIncome)}",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextPrimary
                        )

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFDCFCE7))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "📈 +24%",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF16A34A)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "$completedOrders pesanan selesai disajikan",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 5. Order Queue & Speed Stats Grid (2 Cards Row)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Card 1: Antre Aktif
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, BorderColor)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "ANTRE AKTIF", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = TextMuted)
                            Icon(imageVector = Icons.Default.ShoppingBag, contentDescription = "Queue", tint = BluePrimary, modifier = Modifier.size(16.dp))
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(text = "$activeQueueCount Porsi", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = TextPrimary)

                        Spacer(modifier = Modifier.height(8.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFDBEAFE))
                                    .padding(vertical = 3.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "$readyCount Siap Ambil", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = BluePrimary)
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFFFEDD5))
                                    .padding(vertical = 3.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "$cookingCount Dimasak", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFC2410C))
                            }
                        }
                    }
                }

                // Card 2: Kecepatan
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, BorderColor)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "KECEPATAN", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = TextMuted)
                            Icon(imageVector = Icons.Default.Timer, contentDescription = "Speed", tint = Color(0xFFEA580C), modifier = Modifier.size(16.dp))
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(text = "$averagePrepMinutes Menit", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = TextPrimary)

                        Spacer(modifier = Modifier.height(8.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFDCFCE7))
                                .padding(vertical = 3.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "⚡ Optimal", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF16A34A))
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Rata-rata waktu saji",
                            fontSize = 10.sp,
                            color = TextMuted,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 6. Manajemen Stok Menu Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "Manajemen Stok Menu", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text(text = "Update ketersediaan real-time", fontSize = 11.sp, color = TextSecondary)
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(BlueLightBg)
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(text = "${menuList.count { it.isAvailable }} Menu Aktif", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BluePrimary)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Stock Items List
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                menuList.forEachIndexed { index, menu ->
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
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                // Food Thumbnail
                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (menu.isAvailable) Color(0xFFFEF3C7) else Color(0xFFE2E8F0)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = menu.foodEmoji, fontSize = 28.sp)

                                    if (!menu.isAvailable) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(Color.Black.copy(alpha = 0.4f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(text = "HABIS", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Text(
                                        text = menu.name,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (menu.isAvailable) TextPrimary else TextMuted
                                    )
                                    Text(
                                        text = "Rp ${String.format(Locale.GERMANY, "%,d", menu.price)}",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (menu.isAvailable) BluePrimary else TextMuted
                                    )

                                    Spacer(modifier = Modifier.height(2.dp))

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .clip(CircleShape)
                                                .background(if (menu.isAvailable) Color(0xFF16A34A) else Color(0xFFDC2626))
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (menu.isAvailable) "Tersedia" else "Stok Kosong",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (menu.isAvailable) Color(0xFF16A34A) else Color(0xFFDC2626)
                                        )
                                    }
                                }
                            }

                            // Quantity Increment / Decrement & Toggle
                            Column(horizontalAlignment = Alignment.End) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(InputBg)
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                ) {
                                    IconButton(
                                        onClick = {
                                            if (menu.stock > 0) {
                                                val updated = menuList.toMutableList()
                                                updated[index] = menu.copy(stock = menu.stock - 1, isAvailable = menu.stock - 1 > 0)
                                                menuList = updated
                                            }
                                        },
                                        modifier = Modifier.size(26.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.Remove, contentDescription = "Decrease", tint = TextPrimary, modifier = Modifier.size(14.dp))
                                    }

                                    Text(
                                        text = "${menu.stock}",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary,
                                        modifier = Modifier.padding(horizontal = 8.dp)
                                    )

                                    IconButton(
                                        onClick = {
                                            val updated = menuList.toMutableList()
                                            updated[index] = menu.copy(stock = menu.stock + 1, isAvailable = true)
                                            menuList = updated
                                        },
                                        modifier = Modifier.size(26.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.Add, contentDescription = "Increase", tint = TextPrimary, modifier = Modifier.size(14.dp))
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Switch(
                                    checked = menu.isAvailable,
                                    onCheckedChange = { checked ->
                                        val updated = menuList.toMutableList()
                                        updated[index] = menu.copy(isAvailable = checked, stock = if (checked && menu.stock == 0) 5 else menu.stock)
                                        menuList = updated
                                    },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = BluePrimary,
                                        uncheckedThumbColor = TextMuted,
                                        uncheckedTrackColor = BorderColor
                                    )
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 7. Save Stock Changes Button
            Button(
                onClick = {
                    Toast.makeText(context, "Perubahan Stok Berhasil Disimpan ke Sistem Koperasi!", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BlueLightBg,
                    contentColor = BluePrimary
                ),
                border = BorderStroke(1.dp, BlueChipBg)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Save, contentDescription = "Save", modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Simpan Perubahan Stok", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SellerDashboardScreenPreview() {
    EightCanteenTheme {
        SellerDashboardScreen()
    }
}
