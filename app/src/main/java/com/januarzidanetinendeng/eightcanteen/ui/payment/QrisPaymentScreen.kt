package com.januarzidanetinendeng.eightcanteen.ui.payment

import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.januarzidanetinendeng.eightcanteen.ui.checkout.CartViewModel
import com.januarzidanetinendeng.eightcanteen.ui.checkout.formatRupiah
import com.januarzidanetinendeng.eightcanteen.ui.components.EKantinLogoIcon
import com.januarzidanetinendeng.eightcanteen.ui.theme.EightCanteenTheme
import kotlinx.coroutines.delay

@Composable
fun QrisPaymentScreen(
    viewModel: CartViewModel = remember { CartViewModel() },
    onBackClick: () -> Unit = {},
    onPaymentSuccess: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Countdown Timer (15 Minutes = 899 seconds)
    var remainingSeconds by remember { mutableIntStateOf(899) }

    LaunchedEffect(Unit) {
        while (remainingSeconds > 0) {
            delay(1000L)
            remainingSeconds--
        }
    }

    val minutes = remainingSeconds / 60
    val seconds = remainingSeconds % 60
    val timerString = String.format("%02d:%02d", minutes, seconds)

    Scaffold(
        topBar = {
            TopAppBarHeader(
                title = "Pembayaran QRIS",
                onBackClick = onBackClick,
                onHelpClick = {
                    Toast.makeText(context, "Scan QR Code dengan m-Banking (Bank DKI) atau E-Wallet pilihan Anda.", Toast.LENGTH_LONG).show()
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
            // 1. Countdown Timer Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AccessTime,
                        contentDescription = null,
                        tint = Color(0xFF0052CC),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Selesaikan Pembayaran Dalam ",
                        fontSize = 13.sp,
                        color = Color(0xFF1E3A8A)
                    )
                    Text(
                        text = timerString,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF0052CC)
                    )
                }
            }

            // 2. Info Stand & Tagihan Card
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
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Storefront,
                            contentDescription = null,
                            tint = Color(0xFF0052CC),
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = state.cartItems.firstOrNull()?.standName ?: state.standInfo,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0040A8)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Total Tagihan",
                            fontSize = 14.sp,
                            color = Color(0xFF64748B)
                        )
                        Text(
                            text = "Rp ${formatRupiah(state.totalPayment)}",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF0052CC)
                        )
                    }
                }
            }

            // 3. Display QR Code Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // QRIS Brand Header Banner
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF0F172A))
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "QRIS",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 2.sp
                            )
                            Text(
                                text = "DINAMIS",
                                color = Color(0xFFF59E0B),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Large QR Code Canvas Component
                    QrisCodeCanvas(size = 210.dp)

                    Text(
                        text = "Scan QR Code ini menggunakan aplikasi M-Banking (Bank DKI, JakOne) atau E-Wallet (GoPay, OVO, DANA, ShopeePay)",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B),
                        textAlign = TextAlign.Center,
                        lineHeight = 17.sp,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 4. Main Button: Cek Status Pembayaran
            Button(
                onClick = {
                    viewModel.processPayment()
                    Toast.makeText(context, "Pembayaran Berhasil Diverifikasi!", Toast.LENGTH_SHORT).show()
                    onPaymentSuccess()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0052CC)),
                shape = CircleShape,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Verify",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Cek Status Pembayaran",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun TopAppBarHeader(
    title: String,
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
                    text = title,
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
                    contentDescription = "Bantuan",
                    tint = Color(0xFF475569),
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

@Composable
fun QrisCodeCanvas(size: Dp = 200.dp) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF8FAFC))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = this.size.width
            val h = this.size.height

            // Corner Alignment Squares
            val sqSize = w * 0.22f

            // Top-Left Corner
            drawRoundRect(color = Color(0xFF0F172A), topLeft = Offset(0f, 0f), size = Size(sqSize, sqSize), cornerRadius = CornerRadius(6f))
            drawRoundRect(color = Color.White, topLeft = Offset(sqSize * 0.2f, sqSize * 0.2f), size = Size(sqSize * 0.6f, sqSize * 0.6f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f))
            drawRoundRect(color = Color(0xFF0F172A), topLeft = Offset(sqSize * 0.35f, sqSize * 0.35f), size = Size(sqSize * 0.3f, sqSize * 0.3f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(2f))

            // Top-Right Corner
            drawRoundRect(color = Color(0xFF0F172A), topLeft = Offset(w - sqSize, 0f), size = Size(sqSize, sqSize), cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f))
            drawRoundRect(color = Color.White, topLeft = Offset(w - sqSize + sqSize * 0.2f, sqSize * 0.2f), size = Size(sqSize * 0.6f, sqSize * 0.6f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f))
            drawRoundRect(color = Color(0xFF0F172A), topLeft = Offset(w - sqSize + sqSize * 0.35f, sqSize * 0.35f), size = Size(sqSize * 0.3f, sqSize * 0.3f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(2f))

            // Bottom-Left Corner
            drawRoundRect(color = Color(0xFF0F172A), topLeft = Offset(0f, h - sqSize), size = Size(sqSize, sqSize), cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f))
            drawRoundRect(color = Color.White, topLeft = Offset(sqSize * 0.2f, h - sqSize + sqSize * 0.2f), size = Size(sqSize * 0.6f, sqSize * 0.6f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f))
            drawRoundRect(color = Color(0xFF0F172A), topLeft = Offset(sqSize * 0.35f, h - sqSize + sqSize * 0.35f), size = Size(sqSize * 0.3f, sqSize * 0.3f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(2f))

            // Grid pattern simulation
            val cols = 8
            val rows = 8
            val cellW = w / cols
            val cellH = h / rows

            for (r in 0 until rows) {
                for (c in 0 until cols) {
                    if ((r < 2 && c < 2) || (r < 2 && c > 5) || (r > 5 && c < 2)) continue
                    if ((r + c) % 2 == 0) {
                        drawRoundRect(
                            color = Color(0xFF0F172A),
                            topLeft = Offset(c * cellW + cellW * 0.15f, r * cellH + cellH * 0.15f),
                            size = Size(cellW * 0.7f, cellH * 0.7f),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(3f)
                        )
                    }
                }
            }

            // Center Logo icon overlay
            drawCircle(color = Color(0xFF0052CC), radius = w * 0.12f, center = Offset(w * 0.5f, h * 0.5f))
            drawCircle(color = Color.White, radius = w * 0.09f, center = Offset(w * 0.5f, h * 0.5f))
        }
        Icon(
            imageVector = Icons.Outlined.QrCodeScanner,
            contentDescription = null,
            tint = Color(0xFF0052CC),
            modifier = Modifier.size(24.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun QrisPaymentScreenPreview() {
    EightCanteenTheme {
        QrisPaymentScreen()
    }
}
