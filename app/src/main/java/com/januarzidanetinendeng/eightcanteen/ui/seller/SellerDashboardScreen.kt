package com.januarzidanetinendeng.eightcanteen.ui.seller

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.januarzidanetinendeng.eightcanteen.data.remote.OrderResponse
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
    standName: String = "",
    counterSlot: String = "",
    todayIncome: Int = 0,
    completedOrders: Int = 0,
    activeQueueCount: Int = 0,
    readyCount: Int = 0,
    cookingCount: Int = 0,
    averagePrepMinutes: Int = 0,
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
    var showNewOrderAlert by remember { mutableStateOf(false) }
    var selectedNavTab by remember { mutableIntStateOf(0) } // 0: Beranda, 1: Pesanan Masuk, 2: Tambah Menu

    var currentTodayIncome by remember { mutableIntStateOf(todayIncome) }
    var currentCompletedOrders by remember { mutableIntStateOf(completedOrders) }
    var currentActiveQueueCount by remember { mutableIntStateOf(activeQueueCount) }
    var currentReadyCount by remember { mutableIntStateOf(readyCount) }
    var currentCookingCount by remember { mutableIntStateOf(cookingCount) }
    var currentAveragePrep by remember { mutableIntStateOf(averagePrepMinutes) }

    // Managed dynamic stock list state (100% dinamis dari API / database)
    var menuList by remember { mutableStateOf<List<SellerMenuItem>>(emptyList()) }
    var isLoadingMenus by remember { mutableStateOf(true) }
    var menuErrorMessage by remember { mutableStateOf<String?>(null) }

    // Orders state for Seller Orders panel
    var orderList by remember { mutableStateOf<List<OrderResponse>>(emptyList()) }
    var isLoadingOrders by remember { mutableStateOf(false) }
    var selectedOrderFilter by remember { mutableStateOf("Semua") } // "Semua", "Antrean Aktif", "Siap Diambil", "Selesai"

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

    fun refreshAllSellerData() {
        coroutineScope.launch {
            val session = SessionManager.getInstance(context)
            val sellerStandId = session.getStandId()

            // 1. Fetch Stand info (/stands/me)
            repository.getMyStand().onSuccess { res ->
                res.data?.let { stand ->
                    val name = stand.name.takeIf { it.isNotBlank() } ?: currentStandName
                    val slot = (stand.counterSlot ?: stand.standNumber ?: "").takeIf { it.isNotBlank() } ?: currentCounterSlot
                    currentStandName = name
                    currentCounterSlot = slot
                    stand.isOpen?.let { isStoreOpen = it }
                    session.saveStand(stand.id, name, slot)
                    onStandUpdated(name, slot)
                }
            }

            // 2. Fetch Seller Revenue Metrics (/stands/revenue atau /seller/revenue)
            repository.getSellerRevenue().onSuccess { res ->
                res.data?.let { rev ->
                    rev.todayIncome?.let { currentTodayIncome = it }
                    rev.completedOrders?.let { currentCompletedOrders = it }
                    rev.activeQueueCount?.let { currentActiveQueueCount = it }
                    rev.readyCount?.let { currentReadyCount = it }
                    rev.cookingCount?.let { currentCookingCount = it }
                    rev.averagePrepMinutes?.let { currentAveragePrep = it }
                }
            }

            // 3. Fetch Stand Menus (/menus/me) - 100% Dinamis dari Database
            isLoadingMenus = true
            menuErrorMessage = null
            repository.getMyMenus().onSuccess { res ->
                isLoadingMenus = false
                val apiMenus = res.data ?: emptyList()
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
            }.onFailure { err ->
                // Jika getMyMenus gagal, coba fallback via getMenusByStand jika standId ada
                if (!sellerStandId.isNullOrBlank()) {
                    repository.getMenusByStand(sellerStandId).onSuccess { res ->
                        isLoadingMenus = false
                        val apiMenus = res.data ?: emptyList()
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
                    }.onFailure { err2 ->
                        isLoadingMenus = false
                        menuErrorMessage = "Gagal memuat menu dari server: ${err2.message ?: err.message ?: "Koneksi terputus"}"
                        menuList = emptyList()
                    }
                } else {
                    isLoadingMenus = false
                    menuErrorMessage = "Gagal memuat menu dari server: ${err.message ?: "Koneksi terputus"}"
                    menuList = emptyList()
                }
            }

            // 4. Fetch Orders (/orders) -> Otomatis hanya memuat pesanan yang masuk ke stand milik seller
            isLoadingOrders = true
            repository.getOrders().onSuccess { res ->
                isLoadingOrders = false
                res.data?.let { orders ->
                    orderList = orders
                    val ready = orders.filter { it.status == "READY" || it.status == "READY_FOR_PICKUP" }
                    val cooking = orders.filter { it.status == "COOKING" || it.status == "PENDING" || it.status == "PENDING_PAYMENT" }
                    val completed = orders.filter { it.status == "COMPLETED" }

                    currentReadyCount = ready.size
                    currentCookingCount = cooking.size
                    currentActiveQueueCount = ready.size + cooking.size
                    if (completed.isNotEmpty() && currentCompletedOrders == 0) {
                        currentCompletedOrders = completed.size
                        currentTodayIncome = completed.sumOf { it.totalAmount }
                    }
                    if (cooking.isNotEmpty()) {
                        showNewOrderAlert = true
                    }
                }
            }.onFailure {
                isLoadingOrders = false
            }
        }
    }

    LaunchedEffect(Unit) {
        refreshAllSellerData()
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
                    // Quick Scan QR Camera Icon Button
                    IconButton(onClick = onScanQrClick) {
                        Icon(imageVector = Icons.Default.QrCodeScanner, contentDescription = "Scan QR", tint = BluePrimary)
                    }

                    // Refresh Button
                    IconButton(onClick = {
                        refreshAllSellerData()
                        Toast.makeText(context, "Memperbarui data stand & pesanan...", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "Refresh", tint = TextPrimary)
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
                        refreshAllSellerData()
                    },
                    icon = {
                        BadgedBox(badge = {
                            if (currentActiveQueueCount > 0) {
                                Badge(containerColor = Color(0xFFEA580C)) {
                                    Text("$currentActiveQueueCount")
                                }
                            }
                        }) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.ReceiptLong, contentDescription = "Pesanan")
                        }
                    },
                    label = { Text("Pesanan Masuk", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
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

        // =========================================================================
        // VIEW TAB 1: DAFTAR PESANAN MASUK & SCAN QR (selectedNavTab == 1)
        // =========================================================================
        if (selectedNavTab == 1) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // Top Banner with Scan QR Action
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onScanQrClick() },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = BluePrimary)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color.White.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = Icons.Default.QrCodeScanner, contentDescription = "Scan", tint = Color.White, modifier = Modifier.size(24.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(text = "Scan QR Pengambilan Siswa", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Text(text = "Arahkan kamera ke tiket QR di HP siswa", fontSize = 11.sp, color = Color.White.copy(alpha = 0.85f))
                            }
                        }
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Scan", tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Title & Active Queue Indicator
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "Daftar Pesanan Masuk", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text(text = "Stand: $currentStandName ($currentCounterSlot)", fontSize = 11.sp, color = TextSecondary)
                    }

                    IconButton(
                        onClick = { refreshAllSellerData() },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "Refresh", tint = BluePrimary, modifier = Modifier.size(18.dp))
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Filter Chips Row
                val filterOptions = listOf("Semua", "Antrean Aktif", "Siap Diambil", "Selesai")
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    filterOptions.forEach { filter ->
                        val isSelected = selectedOrderFilter == filter
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isSelected) BluePrimary else BlueLightBg)
                                .border(1.dp, if (isSelected) BluePrimary else BlueChipBg, RoundedCornerShape(20.dp))
                                .clickable { selectedOrderFilter = filter }
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = filter,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else BluePrimary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Filtered Orders List
                val filteredOrders = orderList.filter { order ->
                    val status = (order.status ?: "PENDING").uppercase()
                    when (selectedOrderFilter) {
                        "Antrean Aktif" -> status == "PENDING" || status == "PENDING_PAYMENT" || status == "COOKING" || status == "READY" || status == "READY_FOR_PICKUP"
                        "Siap Diambil" -> status == "READY" || status == "READY_FOR_PICKUP"
                        "Selesai" -> status == "COMPLETED"
                        else -> true
                    }
                }

                if (isLoadingOrders) {
                    Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = BluePrimary)
                    }
                } else if (filteredOrders.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "📦", fontSize = 42.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Tidak Ada Pesanan",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Belum ada pesanan siswa untuk kategori '$selectedOrderFilter'",
                                fontSize = 12.sp,
                                color = TextSecondary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        filteredOrders.forEach { order ->
                            val statusUpper = (order.status ?: "PENDING").uppercase()
                            val isCompleted = statusUpper == "COMPLETED"
                            val isReady = statusUpper == "READY" || statusUpper == "READY_FOR_PICKUP"
                            val isCooking = statusUpper == "COOKING"
                            val isPending = statusUpper == "PENDING" || statusUpper == "PENDING_PAYMENT"

                            val statusBg = when {
                                isCompleted -> Color(0xFFDCFCE7)
                                isReady -> Color(0xFFDBEAFE)
                                isCooking -> Color(0xFFFFEDD5)
                                else -> Color(0xFFFEF3C7)
                            }
                            val statusColor = when {
                                isCompleted -> Color(0xFF16A34A)
                                isReady -> BluePrimary
                                isCooking -> Color(0xFFC2410C)
                                else -> Color(0xFFB45309)
                            }
                            val statusText = when {
                                isCompleted -> "SELESAI (SUDAH DIAMBIL)"
                                isReady -> "SIAP DIAMBIL SISWA"
                                isCooking -> "SEDANG DIMASAK"
                                else -> "MENUNGGU PROSES"
                            }

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                border = BorderStroke(1.dp, BorderColor)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    // Order Header Row
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "#${order.orderNumber}",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = TextPrimary
                                        )

                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(statusBg)
                                                .padding(horizontal = 8.dp, vertical = 3.dp)
                                        ) {
                                            Text(
                                                text = statusText,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = statusColor
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))

                                    // Student Name & Class
                                    Text(
                                        text = "Pemesan: ${order.studentName ?: "Siswa"} (${order.studentClass ?: "-"})",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = TextSecondary
                                    )

                                    HorizontalDivider(
                                        modifier = Modifier.padding(vertical = 8.dp),
                                        thickness = 0.8.dp,
                                        color = BorderColor
                                    )

                                    // Items List
                                    order.orderItems.forEach { item ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "${item.quantity}x ${item.name}",
                                                fontSize = 12.sp,
                                                color = TextPrimary
                                            )
                                            Text(
                                                text = "Rp ${String.format(Locale.GERMANY, "%,d", item.price * item.quantity)}",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = TextPrimary
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(2.dp))
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    // Total & Payment Method Row
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(Color(0xFFF1F5F9))
                                                .padding(horizontal = 8.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = if (order.paymentMethod?.contains("CASH", true) == true || order.paymentMethod?.contains("TUNAI", true) == true) "💵 Tunai di Loket" else "💳 Non-Tunai / QRIS",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = TextSecondary
                                            )
                                        }

                                        Text(
                                            text = "Total: Rp ${String.format(Locale.GERMANY, "%,d", order.totalAmount)}",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = BluePrimary
                                        )
                                    }

                                    // Action Buttons based on status
                                    if (!isCompleted) {
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            if (isPending) {
                                                Button(
                                                    onClick = {
                                                        coroutineScope.launch {
                                                            repository.updateOrderStatus(order.id, "COOKING").onSuccess {
                                                                Toast.makeText(context, "Pesanan #${order.orderNumber} mulai dimasak", Toast.LENGTH_SHORT).show()
                                                                refreshAllSellerData()
                                                            }.onFailure {
                                                                Toast.makeText(context, "Status diperbarui", Toast.LENGTH_SHORT).show()
                                                                refreshAllSellerData()
                                                            }
                                                        }
                                                    },
                                                    modifier = Modifier.weight(1f).height(40.dp),
                                                    shape = RoundedCornerShape(10.dp),
                                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF97316))
                                                ) {
                                                    Text("🍳 Mulai Masak", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }

                                            if (isCooking) {
                                                Button(
                                                    onClick = {
                                                        coroutineScope.launch {
                                                            repository.updateOrderStatus(order.id, "READY").onSuccess {
                                                                Toast.makeText(context, "Pesanan #${order.orderNumber} siap diambil!", Toast.LENGTH_SHORT).show()
                                                                refreshAllSellerData()
                                                            }.onFailure {
                                                                refreshAllSellerData()
                                                            }
                                                        }
                                                    },
                                                    modifier = Modifier.weight(1f).height(40.dp),
                                                    shape = RoundedCornerShape(10.dp),
                                                    colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
                                                ) {
                                                    Text("🔔 Siap Diambil", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }

                                            if (isReady) {
                                                // Scan QR Button
                                                Button(
                                                    onClick = onScanQrClick,
                                                    modifier = Modifier.weight(1f).height(40.dp),
                                                    shape = RoundedCornerShape(10.dp),
                                                    colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
                                                ) {
                                                    Icon(imageVector = Icons.Default.QrCodeScanner, contentDescription = "Scan", modifier = Modifier.size(16.dp))
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text("Scan QR", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                                }

                                                // Direct Complete Button (e.g. for cash payment verification)
                                                OutlinedButton(
                                                    onClick = {
                                                        coroutineScope.launch {
                                                            repository.confirmPickup(order.id).onSuccess {
                                                                Toast.makeText(context, "Pesanan #${order.orderNumber} selesai & poin ditambahkan!", Toast.LENGTH_SHORT).show()
                                                                refreshAllSellerData()
                                                            }.onFailure { e ->
                                                                Toast.makeText(context, "Konfirmasi selesai: ${e.message}", Toast.LENGTH_SHORT).show()
                                                                refreshAllSellerData()
                                                            }
                                                        }
                                                    },
                                                    modifier = Modifier.weight(1f).height(40.dp),
                                                    shape = RoundedCornerShape(10.dp),
                                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF16A34A)),
                                                    border = BorderStroke(1.dp, Color(0xFF16A34A))
                                                ) {
                                                    Icon(imageVector = Icons.Default.Check, contentDescription = "Selesai", modifier = Modifier.size(16.dp))
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text("Selesai (Cash)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // =========================================================================
            // VIEW TAB 0: BERANDA DASHBOARD SELLER
            // =========================================================================
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
                            .clickable { selectedNavTab = 1 }
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
                                        text = "Pesanan Baru Menunggu!",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "$currentActiveQueueCount pesanan dalam antrean aktif. Ketuk untuk melihat daftar.",
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
                                    text = "Gunakan kamera untuk verifikasi pengambilan makanan",
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

                // 4. Revenue Today Card (Pendapatan Stand Hari Ini dari /stands/revenue)
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
                                    text = "📈 Terverifikasi",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF16A34A)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "$currentCompletedOrders pesanan telah selesai disajikan hari ini",
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
                    // Card 1: Antre Aktif (Klik untuk langsung buka tab pesanan)
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedNavTab = 1 },
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

                            Text(text = "$currentAveragePrep Menit", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = TextPrimary)

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
                    if (isLoadingMenus) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, BorderColor)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(28.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator(color = BluePrimary, modifier = Modifier.size(32.dp))
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Memuat daftar menu dari server...",
                                    fontSize = 13.sp,
                                    color = TextSecondary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    } else if (menuErrorMessage != null) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                            border = BorderStroke(1.dp, Color(0xFFFCA5A5))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(text = "⚠️", fontSize = 32.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Gagal Memuat Menu dari Server",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFDC2626)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = menuErrorMessage ?: "Terjadi kesalahan saat memuat data menu",
                                    fontSize = 12.sp,
                                    color = TextSecondary,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                OutlinedButton(
                                    onClick = { refreshAllSellerData() },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626)),
                                    border = BorderStroke(1.dp, Color(0xFFDC2626))
                                ) {
                                    Icon(imageVector = Icons.Default.Refresh, contentDescription = "Coba Lagi", modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Coba Lagi", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    } else if (menuList.isEmpty()) {
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
                                Text(text = "Belum Ada Menu di Stand Anda", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = "Daftar menu stand Anda saat ini masih kosong. Klik '+ Tambah Menu' untuk mulai menambahkan menu makanan/minuman.", fontSize = 12.sp, color = TextSecondary, textAlign = TextAlign.Center)
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
                            var updateSuccessCount = 0
                            var updateFailCount = 0
                            menuList.forEach { item ->
                                repository.updateMenu(item.id, stock = item.stock, isAvailable = item.isAvailable).onSuccess {
                                    updateSuccessCount++
                                }.onFailure {
                                    updateFailCount++
                                }
                            }
                            if (updateFailCount > 0) {
                                Toast.makeText(context, "Sebagian stok gagal disimpan ($updateFailCount gagal, $updateSuccessCount berhasil)", Toast.LENGTH_LONG).show()
                            } else {
                                Toast.makeText(context, "Perubahan Stok Berhasil Disimpan ($updateSuccessCount menu tersimpan)!", Toast.LENGTH_SHORT).show()
                            }
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
                            }.onFailure { err ->
                                Toast.makeText(context, "Gagal menyimpan perubahan stand: ${err.message ?: "Kesalahan server"}", Toast.LENGTH_LONG).show()
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
                                if (created != null) {
                                    val newItem = SellerMenuItem(
                                        id = created.id,
                                        name = created.name,
                                        price = created.price,
                                        stock = created.stock,
                                        isAvailable = (created.stock > 0) && created.isAvailable,
                                        foodEmoji = determineFoodEmoji(created.name)
                                    )
                                    menuList = listOf(newItem) + menuList
                                } else {
                                    refreshAllSellerData()
                                }
                                showAddMenuDialog = false
                                Toast.makeText(context, "Menu '${newMenuName.trim()}' Berhasil Ditambahkan ke Server!", Toast.LENGTH_SHORT).show()
                            }.onFailure { e ->
                                Toast.makeText(context, "Gagal menambahkan menu ke server: ${e.message ?: "Terjadi kesalahan"}", Toast.LENGTH_LONG).show()
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
                            val result = repository.updateMenu(
                                menuId = item.id,
                                name = editMenuName.trim(),
                                price = priceInt,
                                stock = stockInt,
                                isAvailable = stockInt > 0
                            )
                            isSavingEditMenu = false

                            result.onSuccess {
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
                                Toast.makeText(context, "Menu Berhasil Diperbarui di Database!", Toast.LENGTH_SHORT).show()
                            }.onFailure { err ->
                                Toast.makeText(context, "Gagal memperbarui menu: ${err.message ?: "Terjadi kesalahan"}", Toast.LENGTH_LONG).show()
                            }
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
                            val result = repository.deleteMenu(item.id)
                            isDeletingMenu = false
                            result.onSuccess {
                                menuList = menuList.filter { it.id != item.id }
                                deletingMenuItem = null
                                Toast.makeText(context, "Menu '${item.name}' Berhasil Dihapus dari Database", Toast.LENGTH_SHORT).show()
                            }.onFailure { err ->
                                Toast.makeText(context, "Gagal menghapus menu: ${err.message ?: "Terjadi kesalahan"}", Toast.LENGTH_LONG).show()
                            }
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
