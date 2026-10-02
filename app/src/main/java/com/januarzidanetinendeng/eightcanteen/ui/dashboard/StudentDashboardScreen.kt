package com.januarzidanetinendeng.eightcanteen.ui.dashboard

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.januarzidanetinendeng.eightcanteen.data.local.SessionManager
import com.januarzidanetinendeng.eightcanteen.data.repository.CanteenRepository
import com.januarzidanetinendeng.eightcanteen.ui.checkout.CartItem
import com.januarzidanetinendeng.eightcanteen.ui.checkout.CartViewModel
import com.januarzidanetinendeng.eightcanteen.ui.checkout.FoodImageType
import com.januarzidanetinendeng.eightcanteen.ui.checkout.formatRupiah
import com.januarzidanetinendeng.eightcanteen.ui.components.EKantinLogoIcon
import com.januarzidanetinendeng.eightcanteen.ui.theme.BlueChipBg
import com.januarzidanetinendeng.eightcanteen.ui.theme.BlueLightBg
import com.januarzidanetinendeng.eightcanteen.ui.theme.BlueLightCard
import com.januarzidanetinendeng.eightcanteen.ui.theme.BluePrimary
import com.januarzidanetinendeng.eightcanteen.ui.theme.BorderColor
import com.januarzidanetinendeng.eightcanteen.ui.theme.EightCanteenTheme
import com.januarzidanetinendeng.eightcanteen.ui.theme.InputBg
import com.januarzidanetinendeng.eightcanteen.ui.theme.ScreenBg
import com.januarzidanetinendeng.eightcanteen.ui.theme.TextMuted
import com.januarzidanetinendeng.eightcanteen.ui.theme.TextPrimary
import com.januarzidanetinendeng.eightcanteen.ui.theme.TextSecondary
import java.util.Locale

data class StandItem(
    val id: String,
    val name: String,
    val rating: String,
    val distanceOrTime: String,
    val isBusy: Boolean,
    val foodEmoji: String
)

data class MenuItem(
    val id: String,
    val name: String,
    val standName: String,
    val price: Int,
    val stock: Int,
    val prepareTime: String,
    val tag: String?,
    val tagColor: Color?,
    val foodEmoji: String,
    val standId: String? = null,
    val category: String = "Makanan",
    val isAvailable: Boolean = true,
    val imageUrl: String? = null
)

private fun classifyMenuCategory(name: String, standCategory: String?): String {
    if (!standCategory.isNullOrBlank()) {
        val sc = standCategory.trim().lowercase()
        if (sc == "minuman" || sc.contains("drink") || sc.contains("beverage")) return "Minuman"
        if (sc == "makanan" || sc.contains("food")) return "Makanan"
    }
    val n = name.lowercase()
    val isDrink = n.contains("jus") || n.contains("juice") || n.contains("teh") ||
            n.contains("tea") || n.contains("kopi") || n.contains("coffee") ||
            n.contains("es ") || n.contains("boba") || n.contains("drink") ||
            n.contains("air") || n.contains("smoothie") || n.contains("susu") ||
            n.contains("lemon") || n.contains("cincau") || n.contains("syrup")
    return if (isDrink) "Minuman" else "Makanan"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentDashboardScreen(
    cartViewModel: CartViewModel = remember { CartViewModel() },
    studentName: String = "",
    studentClass: String = "",
    loyaltyPoints: Int = 0,
    initialNavTab: Int = 0,
    onPointsClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    onCheckoutClick: () -> Unit = {},
    onOrderClick: (orderId: String) -> Unit = {},
    onNavigateToNotifications: () -> Unit = {},
    onSeeAllStandsClick: () -> Unit = {},
    onStandClick: (standId: String, standName: String) -> Unit = { _, _ -> }
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val filterScrollState = rememberScrollState()
    val standsScrollState = rememberScrollState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("Semua") }
    var selectedNavTab by remember { mutableIntStateOf(initialNavTab) }

    LaunchedEffect(initialNavTab) {
        selectedNavTab = initialNavTab
    }

    val cartUiState by cartViewModel.uiState.collectAsState()
    val cartCount = cartUiState.totalMenuCount
    val cartTotal = cartUiState.subtotal

    var currentStudentName by remember { mutableStateOf(studentName.ifBlank { "Siswa" }) }
    var currentStudentClass by remember { mutableStateOf(studentClass) }
    var currentLoyaltyPoints by remember { mutableIntStateOf(loyaltyPoints) }

    var stands by remember { mutableStateOf<List<StandItem>>(emptyList()) }
    var menuItems by remember { mutableStateOf<List<MenuItem>>(emptyList()) }
    var isLoadingStands by remember { mutableStateOf(true) }
    var isLoadingMenus by remember { mutableStateOf(true) }
    var isRefreshing by remember { mutableStateOf(false) }
    val repository = remember { CanteenRepository() }

    fun refreshAllStudentData(isPullRefresh: Boolean = false) {
        if (isPullRefresh) isRefreshing = true
        coroutineScope.launch {
            try {
                val session = SessionManager.getInstance(context)
                if (session.getUserName().isNotBlank() && session.getUserName() != "Pengguna") {
                    currentStudentName = session.getUserName()
                }
                if (!session.getStudentClass().isNullOrBlank()) {
                    currentStudentClass = session.getStudentClass() ?: ""
                }
                currentLoyaltyPoints = session.getPoints()

                // 1. Fetch User Profile
                repository.getMyProfile().onSuccess { res ->
                    res.data?.let { profile ->
                        val fetchedName = (profile.fullName ?: profile.name)?.trim()
                        if (!fetchedName.isNullOrBlank() && !fetchedName.equals("EMPTY", ignoreCase = true) && !fetchedName.equals("Pengguna", ignoreCase = true)) {
                            currentStudentName = fetchedName
                        }
                        val fetchedClass = (profile.resolvedClass ?: profile.studentClass ?: profile.className)?.trim()
                        if (!fetchedClass.isNullOrBlank()) {
                            currentStudentClass = fetchedClass
                        }
                        session.updateProfile(name = currentStudentName, studentClass = currentStudentClass)
                        profile.points?.let {
                            currentLoyaltyPoints = it
                            session.updatePoints(it)
                        }
                    }
                }

                // 2. Fetch Stands
                isLoadingStands = true
                repository.getStands().onSuccess { res ->
                    isLoadingStands = false
                    val apiStands = res.data ?: emptyList()
                    stands = apiStands.map { stand ->
                        StandItem(
                            id = stand.id,
                            name = stand.name,
                            rating = "",
                            distanceOrTime = "${stand.counterSlot ?: "Stand"} • ${if (stand.isOpen) "Buka" else "Tutup"}",
                            isBusy = false,
                            foodEmoji = when {
                                stand.name.contains("Kebab", true) -> "🥙"
                                stand.name.contains("Ketoprak", true) -> "🍲"
                                stand.name.contains("Ayam", true) -> "🍗"
                                stand.name.contains("Jus", true) || stand.name.contains("Buah", true) -> "🍹"
                                stand.name.contains("Kopi", true) || stand.name.contains("Barista", true) -> "🧋"
                                else -> "🍱"
                            }
                        )
                    }
                }.onFailure {
                    isLoadingStands = false
                    stands = emptyList()
                }

                // 3. Fetch Menus
                isLoadingMenus = true
                repository.getAllMenus().onSuccess { res ->
                    isLoadingMenus = false
                    val apiMenus = res.data ?: emptyList()
                    menuItems = apiMenus.map { menu ->
                        val resolvedCategory = classifyMenuCategory(menu.name, menu.stands?.category)
                        val isAvail = menu.isAvailable && menu.stock > 0
                        MenuItem(
                            id = menu.id,
                            name = menu.name,
                            standName = menu.stands?.name ?: "Stand Kantin",
                            price = menu.price,
                            stock = menu.stock,
                            prepareTime = menu.prepareTime ?: "10 mnt",
                            tag = when {
                                !menu.isAvailable || menu.stock <= 0 -> "Tidak Tersedia"
                                menu.stock in 1..4 -> "Tersisa ${menu.stock}"
                                menu.stock > 10 -> "Tersedia"
                                else -> null
                            },
                            tagColor = when {
                                !menu.isAvailable || menu.stock <= 0 -> Color(0xFFFEE2E2)
                                menu.stock in 1..4 -> Color(0xFFFEF3C7)
                                else -> Color(0xFFDCFCE7)
                            },
                            foodEmoji = when {
                                menu.name.contains("Kebab", true) -> "🥙"
                                menu.name.contains("Ketoprak", true) -> "🍲"
                                menu.name.contains("Ayam", true) || menu.name.contains("Geprek", true) -> "🍗"
                                menu.name.contains("Dimsum", true) -> "🥟"
                                menu.name.contains("Kopi", true) -> "☕"
                                menu.name.contains("Teh", true) -> "🧋"
                                menu.name.contains("Jus", true) -> "🍹"
                                else -> "🍛"
                            },
                            standId = menu.standId ?: menu.stands?.id,
                            category = resolvedCategory,
                            isAvailable = isAvail,
                            imageUrl = menu.imageUrl
                        )
                    }
                }.onFailure {
                    isLoadingMenus = false
                    menuItems = emptyList()
                }
            } finally {
                isRefreshing = false
            }
        }
    }

    LaunchedEffect(Unit) {
        refreshAllStudentData()
    }

    val displayedMenus = remember(searchQuery, selectedFilter, menuItems) {
        menuItems.filter { item ->
            val matchesQuery = searchQuery.isBlank() ||
                item.name.contains(searchQuery, ignoreCase = true) ||
                item.standName.contains(searchQuery, ignoreCase = true)
            val matchesFilter = when (selectedFilter) {
                "Makanan" -> item.category.equals("Makanan", ignoreCase = true)
                "Minuman" -> item.category.equals("Minuman", ignoreCase = true)
                else -> true
            }
            matchesQuery && matchesFilter
        }
    }

    Scaffold(
        containerColor = ScreenBg,
        topBar = {
            if (selectedNavTab == 0) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
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

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { Toast.makeText(context, "Pencarian Aktif", Toast.LENGTH_SHORT).show() }) {
                            Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = TextPrimary)
                        }

                        Box {
                            IconButton(onClick = { onNavigateToNotifications() }) {
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

                        // Profile / Logout Button for Demo
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFEE2E2))
                            .clickable {
                                Toast.makeText(context, "Logout Berhasil", Toast.LENGTH_SHORT).show()
                                onLogoutClick()
                            },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.Logout, contentDescription = "Logout", tint = Color(0xFFDC2626), modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = selectedNavTab == 0,
                    onClick = { selectedNavTab = 0 },
                    icon = { Icon(imageVector = Icons.Default.Storefront, contentDescription = "Beranda") },
                    label = { Text("Beranda", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = BluePrimary, selectedTextColor = BluePrimary, indicatorColor = BlueLightBg)
                )
                NavigationBarItem(
                    selected = selectedNavTab == 1,
                    onClick = {
                        selectedNavTab = 1
                    },
                    icon = { Icon(imageVector = Icons.AutoMirrored.Filled.ReceiptLong, contentDescription = "Pesanan") },
                    label = { Text("Pesanan", fontSize = 11.sp, fontWeight = if (selectedNavTab == 1) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = BluePrimary, selectedTextColor = BluePrimary, indicatorColor = BlueLightBg)
                )
                NavigationBarItem(
                    selected = selectedNavTab == 2,
                    onClick = {
                        selectedNavTab = 2
                        onPointsClick() // Reuse points for profile/rewards tab for students
                    },
                    icon = { Icon(imageVector = Icons.Default.Person, contentDescription = "Profil") },
                    label = { Text("Profil", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = BluePrimary, selectedTextColor = BluePrimary, indicatorColor = BlueLightBg)
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (selectedNavTab == 1) {
                StudentOrderHistoryScreen(
                    onBackClick = { selectedNavTab = 0 },
                    onOrderClick = onOrderClick,
                    onOrderNewFoodClick = { selectedNavTab = 0 },
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                PullToRefreshBox(
                    isRefreshing = isRefreshing,
                    onRefresh = { refreshAllStudentData(isPullRefresh = true) },
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                // 1. User Greeting & Loyalty Points Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFEF3C7))
                                .border(1.5.dp, BluePrimary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Avatar",
                                tint = Color(0xFFD97706),
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Halo, $currentStudentName",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "👋", fontSize = 14.sp)
                            }
                            Text(
                                text = currentStudentClass,
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    // Loyalty Badge (Clickable to open Points & Rewards)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFFFEF3C7))
                            .border(1.dp, Color(0xFFFDE047), RoundedCornerShape(20.dp))
                            .clickable { onPointsClick() }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "✪", fontSize = 14.sp, color = Color(0xFFB45309))
                            Spacer(modifier = Modifier.width(4.dp))
                            Column {
                                Text(text = "LOYALTY", fontSize = 8.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFFB45309))
                                Text(text = "$currentLoyaltyPoints Poin", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF92400E))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 2. Loyalty Progress Card (Clickable to open Points & Rewards)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(BlueLightCard)
                        .border(1.dp, BlueChipBg, RoundedCornerShape(18.dp))
                        .clickable { onPointsClick() }
                        .padding(14.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "🍱", fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "$currentLoyaltyPoints/50 Poin",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "menuju Gratis Makan Siang!",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }

                            Text(
                                text = "${(currentLoyaltyPoints * 2).coerceAtMost(100)}%",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = BluePrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Progress Bar
                        LinearProgressIndicator(
                            progress = { (currentLoyaltyPoints / 50f).coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = BluePrimary,
                            trackColor = Color(0xFFDBEAFE)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "• Es Teh Gratis (20p)", fontSize = 10.sp, color = BluePrimary, fontWeight = FontWeight.Bold)
                            Text(text = "🍱 Makan Siang (50p)", fontSize = 10.sp, color = TextMuted)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 3. Search Bar Box
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Cari menu, stand, atau cemilan favorit...", fontSize = 13.sp, color = TextMuted) },
                    leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = TextSecondary) },
                    trailingIcon = { Icon(imageVector = Icons.Default.Mic, contentDescription = "Mic", tint = BluePrimary) },
                    singleLine = true,
                    shape = RoundedCornerShape(22.dp),
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp, color = TextPrimary),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = InputBg,
                        unfocusedContainerColor = InputBg,
                        focusedBorderColor = BluePrimary,
                        unfocusedBorderColor = BorderColor
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // 4. Horizontal Category Filter Chips (Makanan & Minuman)
                val filterList = remember { listOf("Semua", "Makanan", "Minuman") }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(filterScrollState),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    filterList.forEach { filter ->
                        val isSelected = selectedFilter == filter
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isSelected) BluePrimary else Color.White)
                                .border(1.dp, if (isSelected) BluePrimary else BorderColor, RoundedCornerShape(20.dp))
                                .clickable { selectedFilter = filter }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                when (filter) {
                                    "Semua" -> Text(text = "🍽️ ", fontSize = 12.sp)
                                    "Makanan" -> Text(text = "🍱 ", fontSize = 12.sp)
                                    "Minuman" -> Text(text = "🍹 ", fontSize = 12.sp)
                                }

                                Text(
                                    text = filter,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else TextPrimary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 5. Daftar Stand Section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Daftar Stand", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)

                    Text(
                        text = "Lihat Semua >",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = BluePrimary,
                        modifier = Modifier.clickable { onSeeAllStandsClick() }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Horizontal Stand Cards (Tanpa Rating)
                if (isLoadingStands) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(color = BluePrimary, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Memuat daftar stand...", fontSize = 12.sp, color = TextSecondary)
                        }
                    }
                } else if (stands.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, BorderColor)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Belum ada stand yang terdaftar di kantin",
                                fontSize = 12.sp,
                                color = TextSecondary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(standsScrollState),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        stands.forEach { stand ->
                            Card(
                                modifier = Modifier
                                    .width(150.dp)
                                    .clickable {
                                        onStandClick(stand.id, stand.name)
                                    },
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                border = BorderStroke(1.dp, BorderColor)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(90.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Color(0xFFEFF6FF)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = stand.foodEmoji, fontSize = 42.sp)

                                        if (stand.isBusy) {
                                            Box(
                                                modifier = Modifier
                                                    .align(Alignment.BottomEnd)
                                                    .padding(6.dp)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(Color(0xFFFFEDD5))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(text = "Ramai", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFFC2410C))
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(text = stand.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary, maxLines = 1)
                                    Text(text = stand.distanceOrTime, fontSize = 11.sp, color = TextSecondary)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 6. Menu Terlaris Hari Ini Section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Menu Terlaris Hari Ini", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "📈", fontSize = 14.sp)
                    }

                    Text(text = "Pre-order istirahat 1", fontSize = 11.sp, color = TextMuted)
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Vertical Menu List
                if (isLoadingMenus) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = BluePrimary, modifier = Modifier.size(28.dp), strokeWidth = 2.5.dp)
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("Memuat menu kantin...", fontSize = 12.sp, color = TextSecondary)
                        }
                    }
                } else if (displayedMenus.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, BorderColor)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "🍽️", fontSize = 34.sp)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = if (searchQuery.isNotBlank() || selectedFilter != "Semua")
                                        "Tidak ada menu yang sesuai pencarian"
                                    else
                                        "Belum ada menu yang tersedia saat ini",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Menu akan muncul setelah penjual stand menambahkannya.",
                                    fontSize = 11.sp,
                                    color = TextSecondary,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        displayedMenus.forEach { menu ->
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
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Food Thumbnail Box
                                    Box(
                                        modifier = Modifier
                                            .size(80.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Color(0xFFFEF3C7)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (!menu.imageUrl.isNullOrBlank()) {
                                            AsyncImage(
                                                model = ImageRequest.Builder(context)
                                                    .data(menu.imageUrl)
                                                    .crossfade(true)
                                                    .build(),
                                                contentDescription = menu.name,
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier.fillMaxSize()
                                            )
                                        } else {
                                            Text(text = menu.foodEmoji, fontSize = 38.sp)
                                        }

                                        // Low Stock Badge
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.BottomStart)
                                                .padding(4.dp)
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(Color(0xFFDC2626))
                                                .padding(horizontal = 5.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "Sisa ${menu.stock}",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    // Details Column
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(text = menu.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                            menu.tag?.let { tag ->
                                                Spacer(modifier = Modifier.width(6.dp))
                                                val tagTextColor = if (tag == "Tidak Tersedia") Color(0xFFDC2626) else if (menu.stock in 1..4) Color(0xFFB45309) else Color(0xFF15803D)
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(6.dp))
                                                        .background(menu.tagColor ?: Color(0xFFFEF3C7))
                                                        .padding(horizontal = 5.dp, vertical = 1.dp)
                                                ) {
                                                    Text(text = tag, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = tagTextColor)
                                                }
                                            }
                                        }

                                        Text(text = menu.standName, fontSize = 11.sp, color = TextSecondary)
                                        Text(text = "⏱️ ${menu.prepareTime} • Tersisa: ${menu.stock} porsi", fontSize = 10.sp, color = TextMuted)

                                        Spacer(modifier = Modifier.height(4.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "Rp ${String.format(Locale.GERMANY, "%,d", menu.price)}",
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = if (menu.isAvailable && menu.stock > 0) BluePrimary else TextMuted
                                            )

                                            val canOrder = menu.isAvailable && menu.stock > 0

                                            Button(
                                                onClick = {
                                                    val foodType = when {
                                                        menu.name.contains("Kebab", true) -> FoodImageType.KEBAB
                                                        menu.name.contains("Ketoprak", true) -> FoodImageType.KETOPRAK
                                                        menu.name.contains("Ayam", true) || menu.name.contains("Geprek", true) -> FoodImageType.AYAM_GEPREK
                                                        menu.name.contains("Dimsum", true) -> FoodImageType.DIMSUM
                                                        menu.name.contains("Kopi", true) -> FoodImageType.ES_KOPI
                                                        menu.name.contains("Jeruk", true) || menu.name.contains("Jus", true) -> FoodImageType.ES_JERUK
                                                        else -> FoodImageType.GENERIC
                                                    }
                                                    cartViewModel.addToCart(
                                                        CartItem(
                                                            id = menu.id,
                                                            name = menu.name,
                                                            price = menu.price.toDouble(),
                                                            quantity = 1,
                                                            standName = menu.standName,
                                                            standId = menu.standId,
                                                            imageType = foodType,
                                                            foodEmoji = menu.foodEmoji
                                                        )
                                                    )
                                                    Toast.makeText(context, "${menu.name} ditambahkan ke keranjang!", Toast.LENGTH_SHORT).show()
                                                },
                                                enabled = canOrder,
                                                shape = RoundedCornerShape(16.dp),
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = if (canOrder) BluePrimary else Color(0xFFE2E8F0),
                                                    contentColor = if (canOrder) Color.White else Color(0xFF94A3B8),
                                                    disabledContainerColor = Color(0xFFE2E8F0),
                                                    disabledContentColor = Color(0xFF94A3B8)
                                                ),
                                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                                modifier = Modifier.height(34.dp)
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    if (canOrder) {
                                                        Icon(imageVector = Icons.Default.Add, contentDescription = "Pesan", modifier = Modifier.size(14.dp))
                                                        Spacer(modifier = Modifier.width(4.dp))
                                                        Text(text = "Pesan", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                    } else {
                                                        Text(text = "Habis", fontSize = 11.sp, fontWeight = FontWeight.Bold)
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

                    Spacer(modifier = Modifier.height(80.dp))
                }
            }

            // 7. Floating Cart Bar
            AnimatedVisibility(
                visible = cartCount > 0,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 12.dp, start = 16.dp, end = 16.dp),
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it })
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(8.dp, RoundedCornerShape(24.dp), ambientColor = Color(0x332563EB))
                        .clip(RoundedCornerShape(24.dp))
                        .background(BluePrimary)
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = Icons.Default.ShoppingBag, contentDescription = "Cart", tint = Color.White, modifier = Modifier.size(20.dp))
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .size(16.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFFEF3C7)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = "$cartCount", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB45309))
                                }
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column {
                                Text(text = "$cartCount Item di Keranjang", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Text(text = "Rp ${formatRupiah(cartTotal)}", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                            }
                        }

                        Button(
                            onClick = onCheckoutClick,
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = BluePrimary),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "Checkout", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Go", modifier = Modifier.size(14.dp))
                            }
                        }
                    }
                }
            }
        } // end of else
    }
}
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun StudentDashboardScreenPreview() {
    EightCanteenTheme {
        StudentDashboardScreen()
    }
}
