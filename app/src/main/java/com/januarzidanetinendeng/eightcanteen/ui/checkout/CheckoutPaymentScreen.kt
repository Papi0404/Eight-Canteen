package com.januarzidanetinendeng.eightcanteen.ui.checkout

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.januarzidanetinendeng.eightcanteen.data.local.SessionManager
import com.januarzidanetinendeng.eightcanteen.data.remote.OrderItemRequest
import com.januarzidanetinendeng.eightcanteen.data.repository.CanteenRepository
import com.januarzidanetinendeng.eightcanteen.ui.theme.BlueLightBg
import com.januarzidanetinendeng.eightcanteen.ui.theme.BluePrimary
import com.januarzidanetinendeng.eightcanteen.ui.theme.BorderColor
import com.januarzidanetinendeng.eightcanteen.ui.theme.InputBg
import com.januarzidanetinendeng.eightcanteen.ui.theme.ScreenBg
import com.januarzidanetinendeng.eightcanteen.ui.theme.TextMuted
import com.januarzidanetinendeng.eightcanteen.ui.theme.TextPrimary
import com.januarzidanetinendeng.eightcanteen.ui.theme.TextSecondary
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun CheckoutPaymentScreen(
    cartViewModel: CartViewModel = remember { CartViewModel() },
    onBackClick: () -> Unit = {},
    onPaymentSuccess: (method: String, orderId: String) -> Unit = { _, _ -> }
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    var selectedMethod by remember { mutableStateOf("TUNAI") } // Default to TUNAI (QRIS temporarily disabled)
    var isLoading by remember { mutableStateOf(false) }
    var userPoints by remember { mutableIntStateOf(SessionManager.getInstance(context).getPoints()) }
    var usePoints by remember { mutableStateOf(false) }

    // Dialog States for Cart CRUD & Item Notes
    var editingNoteItem by remember { mutableStateOf<CartItem?>(null) }
    var currentNoteInput by remember { mutableStateOf("") }
    var orderNoteInput by remember { mutableStateOf("") }
    var showClearCartConfirm by remember { mutableStateOf(false) }
    var itemToDelete by remember { mutableStateOf<CartItem?>(null) }

    LaunchedEffect(Unit) {
        val repo = CanteenRepository()
        repo.getMyProfile().onSuccess { res ->
            res.data?.points?.let { pts ->
                userPoints = pts
                SessionManager.getInstance(context).updatePoints(pts)
            }
        }
    }

    val cartUiState by cartViewModel.uiState.collectAsState()
    val items = cartUiState.cartItems
    val subtotal = cartUiState.subtotal
    val adminFee = if (selectedMethod == "QRIS") 1000 else 0
    val canRedeemPoints = userPoints >= 20
    val pointsDiscount = if (usePoints && canRedeemPoints) 2000 else 0
    val totalPayment = (subtotal + adminFee - pointsDiscount).coerceAtLeast(0.0)

    Scaffold(
        containerColor = ScreenBg,
        topBar = {
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

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Keranjang & Pembayaran",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = if (items.isEmpty()) "Kelola menu pesanan kantin" else "${items.size} jenis menu di keranjang",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }

                if (items.isNotEmpty()) {
                    TextButton(
                        onClick = { showClearCartConfirm = true },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Kosongkan",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFEF4444)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        if (items.isEmpty()) {
            // Empty Cart State
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, BorderColor)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 36.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(76.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFEFF6FF)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = "Keranjang Kosong",
                                tint = BluePrimary,
                                modifier = Modifier.size(38.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        Text(
                            text = "Keranjang Belanja Kosong",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Anda belum memilih menu dari stand kantin. Silakan pilih menu makanan atau minuman favorit Anda.",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = onBackClick,
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                        ) {
                            Text(
                                text = "Jelajahi Menu Kantin",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // 1. Alert Banner Pick up time
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(BluePrimary)
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color.White.copy(alpha = 0.2f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "JADWAL AMBIL PESANAN",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Istirahat 1 (10:15 - 10:30 WIB)",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Stand Kantin SMKN 8 Jakarta",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🍱", fontSize = 20.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                val distinctStands = items.mapNotNull { it.standName.ifBlank { null } ?: it.standId }.distinct()
                if (distinctStands.size > 1) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 14.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
                        border = BorderStroke(1.dp, Color(0xFFBFDBFE))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("🏪", fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Pesanan dari ${distinctStands.size} Stand Berbeda",
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E40AF)
                                )
                                Text(
                                    text = "Sistem otomatis menerbitkan tiket QR terpisah untuk tiap stand agar dapat discan langsung oleh masing-masing penjual.",
                                    fontSize = 11.sp,
                                    color = Color(0xFF1E3A8A),
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }
                }

                // 2. Daftar Menu & Manajemen Keranjang (CRUD & Notes)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
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
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ReceiptLong,
                                    contentDescription = "Receipt",
                                    tint = BluePrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Rincian Menu",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(InputBg)
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "${items.sumOf { it.quantity }} Porsi (${items.size} Menu)",
                                    fontSize = 10.sp,
                                    color = TextSecondary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        items.forEachIndexed { index, item ->
                            if (index > 0) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 12.dp),
                                    color = BorderColor,
                                    thickness = 0.8.dp
                                )
                            }

                            Column(modifier = Modifier.fillMaxWidth()) {
                                // Item Header Row
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(52.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Color(0xFFFEF3C7)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = item.foodEmoji, fontSize = 26.sp)
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = item.name,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary,
                                            maxLines = 1
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "${item.standName} • Rp ${String.format(Locale.GERMANY, "%,d", item.price.toLong())} / porsi",
                                            fontSize = 11.sp,
                                            color = TextSecondary
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Text(
                                        text = "Rp ${String.format(Locale.GERMANY, "%,d", (item.price * item.quantity).toLong())}",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = BluePrimary
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Item Controls: Quantity (+ / -), Delete, and Per-Item Note
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Quantity Stepper
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(Color(0xFFF8FAFC))
                                            .border(1.dp, BorderColor, RoundedCornerShape(10.dp))
                                            .padding(horizontal = 4.dp, vertical = 2.dp)
                                    ) {
                                        IconButton(
                                            onClick = {
                                                if (item.quantity > 1) {
                                                    cartViewModel.updateQuantity(item.id, item.quantity - 1)
                                                } else {
                                                    itemToDelete = item
                                                }
                                            },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Remove,
                                                contentDescription = "Kurang",
                                                tint = TextPrimary,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }

                                        Text(
                                            text = "${item.quantity}",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary,
                                            modifier = Modifier.padding(horizontal = 6.dp)
                                        )

                                        IconButton(
                                            onClick = {
                                                cartViewModel.updateQuantity(item.id, item.quantity + 1)
                                            },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Add,
                                                contentDescription = "Tambah",
                                                tint = TextPrimary,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(6.dp))

                                    // Delete Item Button
                                    IconButton(
                                        onClick = { itemToDelete = item },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Hapus Menu",
                                            tint = Color(0xFFEF4444),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.weight(1f))

                                    // Per-Item Note Button / Pill
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(if (item.note.isNotBlank()) Color(0xFFFEF3C7) else Color(0xFFEFF6FF))
                                            .border(
                                                1.dp,
                                                if (item.note.isNotBlank()) Color(0xFFFDE68A) else BluePrimary.copy(alpha = 0.3f),
                                                RoundedCornerShape(10.dp)
                                            )
                                            .clickable {
                                                editingNoteItem = item
                                                currentNoteInput = item.note
                                            }
                                            .padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = if (item.note.isNotBlank()) "📝 ${item.note.take(16)}${if (item.note.length > 16) "..." else ""}" else "✏️ + Catatan",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = if (item.note.isNotBlank()) Color(0xFFB45309) else BluePrimary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Catatan Pesanan (Order-Level Note)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, BorderColor)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "📌", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Catatan Pesanan untuk Penjual (Opsional)",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = orderNoteInput,
                            onValueChange = { orderNoteInput = it },
                            placeholder = {
                                Text(
                                    text = "Contoh: Tolong jangan pakai sendok plastik ya bang",
                                    fontSize = 12.sp,
                                    color = TextMuted
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = BluePrimary,
                                unfocusedBorderColor = BorderColor,
                                focusedContainerColor = InputBg,
                                unfocusedContainerColor = InputBg
                            ),
                            maxLines = 3
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 3. Metode Pembayaran
                Text(
                    text = "Metode Pembayaran",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(10.dp))

                // QRIS Option (Dinonaktifkan Sementara: "Belum Tersedia")
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            Toast.makeText(context, "Metode pembayaran QRIS belum tersedia saat ini", Toast.LENGTH_SHORT).show()
                        },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCode,
                            contentDescription = "QRIS",
                            tint = TextMuted,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "QRIS Bebas Admin",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextMuted
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFFFEE2E2))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "Belum Tersedia",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFDC2626)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Gopay, Dana, ShopeePay, M-Banking",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }
                        // Disabled radio circle
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .border(2.dp, Color(0xFFCBD5E1), CircleShape)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Tunai Option
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedMethod = "TUNAI" },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = if (selectedMethod == "TUNAI") Color(0xFFF0FDF4) else Color.White),
                    border = BorderStroke(1.5.dp, if (selectedMethod == "TUNAI") Color(0xFF16A34A) else BorderColor)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Money,
                            contentDescription = "Tunai",
                            tint = if (selectedMethod == "TUNAI") Color(0xFF16A34A) else TextSecondary,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Bayar Tunai di Stand",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Tunjukkan struk barcode & bayar pas di loket",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .border(2.dp, if (selectedMethod == "TUNAI") Color(0xFF16A34A) else BorderColor, CircleShape)
                                .background(if (selectedMethod == "TUNAI") Color(0xFF16A34A) else Color.Transparent),
                            contentAlignment = Alignment.Center
                        ) {
                            if (selectedMethod == "TUNAI") {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(Color.White)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Tukar Poin Reward Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, BorderColor)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFEF3C7)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🪙", fontSize = 18.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Gunakan Poin Diskon",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = if (canRedeemPoints) "Tukar 20 Poin = Diskon Rp 2.000 (Milikmu: $userPoints poin)" else "Poin Anda: $userPoints (Min. 20 poin untuk diskon)",
                                fontSize = 11.sp,
                                color = if (canRedeemPoints) Color(0xFF16A34A) else TextMuted
                            )
                        }
                        Switch(
                            checked = usePoints && canRedeemPoints,
                            onCheckedChange = { checked ->
                                if (!canRedeemPoints) {
                                    Toast.makeText(context, "Poin Anda belum cukup (minimal 20 poin)", Toast.LENGTH_SHORT).show()
                                } else {
                                    usePoints = checked
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

                Spacer(modifier = Modifier.height(20.dp))

                // 4. Ringkasan Tagihan
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, BorderColor)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Ringkasan Pembayaran",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "Total Harga (${items.sumOf { it.quantity }} Porsi)", fontSize = 12.sp, color = TextSecondary)
                            Text(text = "Rp ${String.format(Locale.GERMANY, "%,d", subtotal.toLong())}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "Biaya Layanan Koperasi", fontSize = 12.sp, color = TextSecondary)
                            Text(text = if (adminFee > 0) "Rp ${String.format(Locale.GERMANY, "%,d", adminFee.toLong())}" else "Gratis", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (adminFee > 0) TextPrimary else Color(0xFF16A34A))
                        }

                        if (pointsDiscount > 0) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text = "Diskon Tukar Poin (20 Poin)", fontSize = 12.sp, color = Color(0xFF16A34A), fontWeight = FontWeight.SemiBold)
                                Text(text = "-Rp ${String.format(Locale.GERMANY, "%,d", pointsDiscount.toLong())}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF16A34A))
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = BorderColor, thickness = 1.dp)
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "Total Tagihan", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text(text = "Rp ${String.format(Locale.GERMANY, "%,d", totalPayment.toLong())}", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = BluePrimary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 5. Checkout Button
                Button(
                    onClick = {
                        if (items.isEmpty()) {
                            Toast.makeText(context, "Keranjang Kosong!", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        if (selectedMethod == "QRIS") {
                            Toast.makeText(context, "Metode pembayaran QRIS belum tersedia. Silakan pilih Tunai.", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        isLoading = true
                        coroutineScope.launch {
                            val repo = CanteenRepository()
                            val standIdToUse = items.firstOrNull { !it.standId.isNullOrBlank() }?.standId ?: ""
                            val orderItems = items.map {
                                OrderItemRequest(
                                    menuId = it.id,
                                    quantity = it.quantity,
                                    note = it.note.ifBlank { null }
                                )
                            }
                            val aggregatedNotes = items.filter { it.note.isNotBlank() }
                                .joinToString("; ") { "${it.name}: ${it.note}" }
                                .ifBlank { null }
                            val finalOrderNote = orderNoteInput.trim().ifBlank { aggregatedNotes }

                            val isRedeeming = usePoints && canRedeemPoints
                            repo.createOrder(
                                standId = standIdToUse,
                                items = orderItems,
                                paymentMethod = selectedMethod,
                                usePoints = isRedeeming,
                                note = finalOrderNote
                            ).onSuccess { res ->
                                isLoading = false
                                val order = res.data
                                val allCreated = order?.allOrders ?: listOfNotNull(order)
                                val orderIds = allCreated.map { it.id }.filter { it.isNotBlank() }
                                val combinedId = if (orderIds.isNotEmpty()) orderIds.joinToString(",") else (order?.id ?: "")

                                if (isRedeeming) {
                                    val remainingPoints = (userPoints - 20).coerceAtLeast(0)
                                    userPoints = remainingPoints
                                    SessionManager.getInstance(context).updatePoints(remainingPoints)
                                }

                                if (allCreated.size > 1) {
                                    Toast.makeText(
                                        context,
                                        "${allCreated.size} Pesanan berhasil dibuat untuk masing-masing stand!",
                                        Toast.LENGTH_LONG
                                    ).show()
                                } else {
                                    val orderNum = order?.orderNumber ?: ""
                                    Toast.makeText(context, "Pesanan #$orderNum Berhasil Dibuat!", Toast.LENGTH_LONG).show()
                                }
                                onPaymentSuccess(selectedMethod, combinedId)
                            }.onFailure { err ->
                                isLoading = false
                                val errorMsg = err.message ?: "Gagal memproses pesanan ke server"
                                Toast.makeText(context, "Gagal Checkout: $errorMsg", Toast.LENGTH_LONG).show()
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    enabled = !isLoading && items.isNotEmpty(),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BluePrimary,
                        disabledContainerColor = Color(0xFFCBD5E1),
                        disabledContentColor = Color(0xFF64748B)
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(22.dp),
                            strokeWidth = 2.5.dp
                        )
                    } else {
                        Text(text = "Bayar Sekarang", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }

    // Dialog: Edit Note for Item
    if (editingNoteItem != null) {
        val targetItem = editingNoteItem!!
        AlertDialog(
            onDismissRequest = { editingNoteItem = null },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color.White,
            title = {
                Text(
                    text = "📝 Catatan Pesanan",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Menu: ${targetItem.name}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = BluePrimary
                    )
                    Text(
                        text = "Tambahkan catatan untuk penjual stand (misal: pedas, tanpa es batu, pisahkan kuah):",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )

                    OutlinedTextField(
                        value = currentNoteInput,
                        onValueChange = { currentNoteInput = it },
                        placeholder = { Text("Ketik catatan di sini...", fontSize = 12.sp, color = TextMuted) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        maxLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BluePrimary,
                            unfocusedBorderColor = BorderColor
                        )
                    )

                    // Quick suggestion chips
                    Text(
                        text = "Pilihan Cepat:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary
                    )
                    val suggestions = listOf("Pedas", "Tidak Pedas", "Sedang", "Tanpa Es", "Es Sedikit", "Bungkus Terpisah")
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        suggestions.forEach { chip ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFEFF6FF))
                                    .border(1.dp, BluePrimary.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                    .clickable {
                                        if (currentNoteInput.isBlank()) {
                                            currentNoteInput = chip
                                        } else if (!currentNoteInput.contains(chip, ignoreCase = true)) {
                                            currentNoteInput = "$currentNoteInput, $chip"
                                        }
                                    }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(text = "+ $chip", fontSize = 11.sp, color = BluePrimary, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        cartViewModel.updateItemNote(targetItem.id, currentNoteInput)
                        editingNoteItem = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Simpan", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { editingNoteItem = null }) {
                    Text("Batal", fontSize = 12.sp, color = TextSecondary)
                }
            }
        )
    }

    // Dialog: Confirm Clear Cart
    if (showClearCartConfirm) {
        AlertDialog(
            onDismissRequest = { showClearCartConfirm = false },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color.White,
            title = {
                Text(
                    text = "Kosongkan Keranjang?",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            },
            text = {
                Text(
                    text = "Semua menu yang telah dipilih akan dihapus dari keranjang belanja Anda.",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        cartViewModel.clearCart()
                        showClearCartConfirm = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Kosongkan", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearCartConfirm = false }) {
                    Text("Batal", fontSize = 12.sp, color = TextSecondary)
                }
            }
        )
    }

    // Dialog: Confirm Delete Single Item
    if (itemToDelete != null) {
        val target = itemToDelete!!
        AlertDialog(
            onDismissRequest = { itemToDelete = null },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color.White,
            title = {
                Text(
                    text = "Hapus Menu?",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            },
            text = {
                Text(
                    text = "Hapus \"${target.name}\" dari keranjang belanja?",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        cartViewModel.removeItem(target.id)
                        itemToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Hapus", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { itemToDelete = null }) {
                    Text("Batal", fontSize = 12.sp, color = TextSecondary)
                }
            }
        )
    }
}
