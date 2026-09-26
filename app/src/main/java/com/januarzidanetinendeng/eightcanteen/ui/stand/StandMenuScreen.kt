package com.januarzidanetinendeng.eightcanteen.ui.stand

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.januarzidanetinendeng.eightcanteen.data.repository.CanteenRepository
import com.januarzidanetinendeng.eightcanteen.ui.checkout.CartItem
import com.januarzidanetinendeng.eightcanteen.ui.checkout.CartViewModel
import com.januarzidanetinendeng.eightcanteen.ui.checkout.FoodImageType
import com.januarzidanetinendeng.eightcanteen.ui.dashboard.MenuItem
import com.januarzidanetinendeng.eightcanteen.ui.theme.BlueLightBg
import com.januarzidanetinendeng.eightcanteen.ui.theme.BluePrimary
import com.januarzidanetinendeng.eightcanteen.ui.theme.BorderColor
import com.januarzidanetinendeng.eightcanteen.ui.theme.InputBg
import com.januarzidanetinendeng.eightcanteen.ui.theme.ScreenBg
import com.januarzidanetinendeng.eightcanteen.ui.theme.TextMuted
import com.januarzidanetinendeng.eightcanteen.ui.theme.TextPrimary
import com.januarzidanetinendeng.eightcanteen.ui.theme.TextSecondary
import java.util.Locale

@Composable
fun StandMenuScreen(
    cartViewModel: CartViewModel = remember { CartViewModel() },
    standId: String,
    standName: String,
    standLocation: String = "Stand SMKN 8 Jakarta",
    onBackClick: () -> Unit = {},
    onCheckoutClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val repository = remember { CanteenRepository() }
    val filterScrollState = rememberScrollState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Semua") }
    var isLoading by remember { mutableStateOf(true) }

    // Stand Emoji mapping berdasarkan nama stand
    val standEmoji = remember(standName) {
        when {
            standName.contains("Kebab", true) -> "🥙"
            standName.contains("Jus", true) || standName.contains("Buah", true) -> "🍹"
            standName.contains("Ayam", true) || standName.contains("Geprek", true) -> "🍗"
            standName.contains("Dimsum", true) || standName.contains("Siomay", true) -> "🥟"
            standName.contains("Kopi", true) || standName.contains("Teh", true) -> "🧋"
            standName.contains("Kantin", true) || standName.contains("Ketoprak", true) -> "🍲"
            else -> "🍱"
        }
    }

    // Default Fallback Menus jika API belum memiliki data untuk stand tertentu
    val fallbackMenus = remember(standName, standId) {
        when {
            standName.contains("Kebab", true) -> listOf(
                MenuItem("m-k1", "Kebab Daging Spesial", standName, 15000, 14, "10 mnt", "Favorit", Color(0xFFFEF3C7), "🥙", standId, "Makanan"),
                MenuItem("m-k2", "Ketoprak Telur", standName, 12000, 15, "15 mnt", null, null, "🍲", standId, "Makanan"),
                MenuItem("m-k3", "Roti Maryam Coklat Keju", standName, 10000, 20, "7 mnt", null, null, "🫓", standId, "Makanan"),
                MenuItem("m-k4", "Es Teh Segar Manis", standName, 5000, 49, "Cepat", null, null, "🧋", standId, "Minuman")
            )
            standName.contains("Jus", true) || standName.contains("Buah", true) -> listOf(
                MenuItem("m-j1", "Jus Jeruk Peras Segar", standName, 5000, 50, "5 mnt", "Segar", Color(0xFFDCFCE7), "🍹", standId, "Minuman"),
                MenuItem("m-j2", "Jus Mangga Manis", standName, 12000, 15, "8 mnt", "Favorit", Color(0xFFFEF3C7), "🥭", standId, "Minuman"),
                MenuItem("m-j3", "Jus Alpukat Kocok", standName, 14000, 12, "8 mnt", null, null, "🥑", standId, "Minuman"),
                MenuItem("m-j4", "Es Buah Spesial Campur", standName, 10000, 25, "5 mnt", null, null, "🍧", standId, "Minuman")
            )
            standName.contains("Ayam", true) || standName.contains("Geprek", true) -> listOf(
                MenuItem("m-a1", "Paket Nasi Ayam Geprek Level 1-5", standName, 16000, 30, "12 mnt", "Best Seller", Color(0xFFFEE2E2), "🍗", standId, "Makanan"),
                MenuItem("m-a2", "Ayam Crispy Sambal Matah", standName, 17000, 20, "12 mnt", null, null, "🍗", standId, "Makanan"),
                MenuItem("m-a3", "Tahu & Tempe Crispy", standName, 5000, 40, "5 mnt", null, null, "🍘", standId, "Makanan"),
                MenuItem("m-a4", "Es Teh Manis Jumbo", standName, 4000, 60, "3 mnt", null, null, "🧋", standId, "Minuman")
            )
            standName.contains("Dimsum", true) || standName.contains("Siomay", true) -> listOf(
                MenuItem("m-d1", "Dimsum Ayam Udang (4 Pcs)", standName, 15000, 22, "8 mnt", "Favorit", Color(0xFFFEF3C7), "🥟", standId, "Makanan"),
                MenuItem("m-d2", "Siomay Bandung Bumbu Kacang", standName, 14000, 18, "7 mnt", null, null, "🥟", standId, "Makanan"),
                MenuItem("m-d3", "Pangsit Goreng Mayones", standName, 10000, 25, "5 mnt", null, null, "🥟", standId, "Makanan"),
                MenuItem("m-d4", "Es Lemon Tea Dingin", standName, 6000, 35, "3 mnt", null, null, "🍋", standId, "Minuman")
            )
            standName.contains("Kopi", true) || standName.contains("Teh", true) -> listOf(
                MenuItem("m-c1", "Kopi Susu Gula Aren", standName, 10000, 40, "5 mnt", "Favorit", Color(0xFFFEF3C7), "🧋", standId, "Minuman"),
                MenuItem("m-c2", "Matcha Green Tea Latte", standName, 12000, 25, "5 mnt", null, null, "🍵", standId, "Minuman"),
                MenuItem("m-c3", "Es Coklat Klasik", standName, 10000, 30, "5 mnt", null, null, "🍫", standId, "Minuman"),
                MenuItem("m-c4", "Toast Roti Bakar Coklat Keju", standName, 10000, 20, "10 mnt", null, null, "🍞", standId, "Makanan")
            )
            else -> listOf(
                MenuItem("m-s1", "Nasi Goreng Spesial SMKN 8", standName, 15000, 25, "10 mnt", "Favorit", Color(0xFFFEF3C7), "🍛", standId, "Makanan"),
                MenuItem("m-s2", "Mie Ayam Bakso Komplit", standName, 14000, 20, "10 mnt", null, null, "🍜", standId, "Makanan"),
                MenuItem("m-s3", "Soto Ayam Lamongan", standName, 15000, 15, "12 mnt", null, null, "🍲", standId, "Makanan"),
                MenuItem("m-s4", "Es Jeruk Manis Segar", standName, 5000, 50, "5 mnt", null, null, "🍹", standId, "Minuman")
            )
        }
    }

    var menuList by remember { mutableStateOf<List<MenuItem>>(fallbackMenus) }

    fun classifyCategory(name: String, standCategory: String?): String {
        val n = name.lowercase()
        val isDrink = n.contains("jus") || n.contains("juice") || n.contains("teh") ||
                n.contains("tea") || n.contains("kopi") || n.contains("coffee") ||
                n.contains("es ") || n.contains("boba") || n.contains("drink") ||
                n.contains("air") || n.contains("lemon") || n.contains("latte")
        return if (isDrink) "Minuman" else "Makanan"
    }

    fun loadMenus() {
        isLoading = true
        coroutineScope.launch {
            // 1. Coba ambil berdasarkan standId
            val result = if (standId.isNotBlank()) {
                repository.getMenusByStand(standId)
            } else {
                repository.getAllMenus()
            }

            isLoading = false
            result.onSuccess { res ->
                val apiMenus = res.data ?: emptyList()
                if (apiMenus.isNotEmpty()) {
                    menuList = apiMenus.map { menu ->
                        val cat = classifyCategory(menu.name, menu.stands?.category)
                        MenuItem(
                            id = menu.id,
                            name = menu.name,
                            standName = menu.stands?.name ?: standName,
                            price = menu.price,
                            stock = menu.stock,
                            prepareTime = menu.prepareTime ?: "10 mnt",
                            tag = if (menu.stock > 30) "Tersedia" else null,
                            tagColor = if (menu.stock > 30) Color(0xFFDCFCE7) else null,
                            foodEmoji = when {
                                menu.name.contains("Kebab", true) -> "🥙"
                                menu.name.contains("Ketoprak", true) -> "🍲"
                                menu.name.contains("Ayam", true) -> "🍗"
                                menu.name.contains("Jus", true) || menu.name.contains("Buah", true) -> "🍹"
                                menu.name.contains("Kopi", true) || menu.name.contains("Teh", true) -> "🧋"
                                menu.name.contains("Dimsum", true) -> "🥟"
                                menu.name.contains("Nasi", true) -> "🍛"
                                menu.name.contains("Mie", true) -> "🍜"
                                else -> "🍱"
                            },
                            standId = menu.standId ?: standId,
                            category = cat
                        )
                    }
                } else {
                    // Coba filter dari semua menu jika endpoint getMenusByStand kosong
                    val allMenusRes = repository.getAllMenus()
                    allMenusRes.onSuccess { allRes ->
                        val matching = allRes.data?.filter { m ->
                            m.standId == standId || 
                            m.stands?.id == standId || 
                            m.stands?.name?.contains(standName, ignoreCase = true) == true
                        } ?: emptyList()

                        if (matching.isNotEmpty()) {
                            menuList = matching.map { menu ->
                                MenuItem(
                                    id = menu.id,
                                    name = menu.name,
                                    standName = menu.stands?.name ?: standName,
                                    price = menu.price,
                                    stock = menu.stock,
                                    prepareTime = menu.prepareTime ?: "10 mnt",
                                    tag = null,
                                    tagColor = null,
                                    foodEmoji = "🍱",
                                    standId = menu.standId ?: standId,
                                    category = classifyCategory(menu.name, menu.stands?.category)
                                )
                            }
                        } else {
                            menuList = fallbackMenus
                        }
                    }.onFailure {
                        menuList = fallbackMenus
                    }
                }
            }.onFailure {
                menuList = fallbackMenus
            }
        }
    }

    LaunchedEffect(standId) {
        loadMenus()
    }

    val filteredMenus = remember(searchQuery, selectedCategory, menuList) {
        menuList.filter { item ->
            val matchSearch = searchQuery.isBlank() || 
                    item.name.contains(searchQuery, ignoreCase = true) || 
                    item.category.contains(searchQuery, ignoreCase = true)
            val matchCategory = selectedCategory == "Semua" || 
                    item.category.equals(selectedCategory, ignoreCase = true)
            matchSearch && matchCategory
        }
    }

    val cartUiState by cartViewModel.uiState.collectAsState()
    val cartCount = cartUiState.totalMenuCount
    val cartTotal = cartUiState.subtotal

    Scaffold(
        containerColor = ScreenBg,
        topBar = {
            // Top Bar Navigation Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Back Button
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(1.dp, BorderColor, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Kembali",
                        tint = TextPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Title Stand
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "DAFTAR MENU STAND",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = BluePrimary,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = standName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                // Stand Icon Badge
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(BlueLightBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Storefront,
                        contentDescription = "Stand",
                        tint = BluePrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(4.dp))

                    // 1. Stand Profile Hero Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, BorderColor),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(Color(0xFFEFF6FF)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = standEmoji, fontSize = 40.sp)
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = standName,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = standLocation,
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(7.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF059669))
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        text = "Buka • Siap Melayani Pre-Order",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF059669)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 2. Search Bar
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Cari menu di ${standName}...", fontSize = 13.sp, color = TextMuted) },
                        leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = TextSecondary) },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = InputBg,
                            unfocusedContainerColor = InputBg,
                            focusedBorderColor = BluePrimary,
                            unfocusedBorderColor = BorderColor
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // 3. Category Filter Chips
                    val categories = listOf("Semua", "Makanan", "Minuman")
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(filterScrollState),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        categories.forEach { cat ->
                            val isSelected = selectedCategory == cat
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(if (isSelected) BluePrimary else Color.White)
                                    .border(1.dp, if (isSelected) BluePrimary else BorderColor, RoundedCornerShape(20.dp))
                                    .clickable { selectedCategory = cat }
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    when (cat) {
                                        "Semua" -> Text(text = "🍽️ ", fontSize = 12.sp)
                                        "Makanan" -> Text(text = "🍱 ", fontSize = 12.sp)
                                        "Minuman" -> Text(text = "🍹 ", fontSize = 12.sp)
                                    }
                                    Text(
                                        text = cat,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) Color.White else TextPrimary
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 4. Header Menu List
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Menu Tersedia (${filteredMenus.size})",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = BluePrimary
                            )
                        } else {
                            IconButton(onClick = { loadMenus() }, modifier = Modifier.size(28.dp)) {
                                Icon(imageVector = Icons.Default.Refresh, contentDescription = "Refresh", tint = BluePrimary, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }

                // 5. Menu Items
                if (filteredMenus.isEmpty() && !isLoading) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "🍽️", fontSize = 48.sp)
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Tidak ada menu yang sesuai",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Coba gunakan kata kunci pencarian yang lain",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                } else {
                    items(filteredMenus, key = { it.id }) { menu ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, BorderColor),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Food Thumbnail Emoji
                                Box(
                                    modifier = Modifier
                                        .size(68.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(Color(0xFFEFF6FF)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = menu.foodEmoji, fontSize = 34.sp)
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                // Details
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = menu.name,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                        menu.tag?.let { tag ->
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(menu.tagColor ?: Color(0xFFFEF3C7))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = tag,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFFB45309)
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(3.dp))
                                    Text(
                                        text = "⏱️ ${menu.prepareTime} • Tersisa: ${menu.stock} porsi",
                                        fontSize = 11.sp,
                                        color = TextMuted
                                    )

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Rp ${String.format(Locale.GERMANY, "%,d", menu.price)}",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = BluePrimary
                                        )

                                        Button(
                                            onClick = {
                                                val foodType = when {
                                                    menu.name.contains("Kebab", true) -> FoodImageType.KEBAB
                                                    menu.name.contains("Ketoprak", true) -> FoodImageType.KETOPRAK
                                                    menu.name.contains("Ayam", true) -> FoodImageType.AYAM_GEPREK
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
                                                        standName = standName,
                                                        standId = menu.standId ?: standId,
                                                        imageType = foodType,
                                                        foodEmoji = menu.foodEmoji
                                                    )
                                                )
                                                Toast.makeText(context, "${menu.name} ditambahkan ke keranjang!", Toast.LENGTH_SHORT).show()
                                            },
                                            shape = RoundedCornerShape(16.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                                            modifier = Modifier.height(34.dp)
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.Add,
                                                    contentDescription = "Pesan",
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(text = "Pesan", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(84.dp))
                }
            }

            // 6. Floating Cart Bar
            AnimatedVisibility(
                visible = cartCount > 0,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp, start = 16.dp, end = 16.dp),
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
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ShoppingBag,
                                    contentDescription = "Keranjang",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column {
                                Text(
                                    text = "$cartCount Menu Dipilih",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Rp ${String.format(Locale.GERMANY, "%,d", cartTotal.toInt())}",
                                    color = Color.White.copy(alpha = 0.9f),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }

                        Button(
                            onClick = onCheckoutClick,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = BluePrimary
                            ),
                            shape = RoundedCornerShape(16.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                            modifier = Modifier.height(38.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Bayar Sekarang",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BluePrimary
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = "Bayar",
                                    tint = BluePrimary,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
