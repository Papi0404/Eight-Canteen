package com.januarzidanetinendeng.eightcanteen.ui.admin

import android.widget.Toast
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
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AddBusiness
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import kotlinx.coroutines.launch
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

data class StandOmset(
    val code: String,
    val name: String,
    val grossIncome: Int,
    val progress: Float,
    val barColor: Color
)

data class PayoutItem(
    val code: String,
    val standName: String,
    val netAmount: Int,
    var isDisbursed: Boolean = false
)

data class ViolationCase(
    val id: String,
    val studentName: String,
    val studentClass: String,
    val standName: String,
    val amount: Int,
    val note: String,
    var isWarned: Boolean = false
)

data class PendingStand(
    val id: String,
    val ownerName: String,
    val standName: String,
    val category: String,
    var isApproved: Boolean = false
)

@Composable
fun AdminDashboardScreen(
    onLogoutClick: () -> Unit = {},
    onNavigateToAddStand: () -> Unit = {}
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val periodScrollState = rememberScrollState()

    var selectedPeriod by remember { mutableStateOf("Minggu Ini") }
    var selectedNavTab by remember { mutableIntStateOf(0) } // Beranda admin tab active

    val standOmsetList = remember {
        listOf(
            StandOmset("C", "Stand C (Ayam Geprek 8)", 950000, 0.95f, BluePrimary),
            StandOmset("A", "Stand A (Kebab Bang Ali)", 820000, 0.82f, BluePrimary),
            StandOmset("B", "Stand B (Ketoprak Bu Joko)", 740000, 0.74f, BluePrimary),
            StandOmset("D", "Stand D (Es & Aneka Jus)", 680000, 0.68f, Color(0xFFF97316)),
            StandOmset("E", "Stand E (Snack & Pastry)", 650000, 0.65f, TextMuted)
        )
    }

    var pendingStands by remember {
        mutableStateOf(
            listOf(
                PendingStand("ps1", "Ibu Siti", "Mie Ayam Manggarai", "Makanan Berat"),
                PendingStand("ps2", "Kang Maman", "Cilok Kuah Pedas", "Cemilan")
            )
        )
    }

    var payoutList by remember {
        mutableStateOf(
            listOf(
                PayoutItem("C", "Ayam Geprek 8", 902500),
                PayoutItem("A", "Kebab Bang Ali", 779000),
                PayoutItem("B", "Ketoprak Bu Joko", 703000)
            )
        )
    }

    var violationList by remember {
        mutableStateOf(
            listOf(
                ViolationCase("v1", "Ahmad Fathan", "Kelas XI TKJ 1", "Stand A", 15000, "Belum diambil s.d 11.00 WIB (Istirahat 1)"),
                ViolationCase("v2", "Rangga Aditya", "Kelas X DKV 3", "Stand B", 14000, "Tidak hadir di antrean kantin")
            )
        )
    }

    val coroutineScope = androidx.compose.runtime.rememberCoroutineScope()
    val repository = remember { com.januarzidanetinendeng.eightcanteen.data.repository.CanteenRepository() }

    androidx.compose.runtime.LaunchedEffect(selectedPeriod) {
        repository.getAdminRevenue().onSuccess { res ->
            // Update revenue if available from API
        }
        repository.getAdminViolations().onSuccess { res ->
            res.data?.let { list ->
                if (list.isNotEmpty()) {
                    violationList = list.map { v ->
                        ViolationCase(
                            id = v.id,
                            studentName = v.studentName ?: "Siswa",
                            studentClass = v.studentClass ?: "Kelas Siswa",
                            standName = v.standName ?: "Stand",
                            amount = v.amount ?: 15000,
                            note = v.note ?: "Belum diambil saat istirahat"
                        )
                    }
                }
            }
        }
    }

    Scaffold(
        containerColor = ScreenBg,
        topBar = {
            // Top Bar Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
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
                            text = "ADMIN",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextMuted,
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                // Right Action Icons
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { Toast.makeText(context, "Pencarian Laporan Koperasi", Toast.LENGTH_SHORT).show() }) {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = TextPrimary)
                    }

                    // Notification Bell with Active Indicator Dot
                    Box {
                        IconButton(onClick = { Toast.makeText(context, "3 Kasus Order Tidak Diambil Menunggu Tindakan", Toast.LENGTH_SHORT).show() }) {
                            Icon(imageVector = Icons.Default.Notifications, contentDescription = "Notify", tint = TextPrimary)
                        }
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(top = 10.dp, end = 10.dp)
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFDC2626))
                        )
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
                        Icon(imageVector = Icons.AutoMirrored.Filled.Logout, contentDescription = "Logout", tint = Color(0xFFDC2626), modifier = Modifier.size(18.dp))
                    }
                }
            }
        },
        bottomBar = {
            // Admin Exclusive Bottom Nav
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = selectedNavTab == 0,
                    onClick = { selectedNavTab = 0 },
                    icon = { Icon(imageVector = Icons.Default.Widgets, contentDescription = "Dashboard") },
                    label = { Text("Dashboard", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = BluePrimary, selectedTextColor = BluePrimary, indicatorColor = BlueLightBg)
                )
                NavigationBarItem(
                    selected = selectedNavTab == 1,
                    onClick = { selectedNavTab = 1; Toast.makeText(context, "Membuka Persetujuan Mitra...", Toast.LENGTH_SHORT).show() },
                    icon = { Icon(imageVector = Icons.Default.Group, contentDescription = "Persetujuan") },
                    label = { Text("Approval", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = BluePrimary, selectedTextColor = BluePrimary, indicatorColor = BlueLightBg)
                )
                NavigationBarItem(
                    selected = selectedNavTab == 2,
                    onClick = { selectedNavTab = 2; Toast.makeText(context, "Membuka Riwayat Keuangan...", Toast.LENGTH_SHORT).show() },
                    icon = { Icon(imageVector = Icons.Default.MonetizationOn, contentDescription = "Pencairan") },
                    label = { Text("Keuangan", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = BluePrimary, selectedTextColor = BluePrimary, indicatorColor = BlueLightBg)
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Header Title & Export Button Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Panel Koperasi & Pengelola",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFEAB308))
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "SMKN 8 Jakarta • Realtime Sync",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }

                // Export Button
                OutlinedButton(
                    onClick = {
                        Toast.makeText(context, "Mengekspor Laporan Laba Koperasi (PDF/Excel)...", Toast.LENGTH_SHORT).show()
                    },
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, BlueChipBg),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = BluePrimary),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Download, contentDescription = "Export", modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Ekspor", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 2. Horizontal Date Filter Segmented Pills
            val periodOptions = remember { listOf("Hari Ini", "Kemarin", "Minggu Ini", "Bulan Ini") }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(periodScrollState),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                periodOptions.forEach { period ->
                    val isSelected = selectedPeriod == period || (selectedPeriod == "Minggu Ini" && period.startsWith("Minggu"))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) BluePrimary else Color.White)
                            .border(1.dp, if (isSelected) BluePrimary else BorderColor, RoundedCornerShape(20.dp))
                            .clickable { selectedPeriod = period }
                            .padding(horizontal = 14.dp, vertical = 7.dp)
                    ) {
                        Text(
                            text = if (period == "Minggu Ini") "Minggu Ini (Aktif)" else period,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else TextPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Gross Omset & 5% Koperasi Fee Grid Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Card 1: Omset Bruto
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
                            Text(text = "OMSET BRUTO", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = TextMuted)
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(BlueLightBg),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "🏺", fontSize = 13.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(text = "Rp 3.840k", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = TextPrimary)

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(text = "284 Pesanan", fontSize = 11.sp, color = TextSecondary)
                    }
                }

                // Card 2: Bagi Hasil (5%)
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7).copy(alpha = 0.4f)),
                    border = BorderStroke(1.dp, Color(0xFFFDE047))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "BAGI HASIL (5%)", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFFB45309))
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFEF3C7)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "🏛️", fontSize = 13.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(text = "Rp 192.000", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF92400E))

                        Spacer(modifier = Modifier.height(4.dp))

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFDCFCE7))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(text = "📈 +12% vs minggu lalu", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF16A34A))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))
            
            // NEW: Tambah & Persetujuan Stand Section (Approval List)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = BlueLightBg),
                border = BorderStroke(1.dp, BluePrimary.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Pendaftaran Stand Baru", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = BluePrimary)
                            Text(text = "Menunggu verifikasi admin koperasi", fontSize = 11.sp, color = TextSecondary)
                        }

                        // Add Manual Button
                        IconButton(
                            onClick = { onNavigateToAddStand() },
                            modifier = Modifier.size(36.dp).background(BluePrimary, CircleShape)
                        ) {
                            Icon(imageVector = Icons.Default.AddBusiness, contentDescription = "Add", tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    val unapprovedCount = pendingStands.count { !it.isApproved }
                    if (unapprovedCount == 0) {
                        Text(
                            text = "Tidak ada pendaftaran stand baru saat ini.",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            pendingStands.forEachIndexed { index, ps ->
                                if (!ps.isApproved) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(14.dp))
                                            .background(Color.White)
                                            .border(1.dp, BorderColor, RoundedCornerShape(14.dp))
                                            .padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(text = ps.standName, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                            Text(text = "Oleh: ${ps.ownerName} • ${ps.category}", fontSize = 11.sp, color = TextSecondary)
                                        }

                                        Button(
                                            onClick = {
                                                val updated = pendingStands.toMutableList()
                                                updated[index] = ps.copy(isApproved = true)
                                                pendingStands = updated
                                                Toast.makeText(context, "${ps.standName} Berhasil Di-ACC!", Toast.LENGTH_SHORT).show()
                                            },
                                            shape = RoundedCornerShape(12.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                                            modifier = Modifier.height(32.dp)
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = "ACC", modifier = Modifier.size(14.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(text = "Setujui", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 4. Omset Per Stand Section (Performa 5 Mitra Tenant)
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "📊", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(text = "Omset Per Stand", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Text(text = "Performa 5 mitra tenant kantin", fontSize = 11.sp, color = TextSecondary)
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(BlueLightBg)
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(text = "Tertinggi: C", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = BluePrimary)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Progress Bars for Each Stand
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        standOmsetList.forEach { stand ->
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .clip(CircleShape)
                                                .background(stand.barColor)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(text = stand.name, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                    }

                                    Text(
                                        text = "Rp ${String.format(Locale.GERMANY, "%,d", stand.grossIncome)}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                LinearProgressIndicator(
                                    progress = { stand.progress },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(7.dp)
                                        .clip(RoundedCornerShape(4.dp)),
                                    color = stand.barColor,
                                    trackColor = InputBg
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 5. Siap Dicairkan ke Penjual Section (Disbursement)
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
                        Column {
                            Text(text = "Siap Dicairkan ke Penjual", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text(text = "Hasil potong bagi hasil 5%", fontSize = 11.sp, color = TextSecondary)
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(BlueLightBg)
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(text = "${payoutList.count { !it.isDisbursed }} Antrean", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = BluePrimary)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        payoutList.forEachIndexed { index, payout ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(InputBg)
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(BlueLightCard),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = payout.code, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = BluePrimary)
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Column {
                                        Text(text = payout.standName, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                        Text(
                                            text = "Net: Rp ${String.format(Locale.GERMANY, "%,d", payout.netAmount)}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = BluePrimary
                                        )
                                    }
                                }

                                Button(
                                    onClick = {
                                        val updated = payoutList.toMutableList()
                                        updated[index] = payout.copy(isDisbursed = true)
                                        payoutList = updated
                                        coroutineScope.launch {
                                            repository.getAdminStandRevenue(payout.code)
                                        }
                                        Toast.makeText(context, "Pencairan Net Rp ${payout.netAmount} ke ${payout.standName} Berhasil!", Toast.LENGTH_SHORT).show()
                                    },
                                    enabled = !payout.isDisbursed,
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = BluePrimary,
                                        disabledContainerColor = Color(0xFFDCFCE7),
                                        disabledContentColor = Color(0xFF16A34A)
                                    ),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                    modifier = Modifier.height(34.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = if (payout.isDisbursed) "✓ Cair" else "💵 Cairkan",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 6. Order Tidak Diambil / Violations Section (Verifikasi Pesanan Tunai Hangus)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFFFEF2F2))
                    .border(1.dp, Color(0xFFFCA5A5), RoundedCornerShape(20.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFDC2626)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = Icons.Default.Warning, contentDescription = "Warning", tint = Color.White, modifier = Modifier.size(16.dp))
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(text = "Order Tidak Diambil", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF991B1B))
                                Text(text = "Verifikasi pesanan tunai hangus", fontSize = 10.sp, color = Color(0xFFB91C1C))
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFDC2626))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(text = "${violationList.count { !it.isWarned }} Kasus", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Violation Cases List
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        violationList.forEachIndexed { index, v ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                border = BorderStroke(1.dp, Color(0xFFFECACA))
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = v.studentName, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                        Text(
                                            text = "Rp ${String.format(Locale.GERMANY, "%,d", v.amount)}",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFDC2626)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(2.dp))

                                    Text(
                                        text = "${v.studentClass} • ${v.standName}",
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )

                                    Spacer(modifier = Modifier.height(2.dp))

                                    Text(
                                        text = "⏱️ ${v.note}",
                                        fontSize = 10.sp,
                                        color = TextMuted
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Button(
                                        onClick = {
                                            val updated = violationList.toMutableList()
                                            updated[index] = v.copy(isWarned = true)
                                            violationList = updated
                                            coroutineScope.launch {
                                                repository.addViolation(userId = v.id, points = 5, note = v.note)
                                            }
                                            Toast.makeText(context, "Teguran & Poin Pelanggaran Dikirim ke ${v.studentName}!", Toast.LENGTH_SHORT).show()
                                        },
                                        enabled = !v.isWarned,
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFFDC2626),
                                            disabledContainerColor = Color(0xFFDCFCE7),
                                            disabledContentColor = Color(0xFF16A34A)
                                        ),
                                        modifier = Modifier.fillMaxWidth().height(36.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(imageVector = Icons.Default.Warning, contentDescription = "Warn", modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = if (v.isWarned) "✓ Teguran Terkirim" else "Kirim Teguran / Poin Pelanggaran",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Rules Notice Box
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.White.copy(alpha = 0.8f))
                            .padding(10.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(imageVector = Icons.Default.Info, contentDescription = "Info", tint = BluePrimary, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Siswa yang mendapat 3x teguran berturut-turut akan dinonaktifkan dari metode bayar tunai (COD kantin).",
                            fontSize = 10.sp,
                            color = TextSecondary,
                            lineHeight = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AdminDashboardScreenPreview() {
    EightCanteenTheme {
        AdminDashboardScreen()
    }
}
