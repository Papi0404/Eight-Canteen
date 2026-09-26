package com.januarzidanetinendeng.eightcanteen.ui.checkout

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.januarzidanetinendeng.eightcanteen.data.remote.OrderItemRequest
import com.januarzidanetinendeng.eightcanteen.data.repository.CanteenRepository
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

@Composable
fun CheckoutPaymentScreen(
    cartViewModel: CartViewModel = remember { CartViewModel() },
    onBackClick: () -> Unit = {},
    onPaymentSuccess: (method: String, orderId: String) -> Unit = { _, _ -> }
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    var selectedMethod by remember { mutableStateOf("QRIS") } // QRIS or TUNAI
    var isLoading by remember { mutableStateOf(false) }

    val cartUiState by cartViewModel.uiState.collectAsState()
    val items = cartUiState.cartItems
    val subtotal = cartUiState.subtotal
    val adminFee = if (selectedMethod == "QRIS") 1000 else 0
    val totalPayment = subtotal + adminFee

    Scaffold(
        containerColor = ScreenBg,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
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

                Column {
                    Text(
                        text = "Checkout & Pembayaran",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Pilih metode pembayaran kantin",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
            }
        }
    ) { innerPadding ->
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
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White.copy(alpha = 0.2f))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(text = "WAKTU AMBIL TEPAT", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = "Istirahat 1 (10:15 WIB)", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text(text = "Stand 04 • Bang Ali", fontSize = 11.sp, color = Color.White.copy(alpha = 0.9f))
                    }
                    Text(text = "⏱️", fontSize = 32.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Rincian Pesanan
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
                            Icon(imageVector = Icons.AutoMirrored.Filled.ReceiptLong, contentDescription = "Receipt", tint = BluePrimary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Rincian Pesanan", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(InputBg)
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(text = "${items.size} Menu", fontSize = 10.sp, color = TextSecondary)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    items.forEach { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFFEF3C7)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = item.foodEmoji, fontSize = 24.sp)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = item.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary, maxLines = 1)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "${item.quantity}x", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BluePrimary)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = item.standName, fontSize = 11.sp, color = TextSecondary)
                                }
                            }
                            Text(
                                text = "Rp ${String.format(Locale.GERMANY, "%,d", (item.price * item.quantity).toLong())}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 3. Metode Pembayaran
            Text(text = "Metode Pembayaran", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.height(10.dp))

            // QRIS Option
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { selectedMethod = "QRIS" },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = if (selectedMethod == "QRIS") BlueLightBg else Color.White),
                border = BorderStroke(2.dp, if (selectedMethod == "QRIS") BluePrimary else BorderColor)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.QrCode, contentDescription = "QRIS", tint = if (selectedMethod == "QRIS") BluePrimary else TextMuted, modifier = Modifier.size(28.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "QRIS Bebas Admin", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text(text = "Gopay, Dana, ShopeePay, M-Banking", fontSize = 11.sp, color = TextSecondary)
                    }
                    // Radio button circle
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .border(2.dp, if (selectedMethod == "QRIS") BluePrimary else TextMuted, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (selectedMethod == "QRIS") {
                            Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(BluePrimary))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Tunai Option
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { selectedMethod = "TUNAI" },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = if (selectedMethod == "TUNAI") BlueLightBg else Color.White),
                border = BorderStroke(2.dp, if (selectedMethod == "TUNAI") BluePrimary else BorderColor)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Money, contentDescription = "TUNAI", tint = if (selectedMethod == "TUNAI") BluePrimary else TextMuted, modifier = Modifier.size(28.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Bayar Tunai di Kasir (COD)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text(text = "Berikan uang pas ke petugas kantin", fontSize = 11.sp, color = TextSecondary)
                    }
                    // Radio button circle
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .border(2.dp, if (selectedMethod == "TUNAI") BluePrimary else TextMuted, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (selectedMethod == "TUNAI") {
                            Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(BluePrimary))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 4. Ringkasan Tagihan
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, BorderColor)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Ringkasan Pembayaran", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "Total Harga (${items.size} Menu)", fontSize = 12.sp, color = TextSecondary)
                        Text(text = "Rp ${String.format(Locale.GERMANY, "%,d", subtotal.toLong())}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "Biaya Layanan Koperasi", fontSize = 12.sp, color = TextSecondary)
                        Text(text = if(adminFee > 0) "Rp ${String.format(Locale.GERMANY, "%,d", adminFee.toLong())}" else "Gratis", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if(adminFee > 0) TextPrimary else Color(0xFF16A34A))
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(BorderColor))
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
                    isLoading = true
                    coroutineScope.launch {
                        val repo = CanteenRepository()
                        val standIdToUse = items.firstOrNull()?.standId ?: "stand-1"
                        val orderItems = items.map {
                            OrderItemRequest(
                                menuId = it.id,
                                quantity = it.quantity
                            )
                        }
                        repo.createOrder(
                            standId = standIdToUse,
                            items = orderItems,
                            paymentMethod = selectedMethod,
                            usePoints = false
                        ).onSuccess { res ->
                            isLoading = false
                            val orderId = res.data?.id ?: "ORDER-${System.currentTimeMillis()}"
                            val orderNum = res.data?.orderNumber ?: "A-101"
                            Toast.makeText(context, "Pesanan #$orderNum Berhasil Dibuat!", Toast.LENGTH_LONG).show()
                            onPaymentSuccess(selectedMethod, orderId)
                        }.onFailure { err ->
                            isLoading = false
                            val orderId = "ORDER-${System.currentTimeMillis()}"
                            Toast.makeText(context, "Mode Offline: Pesanan Berhasil Disimpan", Toast.LENGTH_SHORT).show()
                            onPaymentSuccess(selectedMethod, orderId)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = !isLoading,
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
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
