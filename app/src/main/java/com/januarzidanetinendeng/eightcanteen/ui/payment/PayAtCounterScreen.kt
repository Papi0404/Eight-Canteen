package com.januarzidanetinendeng.eightcanteen.ui.payment

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.januarzidanetinendeng.eightcanteen.ui.checkout.CartViewModel
import com.januarzidanetinendeng.eightcanteen.ui.checkout.formatRupiah
import com.januarzidanetinendeng.eightcanteen.ui.components.EKantinLogoIcon
import com.januarzidanetinendeng.eightcanteen.ui.theme.EightCanteenTheme
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun PayAtCounterScreen(
    viewModel: CartViewModel = remember { CartViewModel() },
    onBackClick: () -> Unit = {},
    onConfirmCashPayment: () -> Unit = {},
    onCancelOrder: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val standNameDisplay = state.cartItems.firstOrNull()?.standName ?: state.standInfo

    // 15 Minutes Countdown Timer (900 seconds)
    var remainingSeconds by remember { mutableIntStateOf(900) }
    var isExpired by remember { mutableStateOf(false) }
    var showExpiredDialog by remember { mutableStateOf(false) }

    // Hitung Jam Batas Akhir (Waktu sekarang + 15 Menit)
    val deadlineTimeStr = remember {
        val calendar = Calendar.getInstance().apply {
            add(Calendar.MINUTE, 15)
        }
        val sdf = SimpleDateFormat("HH:mm 'WIB'", Locale("id", "ID"))
        sdf.format(calendar.time)
    }

    // Coroutine Timer Effect
    LaunchedEffect(remainingSeconds, isExpired) {
        if (remainingSeconds > 0 && !isExpired) {
            delay(1000L)
            remainingSeconds--
        } else if (remainingSeconds <= 0 && !isExpired) {
            isExpired = true
            showExpiredDialog = true
        }
    }

    val minutes = remainingSeconds / 60
    val seconds = remainingSeconds % 60
    val timerString = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)

    Scaffold(
        topBar = {
            HeaderBar(
                title = "Tiket Pengambilan Tunai...",
                onBackClick = onBackClick,
                onHelpClick = {
                    Toast.makeText(
                        context,
                        "Bawa uang pas ke Stand 04 saat waktu istirahat sekolah untuk mengambil pesanan Anda.",
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
            // 1. Banner Status Top (Orange saat Aktif, Merah saat Kadaluwarsa)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isExpired) Color(0xFFDC2626) else Color(0xFFF59E0B)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Timer,
                            contentDescription = null,
                            tint = if (isExpired) Color(0xFF991B1B) else Color(0xFF78350F),
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = if (isExpired) "● KADALUWARSA" else "● STATUS PESANAN",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (isExpired) Color(0xFFFEE2E2) else Color(0xFF78350F)
                            )
                        }

                        Text(
                            text = if (isExpired) "Pesanan Dibatalkan / Kadaluwarsa" else "Menunggu Bayar Tunai",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )

                        Text(
                            text = if (isExpired) "Batas waktu 15 menit pengambilan telah habis" else "Bawa uang pas saat istirahat sekolah",
                            fontSize = 12.sp,
                            color = if (isExpired) Color(0xFFFEF2F2) else Color(0xFF78350F)
                        )
                    }
                }
            }

            // 2. Ticket Card Utama (Container Putih)
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
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // ID Tiket & Lokasi Stand Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = "ID TIKET PENGAMBILAN",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF64748B)
                            )
                            Text(
                                text = "#C-089",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Black,
                                color = if (isExpired) Color(0xFF94A3B8) else Color(0xFF0052CC),
                                letterSpacing = 0.5.sp
                            )
                        }

                        Column(
                            horizontalAlignment = Alignment.End,
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = "LOKASI STAND",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF64748B)
                            )
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(Color(0xFFF1F5F9))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Storefront,
                                        contentDescription = null,
                                        tint = Color(0xFF0052CC),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = "Stand 04",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0F172A)
                                    )
                                }
                            }
                        }
                    }

                    // Deskripsi Stand & Badge COD
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "$standNameDisplay - Pintu Timur Kantin",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF334155),
                            modifier = Modifier.weight(1f)
                        )

                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(if (isExpired) Color(0xFFF1F5F9) else Color(0xFFFFEDD5))
                                .padding(horizontal = 10.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = if (isExpired) "Batal" else "Tunai / COD",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (isExpired) Color(0xFF64748B) else Color(0xFFC2410C)
                            )
                        }
                    }

                    // Display QR Code & Barcode Garis
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFF8FAFC))
                            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // QR Code
                            QrisCodeCanvas(size = 180.dp)

                            // Barcode Lines
                            LinearBarcodeCanvas(width = 230.dp, height = 44.dp)

                            Text(
                                text = "C089-SMK8-2024",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF334155),
                                letterSpacing = 2.sp
                            )
                        }
                    }

                    // Box Total Tagihan Golden/Orange
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isExpired) Color(0xFFE2E8F0) else Color(0xFFF59E0B))
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = "TOTAL TAGIHAN BAYAR DI KASIR STAND",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (isExpired) Color(0xFF475569) else Color(0xFF78350F),
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "Rp ${formatRupiah(state.totalPayment)}",
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Black,
                                color = if (isExpired) Color(0xFF334155) else Color(0xFF451A03)
                            )
                            Text(
                                text = if (isExpired) "(Pesanan Kadaluwarsa)" else "(Mohon Siapkan Uang Pas)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isExpired) Color(0xFF64748B) else Color(0xFF78350F)
                            )
                        }
                    }

                    // Banner Timer Merah/Muda (Real-Time Countdown)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(CircleShape)
                            .background(if (isExpired) Color(0xFFFEE2E2) else Color(0xFFEEF2FF))
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.AccessTime,
                                contentDescription = null,
                                tint = Color(0xFFDC2626),
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = buildAnnotatedString {
                                    withStyle(
                                        style = SpanStyle(
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color(0xFF991B1B)
                                        )
                                    ) {
                                        if (isExpired) {
                                            append("Sisa Waktu: 00:00 (Waktu Habis) ")
                                        } else {
                                            append("Sisa Waktu: $timerString Menit ")
                                        }
                                    }
                                    append("(Batas $deadlineTimeStr)")
                                },
                                fontSize = 12.sp,
                                color = Color(0xFF475569)
                            )
                        }
                    }
                }
            }

            // 3. Card Peringatan Tata Tertib Siswa (Box Merah Muda)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF1F2)),
                border = BorderStroke(1.dp, Color(0xFFFECDD3))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Header Peringatan
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF991B1B)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Shield,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                            Text(
                                text = "PERINGATAN TATA TERTIB SISWA",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF991B1B)
                            )
                            Text(
                                text = "Awas Sanksi Hit & Run SMKN 8 Jakarta",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFC2410C)
                            )
                        }
                    }

                    // Instruksi Tunjukkan Tiket
                    Text(
                        text = buildAnnotatedString {
                            append("Tunjukkan tiket ini dan serahkan uang tunai ")
                            withStyle(
                                style = SpanStyle(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF991B1B)
                                )
                            ) {
                                append("Rp ${formatRupiah(state.totalPayment)}")
                            }
                            append(" ke penjual Stand 04 sebelum waktu 15 menit berakhir.")
                        },
                        fontSize = 12.sp,
                        color = Color(0xFF7F1D1D),
                        lineHeight = 17.sp
                    )

                    // Inner Card List Sanksi Pelanggaran
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White)
                            .padding(12.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "SANKSI PELANGGARAN / PESANAN FIKTIF:",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF991B1B),
                                letterSpacing = 0.5.sp
                            )

                            SanksiItemRow(
                                number = "1",
                                text = "+1 Poin Pelanggaran Kedisiplinan Siswa"
                            )
                            SanksiItemRow(
                                number = "2",
                                text = "Pemblokiran permanen fitur pesan bayar tunai (COD)"
                            )
                            SanksiItemRow(
                                number = "3",
                                text = "Laporan otomatis ke guru BK / Pembina Koperasi Sekolah"
                            )
                        }
                    }

                    // Inner Info Box Pembayaran Kasir
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFEFF6FF))
                            .padding(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.VerifiedUser,
                                contentDescription = null,
                                tint = Color(0xFF0052CC),
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = buildAnnotatedString {
                                    append("Penjual akan men-scan barcode ini & menekan tombol ")
                                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                        append("“Terima Uang”")
                                    }
                                    append(" di HP kasir untuk menyelesaikan transaksi.")
                                },
                                fontSize = 11.5.sp,
                                color = Color(0xFF1E3A8A),
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            // 4. Bottom Action Buttons
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Button 1: Petunjuk Lokasi Stand
                Button(
                    onClick = {
                        Toast.makeText(
                            context,
                            "Lokasi Stand 04: Pintu Timur Kantin SMKN 8 Jakarta.",
                            Toast.LENGTH_LONG
                        ).show()
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
                            imageVector = Icons.Outlined.Explore,
                            contentDescription = "Petunjuk",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Petunjuk Lokasi Stand 04",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                // Button 2: Batalkan Pesanan (Maks. 5 menit)
                Button(
                    onClick = {
                        viewModel.clearCart()
                        Toast.makeText(context, "Pesanan Berhasil Dibatalkan.", Toast.LENGTH_SHORT).show()
                        onCancelOrder()
                    },
                    enabled = !isExpired,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFEEF2FF),
                        contentColor = Color(0xFFDC2626),
                        disabledContainerColor = Color(0xFFF1F5F9),
                        disabledContentColor = Color(0xFF94A3B8)
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
                            imageVector = Icons.Outlined.Cancel,
                            contentDescription = "Batal",
                            tint = if (isExpired) Color(0xFF94A3B8) else Color(0xFFDC2626),
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = if (isExpired) "Waktu Pengambilan Habis" else "Batalkan Pesanan (Maks. 5 menit)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isExpired) Color(0xFF94A3B8) else Color(0xFFDC2626)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // Modal Dialog Waktu Habis / Kadaluwarsa
    if (showExpiredDialog) {
        AlertDialog(
            onDismissRequest = { /* Non-dismissable until user clicks action */ },
            icon = {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = Color(0xFFDC2626),
                    modifier = Modifier.size(36.dp)
                )
            },
            title = {
                Text(
                    text = "Waktu Pengambilan Habis",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFF991B1B)
                )
            },
            text = {
                Text(
                    text = "Pesanan tunai Anda telah dibatalkan otomatis oleh sistem karena melebihi batas waktu 15 menit. Sesuai tata tertib, poin akun Anda mungkin berkurang.",
                    fontSize = 13.5.sp,
                    color = Color(0xFF334155),
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showExpiredDialog = false
                        viewModel.clearCart()
                        onCancelOrder()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0052CC)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Kembali ke Beranda",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        )
    }
}

@Composable
private fun SanksiItemRow(number: String, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(18.dp)
                .clip(CircleShape)
                .background(Color(0xFF991B1B)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number,
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Text(
            text = text,
            fontSize = 12.sp,
            color = Color(0xFF334155),
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun HeaderBar(
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
                    contentDescription = "Info",
                    tint = Color(0xFF475569),
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PayAtCounterScreenPreview() {
    EightCanteenTheme {
        PayAtCounterScreen()
    }
}
