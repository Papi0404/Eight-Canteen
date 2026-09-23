package com.januarzidanetinendeng.eightcanteen.ui.checkout

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.MonetizationOn
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material.icons.outlined.Stars
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.januarzidanetinendeng.eightcanteen.ui.components.EKantinLogoIcon
import com.januarzidanetinendeng.eightcanteen.ui.theme.EightCanteenTheme
import java.text.NumberFormat
import java.util.Locale

fun formatRupiah(amount: Int): String {
    val formatter = NumberFormat.getIntegerInstance(Locale("id", "ID"))
    return formatter.format(amount)
}

@Composable
fun CheckoutScreen(
    viewModel: CheckoutViewModel = remember { CheckoutViewModel() },
    onBackClick: () -> Unit = {},
    onPaymentComplete: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var showEditNoteDialog by remember { mutableStateOf(false) }
    var tempNoteText by remember { mutableStateOf(state.orderNote) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CheckoutTopAppBar(
                onBackClick = onBackClick,
                onHelpClick = {
                    Toast.makeText(
                        context,
                        "Panduan Pre-Order: Pesanan diproses otomatis dan siap diambil tepat di jam istirahat.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            )
        },
        bottomBar = {
            CheckoutBottomBar(
                totalBill = state.totalBill,
                selectedMethodName = state.selectedPaymentMethodName,
                onPayClick = {
                    viewModel.processPayment()
                    showSuccessDialog = true
                }
            )
        },
        containerColor = Color(0xFFF6F8FC)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Time Slot Pickup Card ("WAKTU AMBIL TEPAT")
            PickupTimeSlotCard(
                timeSlotTitle = state.timeSlotTitle,
                standInfo = state.standInfo
            )

            // 2. Order Details List ("Rincian Pesanan")
            OrderDetailsCard(
                cartItems = state.cartItems,
                totalMenuCount = state.totalMenuCount,
                orderNote = state.orderNote,
                onEditNoteClick = {
                    tempNoteText = state.orderNote
                    showEditNoteDialog = true
                },
                onQuantityChange = { itemId, delta ->
                    viewModel.updateItemQuantity(itemId, delta)
                }
            )

            // 3. Loyalty Points Toggle ("Fitur Tukar Poin")
            PointsRewardToggleCard(
                userPoints = state.userPoints,
                redeemPointsCost = state.redeemPointsCost,
                discountAmount = state.discountAmount,
                isEnabled = state.isPointsDiscountEnabled,
                onToggleChange = { enabled ->
                    viewModel.togglePointsDiscount(enabled)
                }
            )

            // 4. Payment Method Selection ("Metode Pembayaran")
            PaymentMethodSelectionCard(
                selectedMethod = state.selectedPaymentMethod,
                onMethodSelected = { method ->
                    viewModel.selectPaymentMethod(method)
                }
            )

            // 5. Payment Summary ("Rincian Pembayaran")
            PaymentSummaryCard(
                subtotal = state.subtotal,
                menuCount = state.totalMenuCount,
                serviceFee = state.serviceFee,
                discountAmount = state.actualDiscount,
                isDiscountActive = state.isPointsDiscountEnabled,
                totalBill = state.totalBill
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Dialog Edit Catatan Pesanan
    if (showEditNoteDialog) {
        AlertDialog(
            onDismissRequest = { showEditNoteDialog = false },
            title = {
                Text(
                    text = "Catatan Pesanan",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Tambahkan instruksi khusus untuk penjual (misal: tingkat kepedasan, saus dipisah, es sedikit).",
                        fontSize = 13.sp,
                        color = Color(0xFF64748B)
                    )
                    OutlinedTextField(
                        value = tempNoteText,
                        onValueChange = { tempNoteText = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Tulis catatan di sini...") },
                        maxLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateOrderNote(tempNoteText)
                        showEditNoteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0052CC))
                ) {
                    Text("Simpan", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditNoteDialog = false }) {
                    Text("Batal", color = Color(0xFF64748B))
                }
            }
        )
    }

    // Dialog Pembayaran Berhasil / Bukti Transaksi
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF22C55E)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Success",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Text(
                        text = "Pesanan Diterima!",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Pesanan Anda senilai Rp ${formatRupiah(state.totalBill)} berhasil diproses dengan metode ${state.selectedPaymentMethodName}.",
                        fontSize = 14.sp,
                        color = Color(0xFF334155)
                    )
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "LOKET PENGAMBILAN",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E40AF)
                            )
                            Text(
                                text = state.standInfo,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                            Text(
                                text = "Waktu: ${state.timeSlotTitle}",
                                fontSize = 12.sp,
                                color = Color(0xFF475569)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        onPaymentComplete()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0052CC)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Kembali ke Beranda", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

// -----------------------------------------------------------------------------
// Header App Bar
// -----------------------------------------------------------------------------
@Composable
private fun CheckoutTopAppBar(
    onBackClick: () -> Unit,
    onHelpClick: () -> Unit
) {
    SurfaceHeaderContainer {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Kembali",
                        tint = Color(0xFF0F172A)
                    )
                }

                EKantinLogoIcon(size = 38.dp)

                Column {
                    Text(
                        text = "E-KANTIN 8",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF0052CC),
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "Checkout & Metode...",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            IconButton(
                onClick = onHelpClick,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.HelpOutline,
                    contentDescription = "Bantuan / Info",
                    tint = Color(0xFF475569),
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

@Composable
private fun SurfaceHeaderContainer(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .shadow(2.dp)
    ) {
        content()
    }
}

// -----------------------------------------------------------------------------
// 1. Time Slot Card ("WAKTU AMBIL TEPAT")
// -----------------------------------------------------------------------------
@Composable
private fun PickupTimeSlotCard(
    timeSlotTitle: String,
    standInfo: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFDDE8FF))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Badge Waktu Ambil Tepat
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color(0xFF0052CC))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.AccessTime,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "WAKTU AMBIL TEPAT",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                // Timer icon in white circle
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Timer,
                        contentDescription = null,
                        tint = Color(0xFF0052CC),
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            // Main Time Text
            Text(
                text = timeSlotTitle,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
            )

            // Stand Name Subtext
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Storefront,
                    contentDescription = null,
                    tint = Color(0xFF0052CC),
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = standInfo,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF0040A8)
                )
            }
        }
    }
}

// -----------------------------------------------------------------------------
// 2. Order Details Section ("Rincian Pesanan")
// -----------------------------------------------------------------------------
@Composable
private fun OrderDetailsCard(
    cartItems: List<CartItem>,
    totalMenuCount: Int,
    orderNote: String,
    onEditNoteClick: () -> Unit,
    onQuantityChange: (String, Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ReceiptLong,
                        contentDescription = null,
                        tint = Color(0xFF0052CC),
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Rincian Pesanan",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                }

                // Total menu count badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF1F5F9))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "$totalMenuCount Menu",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF475569)
                    )
                }
            }

            // Menu Items List
            if (cartItems.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(text = "🛒", fontSize = 28.sp)
                        Text(
                            text = "Keranjang Anda Kosong",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                        Text(
                            text = "Pilih menu makanan lezat di Beranda untuk mulai memesan.",
                            fontSize = 12.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    cartItems.forEachIndexed { index, item ->
                        OrderItemRow(
                            item = item,
                            onQuantityChange = { delta -> onQuantityChange(item.id, delta) }
                        )
                        if (index < cartItems.size - 1) {
                            HorizontalDivider(
                                color = Color(0xFFF1F5F9),
                                thickness = 1.dp,
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            // Catatan Opsi Tambahan Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF1F4F9))
                    .clickable { onEditNoteClick() }
                    .padding(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.EditNote,
                        contentDescription = "Edit Catatan",
                        tint = Color(0xFF64748B),
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Catatan: $orderNote",
                        fontSize = 12.5.sp,
                        color = Color(0xFF334155),
                        fontWeight = FontWeight.Normal,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun OrderItemRow(
    item: CartItem,
    onQuantityChange: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            // Food Thumbnail
            FoodItemThumbnail(
                imageType = item.imageType,
                foodEmoji = item.foodEmoji,
                size = 54.dp
            )

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = item.name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Badge 1x
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFEEF2FF))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${item.quantity}x",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0052CC)
                        )
                    }

                    Text(
                        text = item.standName,
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Price Text + Optional Quantity Stepper
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "Rp ${formatRupiah(item.price * item.quantity)}",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
            )

            // Dynamic Quantity Stepper buttons
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF1F5F9))
                        .clickable { onQuantityChange(-1) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Kurang",
                        tint = Color(0xFF475569),
                        modifier = Modifier.size(12.dp)
                    )
                }

                Text(
                    text = item.quantity.toString(),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A),
                    modifier = Modifier.padding(horizontal = 2.dp)
                )

                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFEEF2FF))
                        .clickable { onQuantityChange(1) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Tambah",
                        tint = Color(0xFF0052CC),
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// 3. Loyalty Points Toggle ("Fitur Tukar Poin")
// -----------------------------------------------------------------------------
@Composable
private fun PointsRewardToggleCard(
    userPoints: Int,
    redeemPointsCost: Int,
    discountAmount: Int,
    isEnabled: Boolean,
    onToggleChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                // Coin Icon Container
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFEF3C7)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.MonetizationOn,
                        contentDescription = null,
                        tint = Color(0xFFD97706),
                        modifier = Modifier.size(24.dp)
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "Kantin Poin SMKN 8",
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )

                        // Orange Badge "50 Poin"
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Color(0xFFEA580C))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "$userPoints Poin",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Text(
                        text = "Tukarkan $redeemPointsCost poin hemat Rp ${formatRupiah(discountAmount)}",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Switch Toggle Button
            Switch(
                checked = isEnabled,
                onCheckedChange = onToggleChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Color(0xFF0052CC),
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color(0xFFCBD5E1)
                )
            )
        }
    }
}

// -----------------------------------------------------------------------------
// 4. Payment Method Selection ("Metode Pembayaran")
// -----------------------------------------------------------------------------
@Composable
private fun PaymentMethodSelectionCard(
    selectedMethod: PaymentMethodType,
    onMethodSelected: (PaymentMethodType) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Section Title
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.AccountBalanceWallet,
                    contentDescription = null,
                    tint = Color(0xFF0052CC),
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Metode Pembayaran",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
            }

            Text(
                text = "PILIH SATU",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF64748B),
                letterSpacing = 0.5.sp
            )
        }

        // Option 1: QRIS Verifikasi Otomatis
        val isQrisSelected = selectedMethod == PaymentMethodType.QRIS_AUTOMATIC
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onMethodSelected(PaymentMethodType.QRIS_AUTOMATIC) },
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = if (isQrisSelected) BorderStroke(1.5.dp, Color(0xFF0052CC)) else BorderStroke(1.dp, Color(0xFFE2E8F0)),
            elevation = CardDefaults.cardElevation(defaultElevation = if (isQrisSelected) 2.dp else 0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.Top,
                        modifier = Modifier.weight(1f)
                    ) {
                        // QRIS Icon Box
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF2563EB)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.QrCodeScanner,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "QRIS Verifikasi Otomatis",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A)
                                )
                            }

                            // Badge REKOMENDASI
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(Color(0xFF0052CC))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.VerifiedUser,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(10.dp)
                                    )
                                    Text(
                                        text = "REKOMENDASI",
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        letterSpacing = 0.5.sp
                                    )
                                }
                            }

                            Text(
                                text = "Bebas Antre • Langsung Ambil",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF0052CC)
                            )
                        }
                    }

                    // Radio Check Circle
                    SelectionRadioCircle(isSelected = isQrisSelected)
                }

                Text(
                    text = "Langsung lunas dan masuk antrean dapur. Scan barcode pesanan di loket Stand 04 tanpa perlu bawa uang tunai.",
                    fontSize = 12.sp,
                    color = Color(0xFF475569),
                    lineHeight = 17.sp
                )

                // E-Wallet Badges Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val walletList = listOf("GoPay", "OVO", "DANA", "ShopeePay", "Bank DKI")
                    walletList.forEach { wallet ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFFF1F5F9))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = wallet,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF334155)
                            )
                        }
                    }
                }
            }
        }

        // Option 2: Tunai / Cash di Stand
        val isCashSelected = selectedMethod == PaymentMethodType.CASH_AT_STAND
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onMethodSelected(PaymentMethodType.CASH_AT_STAND) },
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = if (isCashSelected) BorderStroke(1.5.dp, Color(0xFF0052CC)) else BorderStroke(1.dp, Color(0xFFE2E8F0)),
            elevation = CardDefaults.cardElevation(defaultElevation = if (isCashSelected) 2.dp else 0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        // Cash Icon Box
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFFEDD5)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Payments,
                                contentDescription = null,
                                tint = Color(0xFF9A3412),
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = "Tunai / Cash di Stand",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                            Text(
                                text = "Bayar pas waktu istirahat",
                                fontSize = 12.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }

                    // Radio Check Circle
                    SelectionRadioCircle(isSelected = isCashSelected)
                }

                // Warning Callout Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFFFF1F2))
                        .padding(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Warning,
                            contentDescription = "Peringatan",
                            tint = Color(0xFFC2410C),
                            modifier = Modifier.size(18.dp)
                        )

                        Text(
                            text = buildAnnotatedString {
                                append("Siapkan uang pas di loket Stand 04. ")
                                withStyle(
                                    style = SpanStyle(
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF991B1B)
                                    )
                                ) {
                                    append("Awas: ")
                                }
                                append("Pesanan tidak diambil setelah 10:35 WIB membatalkan pesanan & poin akun berkurang!")
                            },
                            fontSize = 12.sp,
                            color = Color(0xFF7F1D1D),
                            lineHeight = 17.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SelectionRadioCircle(isSelected: Boolean) {
    if (isSelected) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(Color(0xFF0052CC)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Terpilih",
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
        }
    } else {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(Color(0xFFE0E7FF))
                .border(1.5.dp, Color(0xFFCBD5E1), CircleShape)
        )
    }
}

// -----------------------------------------------------------------------------
// 5. Payment Summary ("Rincian Pembayaran")
// -----------------------------------------------------------------------------
@Composable
private fun PaymentSummaryCard(
    subtotal: Int,
    menuCount: Int,
    serviceFee: Int,
    discountAmount: Int,
    isDiscountActive: Boolean,
    totalBill: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Rincian Pembayaran",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
            )

            // Subtotal
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Subtotal ($menuCount Menu)",
                    fontSize = 14.sp,
                    color = Color(0xFF475569)
                )
                Text(
                    text = "Rp ${formatRupiah(subtotal)}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
            }

            // Biaya Layanan Koperasi
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Biaya Layanan Koperasi",
                        fontSize = 14.sp,
                        color = Color(0xFF475569)
                    )
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = "Info Biaya",
                        tint = Color(0xFF64748B),
                        modifier = Modifier.size(15.dp)
                    )
                }
                Text(
                    text = if (serviceFee == 0) "GRATIS (Rp 0)" else "Rp ${formatRupiah(serviceFee)}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0052CC)
                )
            }

            // Diskon Kantin Poin
            AnimatedVisibility(visible = isDiscountActive) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Stars,
                            contentDescription = null,
                            tint = Color(0xFFD97706),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Diskon Kantin Poin",
                            fontSize = 14.sp,
                            color = Color(0xFFC2410C)
                        )
                    }
                    Text(
                        text = "-Rp ${formatRupiah(discountAmount)}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFC2410C)
                    )
                }
            }

            HorizontalDivider(
                color = Color(0xFFE2E8F0),
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 2.dp)
            )

            // Total Tagihan
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total Tagihan",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
                Text(
                    text = "Rp ${formatRupiah(totalBill)}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF0052CC)
                )
            }
        }
    }
}

// -----------------------------------------------------------------------------
// 6. Sticky Bottom Action Bar
// -----------------------------------------------------------------------------
@Composable
private fun CheckoutBottomBar(
    totalBill: Int,
    selectedMethodName: String,
    onPayClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .shadow(8.dp)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = "Total Pembayaran",
                    fontSize = 11.sp,
                    color = Color(0xFF64748B)
                )
                Text(
                    text = "Rp ${formatRupiah(totalBill)}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF0052CC)
                )
                Text(
                    text = "Metode: $selectedMethodName",
                    fontSize = 11.sp,
                    color = Color(0xFF334155),
                    fontWeight = FontWeight.Medium
                )
            }

            Button(
                onClick = onPayClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0052CC)),
                shape = CircleShape,
                contentPadding = PaddingValues(
                    horizontal = 24.dp,
                    vertical = 12.dp
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Bayar Sekarang",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Bayar",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CheckoutScreenPreview() {
    EightCanteenTheme {
        CheckoutScreen()
    }
}
