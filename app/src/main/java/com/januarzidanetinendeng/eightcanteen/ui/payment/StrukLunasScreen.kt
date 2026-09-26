package com.januarzidanetinendeng.eightcanteen.ui.payment

import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.januarzidanetinendeng.eightcanteen.data.remote.OrderResponse
import com.januarzidanetinendeng.eightcanteen.data.repository.CanteenRepository
import com.januarzidanetinendeng.eightcanteen.ui.checkout.CartViewModel
import com.januarzidanetinendeng.eightcanteen.ui.checkout.formatRupiah
import com.januarzidanetinendeng.eightcanteen.ui.components.EKantinLogoIcon
import com.januarzidanetinendeng.eightcanteen.ui.theme.EightCanteenTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun StrukLunasScreen(
    orderId: String? = null,
    viewModel: CartViewModel = remember { CartViewModel() },
    onBackClick: () -> Unit = {},
    onBackToHome: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var orderDetail by remember { mutableStateOf<OrderResponse?>(null) }
    var isCheckingStatus by remember { mutableStateOf(false) }

    // Polling otomatis untuk mengecek apakah penjual sudah scan pesanan siswa
    LaunchedEffect(orderId) {
        if (!orderId.isNullOrBlank()) {
            val repo = CanteenRepository()
            repo.getOrderDetail(orderId).onSuccess { res ->
                res.data?.let { orderDetail = it }
            }

            while (true) {
                delay(3000L)
                if (orderDetail?.status != "COMPLETED") {
                    repo.getOrderDetail(orderId).onSuccess { res ->
                        res.data?.let { updated ->
                            if (updated.status == "COMPLETED" && orderDetail?.status != "COMPLETED") {
                                Toast.makeText(context, "Pesanan Berhasil Discan Penjual! Status: LUNAS", Toast.LENGTH_LONG).show()
                            }
                            orderDetail = updated
                        }
                    }
                } else {
                    break
                }
            }
        }
    }

    val method = orderDetail?.paymentMethod ?: state.selectedPaymentMethodName
    val isCash = method.contains("TUNAI", ignoreCase = true) || method.contains("CASH", ignoreCase = true)
    val currentStatus = orderDetail?.status ?: "PENDING"
    val isSellerScanned = currentStatus == "COMPLETED" || orderDetail?.paymentStatus == "PAID"
    val isPaid = if (isCash) isSellerScanned else (orderDetail?.paymentStatus == "PAID" || currentStatus == "PAID" || currentStatus == "READY" || currentStatus == "COMPLETED")
    val displayTotal = orderDetail?.totalAmount?.takeIf { it > 0 }?.toDouble() ?: state.totalPayment

    fun refreshOrderStatus() {
        if (!orderId.isNullOrBlank()) {
            isCheckingStatus = true
            coroutineScope.launch {
                val repo = CanteenRepository()
                repo.getOrderDetail(orderId).onSuccess { res ->
                    isCheckingStatus = false
                    res.data?.let { updated ->
                        orderDetail = updated
                        if (updated.status == "COMPLETED") {
                            Toast.makeText(context, "Pesanan Telah Discan Penjual! Status: LUNAS", Toast.LENGTH_SHORT).show()
                        } else {
                            val statusDisplay = if (isCash && !isPaid) "Belum Lunas (Bayar Tunai)" else updated.status
                            Toast.makeText(context, "Status Terkini: $statusDisplay", Toast.LENGTH_SHORT).show()
                        }
                    }
                }.onFailure {
                    isCheckingStatus = false
                    Toast.makeText(context, "Gagal memeriksa status", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    val currentDateStr = remember {
        val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm 'WIB'", Locale("id", "ID"))
        sdf.format(Date())
    }

    val standNameDisplay = orderDetail?.stands?.name ?: orderDetail?.standName ?: state.cartItems.firstOrNull()?.standName ?: state.standInfo

    Scaffold(
        topBar = {
            HeaderBar(
                onBackClick = onBackClick,
                onHelpClick = {
                    Toast.makeText(
                        context,
                        "Tunjukkan Struk/Barcode ini di Loket Stand untuk mengambil pesanan Anda.",
                        Toast.LENGTH_LONG
                    ).show()
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
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Success / Pending Header Banner Card
            val bannerBg = if (isPaid) Color(0xFF0052CC) else Color(0xFFD97706)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = bannerBg)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp, horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // White checkmark or payment icon circle
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isPaid) Icons.Default.Check else Icons.Default.Payments,
                            contentDescription = if (isPaid) "Lunas" else "Bayar Tunai",
                            tint = if (isPaid) Color(0xFF0052CC) else Color(0xFFD97706),
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    // Pill Badge
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(if (isPaid) Color(0xFFF59E0B) else Color(0xFFFEF3C7))
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (isPaid) {
                                if (isSellerScanned) "🎉 TELAH DIAMBIL & LUNAS! 🥳" else "🎉 BEBAS ANTRE KASIR! 🥳"
                            } else {
                                "💵 BAYAR TUNAI DI KASIR LOKET"
                            },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isPaid) Color(0xFF78350F) else Color(0xFF92400E)
                        )
                    }

                    Text(
                        text = if (isPaid) "Pembayaran Berhasil" else "Pesanan Dibuat (Belum Bayar)",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )

                    Text(
                        text = if (isPaid) {
                            if (isCash) "Lunas via Tunai di Loket Stand (Telah Discan Penjual)"
                            else "Lunas via QRIS Dinamis (Bank DKI / E-Wallet)"
                        } else {
                            "Silakan bawa uang pas ke kasir stand saat pengambilan makanan"
                        },
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }

            // 2. Main Ticket Container
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Stand Info Pill
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color(0xFFEEF2FF))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
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
                                text = standNameDisplay,
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0040A8)
                            )
                        }
                    }

                    // Queue Pickup Number
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = "Nomor Antrean Pickup",
                            fontSize = 12.sp,
                            color = Color(0xFF64748B)
                        )
                        Text(
                            text = orderDetail?.orderNumber?.takeIf { it.isNotBlank() }?.let { "#$it" }
                                ?: (orderId?.takeLast(5)?.let { "#A-$it" } ?: "#A-142"),
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF0052CC),
                            letterSpacing = 1.sp
                        )

                        // Status Badge
                        val (statusBadgeText, statusBadgeBg, statusBadgeColor) = when {
                            !isPaid && isCash -> Triple("● BELUM LUNAS (BAYAR DI KASIR)", Color(0xFFFEF3C7), Color(0xFFB45309))
                            isSellerScanned -> Triple("● LUNAS • PESANAN SELESAI (SUDAH DIAMBIL)", Color(0xFFDCFCE7), Color(0xFF15803D))
                            isPaid -> Triple("● LUNAS • SIAP DIAMBIL", Color(0xFFDCFCE7), Color(0xFF15803D))
                            currentStatus == "PENDING_PAYMENT" || currentStatus == "PENDING" -> Triple("● MENUNGGU PEMBAYARAN", Color(0xFFFEF3C7), Color(0xFFB45309))
                            else -> Triple("● $currentStatus", Color(0xFFDBEAFE), Color(0xFF1E40AF))
                        }
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(statusBadgeBg)
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = statusBadgeText,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = statusBadgeColor
                            )
                        }
                    }

                    // QR Code & Linear Barcode Card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFF1F5F9))
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Payment Status Banner Inside QR Area
                            if (!isPaid && isCash) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFFFEF3C7))
                                        .border(1.dp, Color(0xFFFCD34D), RoundedCornerShape(12.dp))
                                        .padding(horizontal = 12.dp, vertical = 8.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(text = "⏳", fontSize = 16.sp)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(
                                                text = "STATUS: BELUM LUNAS (TUNAI)",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Black,
                                                color = Color(0xFF92400E)
                                            )
                                            Text(
                                                text = "Tunjukkan QR & bayar tunai. Lunas setelah discan penjual.",
                                                fontSize = 10.sp,
                                                color = Color(0xFFB45309)
                                            )
                                        }
                                    }
                                }
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFFDCFCE7))
                                        .border(1.dp, Color(0xFF86EFAC), RoundedCornerShape(12.dp))
                                        .padding(horizontal = 12.dp, vertical = 8.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(text = "✅", fontSize = 16.sp)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(
                                                text = "STATUS: LUNAS (TERVERIFIKASI)",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Black,
                                                color = Color(0xFF15803D)
                                            )
                                            Text(
                                                text = if (isSellerScanned) "Pesanan telah discan oleh penjual di stand" else "Pembayaran QRIS berhasil",
                                                fontSize = 10.sp,
                                                color = Color(0xFF166534)
                                            )
                                        }
                                    }
                                }
                            }

                            // QR Code
                            QrisCodeCanvas(size = 170.dp)

                            // Barcode Linear Lines
                            LinearBarcodeCanvas(width = 220.dp, height = 40.dp)

                            Text(
                                text = orderDetail?.orderNumber?.takeIf { it.isNotBlank() }?.let { "EKANTIN-$it" }
                                    ?: (orderId?.take(12)?.let { "EKANTIN-$it" } ?: "SMKN8-A142-2025"),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF334155),
                                letterSpacing = 1.5.sp
                            )

                            // Tombol Refresh Status Pesanan
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(Color.White)
                                    .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(20.dp))
                                    .clickable { refreshOrderStatus() }
                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Cek Status",
                                    tint = Color(0xFF0052CC),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isCheckingStatus) "Memeriksa..." else "Cek Status Terkini",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF0052CC)
                                )
                            }
                        }
                    }

                    // Dashed Divider Separator
                    DashedLineSeparator()

                    // Details Section
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Location Info
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFEEF2FF)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.LocationOn,
                                    contentDescription = null,
                                    tint = Color(0xFF0052CC),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "LOKASI STAND",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF64748B)
                                )
                                Text(
                                    text = "Gedung C Lantai 1 – Stand 04",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A)
                                )
                            }
                        }

                        // Schedule Info
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFFEDD5)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.AccessTime,
                                    contentDescription = null,
                                    tint = Color(0xFFC2410C),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "JADWAL AMBIL",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF64748B)
                                )
                                Text(
                                    text = state.timeSlotTitle,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A)
                                )
                            }
                        }

                        // Callout Note Box
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isPaid) Color(0xFFEFF6FF) else Color(0xFFFEF3C7))
                                .padding(12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.VerifiedUser,
                                    contentDescription = null,
                                    tint = if (isPaid) Color(0xFF0052CC) else Color(0xFFD97706),
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = buildAnnotatedString {
                                        if (!isPaid && isCash) {
                                            append("Tunjukkan Barcode ini ke penjual di loket stand dan siapkan uang pas ")
                                            withStyle(
                                                style = SpanStyle(
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFFB45309)
                                                )
                                            ) {
                                                append("Rp ${formatRupiah(displayTotal)}")
                                            }
                                            append(". Status akan otomatis berubah menjadi LUNAS setelah penjual scan barcode ini!")
                                        } else {
                                            append("Tunjukkan Barcode ini ke penjual di loket untuk langsung ambil pesanan ")
                                            withStyle(
                                                style = SpanStyle(
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFF0052CC)
                                                )
                                            ) {
                                                append(if (isSellerScanned) "Pesanan telah selesai diambil!" else "tanpa perlu antre bayar uang lagi!")
                                            }
                                        }
                                    },
                                    fontSize = 12.sp,
                                    color = if (isPaid) Color(0xFF1E3A8A) else Color(0xFF92400E),
                                    lineHeight = 17.sp
                                )
                            }
                        }
                    }

                    HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 1.dp)

                    // Transaction Summary Table
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TransactionRow(label = "Waktu Bayar", value = currentDateStr)
                        TransactionRow(
                            label = "No. Referensi",
                            value = orderDetail?.id?.take(18) ?: (orderId?.take(18) ?: "SMKN8-QRIS-994281")
                        )
                        val methodText = when {
                            (orderDetail?.paymentMethod ?: state.selectedPaymentMethodName).contains("QRIS", true) -> "QRIS Dinamis (Midtrans / E-Wallet)"
                            else -> "Cash di Loket Stand"
                        }
                        TransactionRow(label = "Metode", value = methodText)

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Total Pembayaran",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                val totalToDisplay = orderDetail?.totalAmount?.takeIf { it > 0 }?.toDouble() ?: state.totalPayment
                                Text(
                                    text = "Rp ${formatRupiah(totalToDisplay)}",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF0052CC)
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isPaid) Color(0xFFDCFCE7) else Color(0xFFFEF3C7))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = if (isPaid) "LUNAS" else "BELUM LUNAS",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isPaid) Color(0xFF15803D) else Color(0xFFB45309)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 3. Bottom Action Buttons
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Button 1: Share / WhatsApp
                Button(
                    onClick = {
                        Toast.makeText(context, "Membuka WhatsApp untuk membagikan Struk Pesanan...", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0052CC)),
                    shape = CircleShape,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Unduh Struk / Bagikan ke WhatsApp",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                // Button 2: Kembali ke Beranda
                Button(
                    onClick = {
                        viewModel.clearCart()
                        onBackToHome()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFEEF2FF),
                        contentColor = Color(0xFF0052CC)
                    ),
                    shape = CircleShape,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Beranda",
                            tint = Color(0xFF0052CC),
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Kembali ke Beranda",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0052CC)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun HeaderBar(
    onBackClick: () -> Unit,
    onHelpClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .shadow(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
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

                Text(
                    text = "Struk Lunas Qris &...",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
            }

            IconButton(
                onClick = onHelpClick,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.HelpOutline,
                    contentDescription = "Info",
                    tint = Color(0xFF475569),
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

@Composable
private fun TransactionRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = Color(0xFF64748B)
        )
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F172A)
        )
    }
}

@Composable
private fun DashedLineSeparator() {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
    ) {
        val pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)
        drawLine(
            color = Color(0xFFCBD5E1),
            start = Offset(0f, 0f),
            end = Offset(size.width, 0f),
            pathEffect = pathEffect,
            strokeWidth = 2f
        )
    }
}

@Composable
fun LinearBarcodeCanvas(width: Dp, height: Dp) {
    Canvas(modifier = Modifier.size(width, height)) {
        val w = size.width
        val h = size.height

        val barWidths = listOf(
            3f, 1f, 4f, 2f, 1f, 5f, 2f, 1f, 3f, 2f,
            4f, 1f, 2f, 5f, 1f, 3f, 2f, 4f, 1f, 3f,
            2f, 5f, 1f, 2f, 4f, 1f, 3f, 2f, 4f, 2f
        )

        var xAcc = 0f
        val step = w / barWidths.sum()

        barWidths.forEachIndexed { index, bw ->
            val barW = bw * step
            if (index % 2 == 0) {
                drawRect(
                    color = Color(0xFF0F172A),
                    topLeft = Offset(xAcc, 0f),
                    size = Size(barW, h)
                )
            }
            xAcc += barW
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StrukLunasScreenPreview() {
    EightCanteenTheme {
        StrukLunasScreen()
    }
}
