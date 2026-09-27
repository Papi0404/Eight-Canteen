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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.januarzidanetinendeng.eightcanteen.data.local.SessionManager
import com.januarzidanetinendeng.eightcanteen.data.repository.CanteenRepository
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
import kotlinx.coroutines.launch
import java.util.Locale

data class SellerMenuItem(
    val id: String,
    val name: String,
    val price: Int,
    var stock: Int,
    var isAvailable: Boolean,
    val foodEmoji: String
)

fun determineFoodEmoji(name: String): String {
    val lower = name.lowercase()
    return when {
        lower.contains("jus") || lower.contains("juice") || lower.contains("alpukat") || lower.contains("mangga") -> "🧃"
        lower.contains("kebab") -> "🥙"
        lower.contains("ayam") || lower.contains("crispy") || lower.contains("geprek") -> "🍗"
        lower.contains("sosis") -> "🌭"
        lower.contains("roti") || lower.contains("maryam") -> "🫓"
        lower.contains("kopi") || lower.contains("teh") || lower.contains("boba") -> "🧋"
        lower.contains("mie") || lower.contains("bakso") || lower.contains("ramen") -> "🍜"
        lower.contains("nasi") || lower.contains("rice") -> "🍚"
        lower.contains("burger") -> "🍔"
        lower.contains("pizza") -> "🍕"
        lower.contains("es") -> "🍧"
        lower.contains("buah") -> "🍎"
        else -> "🍱"
    }
}

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
    onStandUpdated: (String, String) -> Unit = { _, _ -> },
    onScanQrClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    var currentStandName by remember { mutableStateOf(standName) }
    var currentCounterSlot by remember { mutableStateOf(counterSlot) }
    var isStoreOpen by remember { mutableStateOf(true) }
    var showNewOrderAlert by remember { mutableStateOf(true) }
    var selectedNavTab by remember { mutableIntStateOf(0) }

    var currentTodayIncome by remember { mutableIntStateOf(todayIncome) }
    var currentCompletedOrders by remember { mutableIntStateOf(completedOrders) }
    var currentActiveQueueCount by remember { mutableIntStateOf(activeQueueCount) }
    var currentReadyCount by remember { mutableIntStateOf(readyCount) }
    var currentCookingCount by remember { mutableIntStateOf(cookingCount) }

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

    // CRUD Dialog States
    var showEditStandDialog by remember { mutableStateOf(false) }
    var editStandNameInput by remember { mutableStateOf("") }
    var editCounterSlotInput by remember { mutableStateOf("") }
    var isSavingStand by remember { mutableStateOf(false) }

    var showAddMenuDialog by remember { mutableStateOf(false) }
    var newMenuName by remember { mutableStateOf("") }
    var newMenuPrice by remember { mutableStateOf("") }
    var newMenuStock by remember { mutableStateOf("10") }
    var isSavingNewMenu by remember { mutableStateOf(false) }

    var editingMenuItem by remember { mutableStateOf<SellerMenuItem?>(null) }
    var editMenuName by remember { mutableStateOf("") }
    var editMenuPrice by remember { mutableStateOf("") }
    var editMenuStock by remember { mutableStateOf("") }
    var isSavingEditMenu by remember { mutableStateOf(false) }

    var deletingMenuItem by remember { mutableStateOf<SellerMenuItem?>(null) }
    var isDeletingMenu by remember { mutableStateOf(false) }

    val repository = remember { CanteenRepository() }

    LaunchedEffect(Unit) {
        val session = SessionManager.getInstance(context)
        val sellerStandId = session.getStandId()

        // 1. Fetch Stand info directly from Supabase via backend /stands/me
        repository.getMyStand().onSuccess { res ->
            res.data?.let { stand ->
                val name = stand.name ?: currentStandName
                val slot = stand.counterSlot ?: stand.standNumber ?: currentCounterSlot
                currentStandName = name
                currentCounterSlot = slot
                stand.isOpen?.let { isStoreOpen = it }
                session.saveStand(stand.id, name, slot)
                onStandUpdated(name, slot)
            }
        }

        // 2. Fetch Stand Menus from /menus/me (returns all seller menus)
        val menusResult = repository.getMyMenus()
        menusResult.onSuccess { res ->
            res.data?.takeIf { it.isNotEmpty() }?.let { apiMenus ->
                menuList = apiMenus.map { menu ->
                    SellerMenuItem(
                        id = menu.id,
                        name = menu.name,
                        price = menu.price,
                        stock = menu.stock,
                        isAvailable = menu.isAvailable,
                        foodEmoji = determineFoodEmoji(menu.name)
                    )
                }
            }
        }.onFailure {
            // Fallback via standId if /menus/me is unavailable
            if (!sellerStandId.isNullOrBlank()) {
                repository.getMenusByStand(sellerStandId).onSuccess { res ->
                    res.data?.takeIf { it.isNotEmpty() }?.let { apiMenus ->
                        menuList = apiMenus.map { menu ->
                            SellerMenuItem(
                                id = menu.id,
                                name = menu.name,
                                price = menu.price,
                                stock = menu.stock,
                                isAvailable = menu.isAvailable && menu.stock > 0,
                                foodEmoji = determineFoodEmoji(menu.name)
                            )
                        }
                    }
                }
            }
        }

        // 3. Fetch Orders for stats
        repository.getOrders().onSuccess { res ->
            res.data?.let { orders ->
                if (orders.isNotEmpty()) {
                    val completed = orders.filter { it.status == "COMPLETED" }
                    val ready = orders.filter { it.status == "READY" }
                    val cooking = orders.filter { it.status == "COOKING" || it.status == "PENDING" }
                    val totalIncome = completed.sumOf { it.totalAmount ?: 0 }

                    if (completed.isNotEmpty()) {
                        currentCompletedOrders = completed.size
                        currentTodayIncome = totalIncome
                    }
                    currentReadyCount = ready.size
                    currentCookingCount = cooking.size
                    currentActiveQueueCount = ready.size + cooking.size
                }
            }
        }
    }

    Scaffold(
        containerColor = ScreenBg,
        topBar = {
            // Top Bar Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
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
                            text = "SELLER PANEL",
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
                        IconButton(onClick = { Toast.makeText(context, "Pesanan Baru Masuk!", Toast.LENGTH_SHORT).show() }) {
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
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Logout",
                            tint = Color(0xFFDC2626),
                            modifier = Modifier.size(18.dp)
                        )
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
                    onClick = {
                        selectedNavTab = 2
                        newMenuName = ""
                        newMenuPrice = ""
                        newMenuStock = "10"
                        showAddMenuDialog = true
                    },
                    icon = { Icon(imageVector = Icons.Default.ShoppingBag, contentDescription = "Tambah Menu") },
                    label = { Text("Tambah Menu", fontSize = 11.sp) },
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
                                    text = "Pesanan Baru Masuk",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Periksa antrean untuk menyajikan pesanan tepat waktu",
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

            // 2. Stand Info & Open/Close Toggle Header Card + EDIT STAND BUTTON
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
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
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
                                    text = currentStandName,
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
                                        Text(text = currentCounterSlot, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = BluePrimary)
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
                                            Text(text = "Penjual Terverifikasi", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB45309))
                                        }
                                    }
                                }
                            }
                        }

                        // Edit Stand Button
                        IconButton(
                            onClick = {
                                editStandNameInput = currentStandName
                                editCounterSlotInput = currentCounterSlot
                                showEditStandDialog = true
                            },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(BlueLightBg)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Stand",
                                tint = BluePrimary,
                                modifier = Modifier.size(18.dp)
                            )
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
                                    text = if (isStoreOpen) "Menerima pesanan siswa" else "Tidak menerima pesanan baru",
                                    fontSize = 10.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        Switch(
                            checked = isStoreOpen,
                            onCheckedChange = { checked ->
                                isStoreOpen = checked
                                coroutineScope.launch {
                                    repository.updateMyStand(isOpen = checked)
                                    Toast.makeText(context, if (checked) "Stand dibuka untuk pesanan" else "Stand ditutup sementara", Toast.LENGTH_SHORT).show()
                                }
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
                                text = "SCAN QR PESANAN SISWA",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "Verifikasi instan pengambilan makanan siswa",
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
                            text = "PENDAPATAN STAND HARI INI",
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
                            text = "Rp ${String.format(Locale.GERMANY, "%,d", currentTodayIncome)}",
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
                        text = "$currentCompletedOrders pesanan selesai disajikan",
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

                        Text(text = "$currentActiveQueueCount Porsi", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = TextPrimary)

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
                                Text(text = "$currentReadyCount Siap Ambil", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = BluePrimary)
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFFFEDD5))
                                    .padding(vertical = 3.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "$currentCookingCount Dimasak", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFC2410C))
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

            // 6. Manajemen Menu & Stok Section Header with "+ Tambah Menu" Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Kelola Menu & Stok", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text(text = "Tambah, ubah nama, harga & stok menu", fontSize = 11.sp, color = TextSecondary)
                }

                Button(
                    onClick = {
                        newMenuName = ""
                        newMenuPrice = ""
                        newMenuStock = "10"
                        showAddMenuDialog = true
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BluePrimary,
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Tambah Menu", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "+ Tambah Menu", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Stock Items List
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                if (menuList.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, BorderColor)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "🍽️", fontSize = 36.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "Belum Ada Menu Stand", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "Klik '+ Tambah Menu' untuk mulai menambahkan menu makanan/minuman stand Anda.", fontSize = 12.sp, color = TextSecondary, textAlign = TextAlign.Center)
                        }
                    }
                }

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

                                Spacer(modifier = Modifier.width(10.dp))

                                Column(modifier = Modifier.weight(1f)) {
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
                                            text = if (menu.isAvailable) "Tersedia (${menu.stock} porsi)" else "Stok Kosong",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (menu.isAvailable) Color(0xFF16A34A) else Color(0xFFDC2626)
                                        )
                                    }
                                }
                            }

                            // Actions: Edit, Delete, Stock Controls & Toggle
                            Column(horizontalAlignment = Alignment.End) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    // Edit Menu Button
                                    IconButton(
                                        onClick = {
                                            editingMenuItem = menu
                                            editMenuName = menu.name
                                            editMenuPrice = menu.price.toString()
                                            editMenuStock = menu.stock.toString()
                                        },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = "Edit Menu",
                                            tint = BluePrimary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }

                                    // Delete Menu Button
                                    IconButton(
                                        onClick = {
                                            deletingMenuItem = menu
                                        },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.DeleteOutline,
                                            contentDescription = "Hapus Menu",
                                            tint = Color(0xFFDC2626),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(2.dp))

                                // Quantity Increment / Decrement
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
                                                val newStock = menu.stock - 1
                                                updated[index] = menu.copy(stock = newStock, isAvailable = newStock > 0)
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
                                        modifier = Modifier.padding(horizontal = 6.dp)
                                    )

                                    IconButton(
                                        onClick = {
                                            val updated = menuList.toMutableList()
                                            val newStock = menu.stock + 1
                                            updated[index] = menu.copy(stock = newStock, isAvailable = true)
                                            menuList = updated
                                        },
                                        modifier = Modifier.size(26.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.Add, contentDescription = "Increase", tint = TextPrimary, modifier = Modifier.size(14.dp))
                                    }
                                }

                                Spacer(modifier = Modifier.height(2.dp))

                                Switch(
                                    checked = menu.isAvailable,
                                    onCheckedChange = { checked ->
                                        val updated = menuList.toMutableList()
                                        val updatedStock = if (checked && menu.stock == 0) 5 else menu.stock
                                        updated[index] = menu.copy(isAvailable = checked, stock = updatedStock)
                                        menuList = updated
                                        coroutineScope.launch {
                                            repository.updateMenu(menu.id, isAvailable = checked, stock = updatedStock)
                                        }
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

            // 7. Save Stock Changes Button (Persists all modified stocks to Supabase)
            Button(
                onClick = {
                    coroutineScope.launch {
                        var updateCount = 0
                        menuList.forEach { item ->
                            repository.updateMenu(item.id, stock = item.stock, isAvailable = item.isAvailable).onSuccess {
                                updateCount++
                            }
                        }
                        Toast.makeText(context, "Perubahan Stok Berhasil Disimpan ($updateCount menu terverifikasi)!", Toast.LENGTH_SHORT).show()
                    }
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
                    Text(text = "Simpan Perubahan Stok ke Database", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // ==========================================
    // DIALOG 1: EDIT NAMA & NOMOR STAND
    // ==========================================
    if (showEditStandDialog) {
        AlertDialog(
            onDismissRequest = { if (!isSavingStand) showEditStandDialog = false },
            title = {
                Text(
                    text = "Edit Profil Stand",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Perbarui nama stand kantin dan slot loket penjualan Anda:",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )

                    OutlinedTextField(
                        value = editStandNameInput,
                        onValueChange = { editStandNameInput = it },
                        label = { Text("Nama Stand Kantin") },
                        placeholder = { Text("Contoh: Jus Buah Segar Bang Ijul") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = InputBg,
                            unfocusedContainerColor = InputBg,
                            focusedBorderColor = BluePrimary,
                            unfocusedBorderColor = BorderColor
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = editCounterSlotInput,
                        onValueChange = { editCounterSlotInput = it },
                        label = { Text("Nomor / Lokasi Stand") },
                        placeholder = { Text("Contoh: Stand 02") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = InputBg,
                            unfocusedContainerColor = InputBg,
                            focusedBorderColor = BluePrimary,
                            unfocusedBorderColor = BorderColor
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editStandNameInput.isBlank()) {
                            Toast.makeText(context, "Nama stand tidak boleh kosong", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        coroutineScope.launch {
                            isSavingStand = true
                            val result = repository.updateMyStand(
                                name = editStandNameInput.trim(),
                                counterSlot = editCounterSlotInput.trim()
                            )
                            isSavingStand = false
                            result.onSuccess { res ->
                                val updatedName = res.data?.name ?: editStandNameInput.trim()
                                val updatedSlot = res.data?.counterSlot ?: res.data?.standNumber ?: editCounterSlotInput.trim()
                                currentStandName = updatedName
                                currentCounterSlot = updatedSlot
                                val session = SessionManager.getInstance(context)
                                session.saveStand(session.getStandId(), updatedName, updatedSlot)
                                onStandUpdated(updatedName, updatedSlot)
                                showEditStandDialog = false
                                Toast.makeText(context, "Nama & Lokasi Stand Berhasil Disimpan!", Toast.LENGTH_SHORT).show()
                            }.onFailure {
                                currentStandName = editStandNameInput.trim()
                                currentCounterSlot = editCounterSlotInput.trim()
                                onStandUpdated(currentStandName, currentCounterSlot)
                                showEditStandDialog = false
                                Toast.makeText(context, "Profil Stand Diperbarui Secara Lokal", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isSavingStand
                ) {
                    if (isSavingStand) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                    } else {
                        Text("Simpan Perubahan")
                    }
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showEditStandDialog = false },
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isSavingStand
                ) {
                    Text("Batal", color = TextPrimary)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp)
        )
    }

    // ==========================================
    // DIALOG 2: TAMBAH MENU BARU
    // ==========================================
    if (showAddMenuDialog) {
        AlertDialog(
            onDismissRequest = { if (!isSavingNewMenu) showAddMenuDialog = false },
            title = {
                Text(
                    text = "Tambah Menu Baru",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Masukkan detail menu makanan/minuman yang akan dijual:",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )

                    OutlinedTextField(
                        value = newMenuName,
                        onValueChange = { newMenuName = it },
                        label = { Text("Nama Menu") },
                        placeholder = { Text("Contoh: Jus Alpukat Spesial") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = InputBg,
                            unfocusedContainerColor = InputBg,
                            focusedBorderColor = BluePrimary,
                            unfocusedBorderColor = BorderColor
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = newMenuPrice,
                        onValueChange = { newMenuPrice = it.filter { c -> c.isDigit() } },
                        label = { Text("Harga (Rp)") },
                        placeholder = { Text("Contoh: 12000") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = InputBg,
                            unfocusedContainerColor = InputBg,
                            focusedBorderColor = BluePrimary,
                            unfocusedBorderColor = BorderColor
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = newMenuStock,
                        onValueChange = { newMenuStock = it.filter { c -> c.isDigit() } },
                        label = { Text("Stok Awal") },
                        placeholder = { Text("Contoh: 15") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = InputBg,
                            unfocusedContainerColor = InputBg,
                            focusedBorderColor = BluePrimary,
                            unfocusedBorderColor = BorderColor
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newMenuName.isBlank()) {
                            Toast.makeText(context, "Nama menu tidak boleh kosong", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        val priceInt = newMenuPrice.toIntOrNull() ?: 10000
                        val stockInt = newMenuStock.toIntOrNull() ?: 10

                        coroutineScope.launch {
                            isSavingNewMenu = true
                            val result = repository.createMenu(
                                standId = null,
                                name = newMenuName.trim(),
                                price = priceInt,
                                stock = stockInt
                            )
                            isSavingNewMenu = false
                            result.onSuccess { res ->
                                val created = res.data
                                val newItem = SellerMenuItem(
                                    id = created?.id ?: System.currentTimeMillis().toString(),
                                    name = created?.name ?: newMenuName.trim(),
                                    price = created?.price ?: priceInt,
                                    stock = created?.stock ?: stockInt,
                                    isAvailable = (created?.stock ?: stockInt) > 0,
                                    foodEmoji = determineFoodEmoji(created?.name ?: newMenuName.trim())
                                )
                                menuList = listOf(newItem) + menuList
                                showAddMenuDialog = false
                                Toast.makeText(context, "Menu '${newItem.name}' Berhasil Ditambahkan!", Toast.LENGTH_SHORT).show()
                            }.onFailure { e ->
                                val newItem = SellerMenuItem(
                                    id = System.currentTimeMillis().toString(),
                                    name = newMenuName.trim(),
                                    price = priceInt,
                                    stock = stockInt,
                                    isAvailable = stockInt > 0,
                                    foodEmoji = determineFoodEmoji(newMenuName.trim())
                                )
                                menuList = listOf(newItem) + menuList
                                showAddMenuDialog = false
                                Toast.makeText(context, "Menu Ditambahkan ke Daftar Stand!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isSavingNewMenu
                ) {
                    if (isSavingNewMenu) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                    } else {
                        Text("Tambah Menu")
                    }
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showAddMenuDialog = false },
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isSavingNewMenu
                ) {
                    Text("Batal", color = TextPrimary)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp)
        )
    }

    // ==========================================
    // DIALOG 3: EDIT MENU (NAMA, HARGA, STOK)
    // ==========================================
    editingMenuItem?.let { item ->
        AlertDialog(
            onDismissRequest = { if (!isSavingEditMenu) editingMenuItem = null },
            title = {
                Text(
                    text = "Edit Detail Menu",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Ubah nama, harga, atau stok menu:",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )

                    OutlinedTextField(
                        value = editMenuName,
                        onValueChange = { editMenuName = it },
                        label = { Text("Nama Menu") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = InputBg,
                            unfocusedContainerColor = InputBg,
                            focusedBorderColor = BluePrimary,
                            unfocusedBorderColor = BorderColor
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = editMenuPrice,
                        onValueChange = { editMenuPrice = it.filter { c -> c.isDigit() } },
                        label = { Text("Harga (Rp)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = InputBg,
                            unfocusedContainerColor = InputBg,
                            focusedBorderColor = BluePrimary,
                            unfocusedBorderColor = BorderColor
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = editMenuStock,
                        onValueChange = { editMenuStock = it.filter { c -> c.isDigit() } },
                        label = { Text("Stok Tersedia") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = InputBg,
                            unfocusedContainerColor = InputBg,
                            focusedBorderColor = BluePrimary,
                            unfocusedBorderColor = BorderColor
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editMenuName.isBlank()) {
                            Toast.makeText(context, "Nama menu tidak boleh kosong", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        val priceInt = editMenuPrice.toIntOrNull() ?: item.price
                        val stockInt = editMenuStock.toIntOrNull() ?: item.stock

                        coroutineScope.launch {
                            isSavingEditMenu = true
                            repository.updateMenu(
                                menuId = item.id,
                                name = editMenuName.trim(),
                                price = priceInt,
                                stock = stockInt,
                                isAvailable = stockInt > 0
                            )
                            isSavingEditMenu = false

                            menuList = menuList.map { m ->
                                if (m.id == item.id) {
                                    m.copy(
                                        name = editMenuName.trim(),
                                        price = priceInt,
                                        stock = stockInt,
                                        isAvailable = stockInt > 0,
                                        foodEmoji = determineFoodEmoji(editMenuName.trim())
                                    )
                                } else m
                            }
                            editingMenuItem = null
                            Toast.makeText(context, "Menu Berhasil Diperbarui!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isSavingEditMenu
                ) {
                    if (isSavingEditMenu) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                    } else {
                        Text("Simpan")
                    }
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { editingMenuItem = null },
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isSavingEditMenu
                ) {
                    Text("Batal", color = TextPrimary)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp)
        )
    }

    // ==========================================
    // DIALOG 4: KONFIRMASI HAPUS MENU
    // ==========================================
    deletingMenuItem?.let { item ->
        AlertDialog(
            onDismissRequest = { if (!isDeletingMenu) deletingMenuItem = null },
            title = {
                Text(
                    text = "Hapus Menu?",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFDC2626)
                )
            },
            text = {
                Text(
                    text = "Apakah Anda yakin ingin menghapus '${item.name}' dari stand Anda? Tindakan ini tidak dapat dibatalkan.",
                    fontSize = 13.sp,
                    color = TextPrimary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            isDeletingMenu = true
                            repository.deleteMenu(item.id)
                            isDeletingMenu = false
                            menuList = menuList.filter { it.id != item.id }
                            deletingMenuItem = null
                            Toast.makeText(context, "Menu '${item.name}' Berhasil Dihapus", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isDeletingMenu
                ) {
                    if (isDeletingMenu) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                    } else {
                        Text("Hapus Menu", color = Color.White)
                    }
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { deletingMenuItem = null },
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isDeletingMenu
                ) {
                    Text("Batal", color = TextPrimary)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SellerDashboardScreenPreview() {
    EightCanteenTheme {
        SellerDashboardScreen()
    }
}
