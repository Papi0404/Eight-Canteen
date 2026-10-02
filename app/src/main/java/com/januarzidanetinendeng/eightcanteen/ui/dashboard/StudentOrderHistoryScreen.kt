package com.januarzidanetinendeng.eightcanteen.ui.dashboard

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.januarzidanetinendeng.eightcanteen.data.remote.OrderResponse
import com.januarzidanetinendeng.eightcanteen.data.repository.CanteenRepository
import com.januarzidanetinendeng.eightcanteen.ui.checkout.formatRupiah
import com.januarzidanetinendeng.eightcanteen.ui.theme.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentOrderHistoryScreen(
    onBackClick: () -> Unit = {},
    onOrderClick: (orderId: String) -> Unit = {},
    onOrderNewFoodClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val repository = remember { CanteenRepository() }

    var orders by remember { mutableStateOf<List<OrderResponse>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var isRefreshing by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf("Semua") }
    var orderToCancel by remember { mutableStateOf<OrderResponse?>(null) }

    fun fetchOrders(isPullRefresh: Boolean = false) {
        if (isPullRefresh) isRefreshing = true else isLoading = true
        coroutineScope.launch {
            try {
                repository.getOrders().onSuccess { res ->
                    orders = res.data ?: emptyList()
                }.onFailure { err ->
                    Toast.makeText(context, "Gagal memuat pesanan: ${err.message}", Toast.LENGTH_SHORT).show()
                }
            } finally {
                isLoading = false
                isRefreshing = false
            }
        }
    }

    LaunchedEffect(Unit) {
        fetchOrders()
    }

    val filteredOrders = remember(orders, selectedFilter) {
        when (selectedFilter) {
            "Aktif" -> orders.filter { it.status != "COMPLETED" && it.status != "CANCELLED" }
            "Selesai" -> orders.filter { it.status == "COMPLETED" }
            else -> orders
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ScreenBg)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
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

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Riwayat Pesanan",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Daftar pesanan aktif & riwayat makanan",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }

            IconButton(
                onClick = { fetchOrders() },
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(1.dp, BorderColor, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Muat Ulang",
                    tint = BluePrimary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // Filter Tabs
        val filterOptions = listOf("Semua", "Aktif", "Selesai")
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filterOptions) { filter ->
                val isSelected = selectedFilter == filter
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSelected) BluePrimary else Color.White)
                        .border(1.dp, if (isSelected) BluePrimary else BorderColor, RoundedCornerShape(20.dp))
                        .clickable { selectedFilter = filter }
                        .padding(horizontal = 16.dp, vertical = 7.dp)
                ) {
                    Text(
                        text = filter,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color.White else TextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Content with Pull to Refresh
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { fetchOrders(isPullRefresh = true) },
            modifier = Modifier.fillMaxSize()
        ) {
            if (isLoading && !isRefreshing) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = BluePrimary, modifier = Modifier.size(36.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(text = "Memuat riwayat pesanan...", fontSize = 13.sp, color = TextSecondary)
                    }
                }
            } else if (filteredOrders.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = "🍽️", fontSize = 54.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (selectedFilter == "Aktif") "Tidak Ada Pesanan Aktif" else "Belum Ada Riwayat Pesanan",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Yuk jelajahi stand kantin SMKN 8 dan pesan makanan favoritmu sekarang!",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onOrderNewFoodClick,
                            colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(text = "Pesan Makanan", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredOrders) { order ->
                        StudentOrderCard(
                            order = order,
                            onViewDetail = { onOrderClick(order.id) },
                            onCancelClick = { orderToCancel = order }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(30.dp))
                    }
                }
            }
        }
    }

    // Dialog Konfirmasi Pembatalan Pesanan
    if (orderToCancel != null) {
        val targetOrder = orderToCancel!!
        AlertDialog(
            onDismissRequest = { orderToCancel = null },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color.White,
            title = {
                Text(
                    text = "Batalkan Pesanan #${targetOrder.orderNumber}?",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            },
            text = {
                Text(
                    text = "Apakah Anda yakin ingin membatalkan pesanan ini? Stok menu akan dikembalikan ke stand dan poin yang digunakan akan dikembalikan ke akun Anda.",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val idToCancel = targetOrder.id
                        orderToCancel = null
                        coroutineScope.launch {
                            repository.cancelOrder(idToCancel).onSuccess {
                                Toast.makeText(context, "Pesanan #${targetOrder.orderNumber} berhasil dibatalkan!", Toast.LENGTH_SHORT).show()
                                fetchOrders()
                            }.onFailure { err ->
                                Toast.makeText(context, "Gagal membatalkan pesanan: ${err.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Ya, Batalkan", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { orderToCancel = null }) {
                    Text("Kembali", fontSize = 12.sp, color = TextSecondary)
                }
            }
        )
    }
}

@Composable
fun StudentOrderCard(
    order: OrderResponse,
    onViewDetail: () -> Unit,
    onCancelClick: () -> Unit = {}
) {
    val standName = order.stands?.name ?: order.standName ?: "Stand Kantin"
    val isCash = order.paymentMethod?.contains("TUNAI", ignoreCase = true) == true ||
            order.paymentMethod?.contains("CASH", ignoreCase = true) == true
    val isCompleted = order.status == "COMPLETED"
    val isPaid = if (isCash) isCompleted else (order.paymentStatus == "PAID" || order.status == "PAID" || isCompleted)

    // Status Badge Info
    val (statusLabel, statusBg, statusColor) = when {
        isCompleted -> Triple("Selesai (Diambil)", Color(0xFFDCFCE7), Color(0xFF15803D))
        order.status == "READY" -> Triple("Siap Diambil", Color(0xFFDCFCE7), Color(0xFF15803D))
        order.status == "COOKING" || order.status == "PAID" -> Triple("Sedang Disiapkan", Color(0xFFDBEAFE), Color(0xFF1E40AF))
        !isPaid && isCash -> Triple("Menunggu Bayar di Kasir", Color(0xFFFEF3C7), Color(0xFFB45309))
        order.status == "CANCELLED" -> Triple("Dibatalkan", Color(0xFFFEE2E2), Color(0xFFB91C1C))
        else -> Triple("Menunggu Konfirmasi", Color(0xFFFEF3C7), Color(0xFFB45309))
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, BorderColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Stand & Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Storefront,
                        contentDescription = "Stand",
                        tint = BluePrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = standName,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(statusBg)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = statusLabel,
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Order Number & Date
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "#${order.orderNumber}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = BluePrimary,
                    letterSpacing = 0.5.sp
                )

                val formattedDate = remember(order.createdAt) {
                    order.createdAt?.take(16)?.replace("T", ", ") ?: "Hari ini"
                }
                Text(
                    text = formattedDate,
                    fontSize = 11.sp,
                    color = TextMuted
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(BorderColor))
            Spacer(modifier = Modifier.height(10.dp))

            // Menu Items Summary
            val items = order.orderItems
            if (items.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    items.forEach { item ->
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${item.quantity}x ${item.name}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary
                                )
                                if (item.price > 0) {
                                    Text(
                                        text = "Rp ${formatRupiah((item.price * item.quantity).toDouble())}",
                                        fontSize = 12.sp,
                                        color = TextSecondary
                                    )
                                }
                            }
                            if (!item.note.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "📝 Catatan: ${item.note}",
                                    fontSize = 10.5.sp,
                                    color = Color(0xFFB45309),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    if (!order.note.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFFEF3C7))
                                .border(0.8.dp, Color(0xFFFDE68A), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.Top) {
                                Text(text = "📌 ", fontSize = 11.sp)
                                Text(
                                    text = "Catatan: ${order.note}",
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF92400E)
                                )
                            }
                        }
                    }
                }
            } else {
                Text(
                    text = "Pesanan Makanan SMKN 8",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Footer: Total & View Struk Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (isCash) "Tunai di Stand" else "QRIS Dinamis",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isCash) Color(0xFFD97706) else BluePrimary
                    )
                    Text(
                        text = "Rp ${formatRupiah(order.totalAmount.toDouble())}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary
                    )
                }

                val canCancel = order.status != "COOKING" && order.status != "COMPLETED" && order.status != "CANCELLED" && order.status != "READY"

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (canCancel) {
                        OutlinedButton(
                            onClick = onCancelClick,
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, Color(0xFFFCA5A5)),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color(0xFFDC2626)
                            ),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Text(
                                text = "Batalkan",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFDC2626)
                            )
                        }
                    }

                    Button(
                        onClick = onViewDetail,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isCompleted) Color(0xFFF1F5F9) else BlueLightBg,
                            contentColor = BluePrimary
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.QrCode,
                                contentDescription = "Barcode",
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Lihat Barcode",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
