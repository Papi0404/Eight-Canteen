package com.januarzidanetinendeng.eightcanteen.ui.admin

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.januarzidanetinendeng.eightcanteen.data.remote.*
import com.januarzidanetinendeng.eightcanteen.data.repository.CanteenRepository
import com.januarzidanetinendeng.eightcanteen.ui.components.EKantinLogoIcon
import com.januarzidanetinendeng.eightcanteen.ui.theme.*
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun AdminDashboardScreen(
    onLogoutClick: () -> Unit = {},
    onNavigateToAddStand: () -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val repository = remember { CanteenRepository() }

    // Navigation State
    var selectedNavTab by remember { mutableIntStateOf(0) } // 0: Keuangan, 1: Stan & Penjual, 2: Siswa, 3: Kelas & Sanksi
    var selectedPeriod by remember { mutableStateOf("today") } // "today" or "all"
    var selectedStandSubTab by remember { mutableIntStateOf(0) } // 0: Daftar Stand, 1: Akun Penjual
    var selectedClassSubTab by remember { mutableIntStateOf(0) } // 0: Rekapitulasi Kelas, 1: Pelanggaran

    // Data States (All 100% Real API)
    var revenueData by remember { mutableStateOf<AdminRevenueResponse?>(null) }
    var standsList by remember { mutableStateOf<List<AdminStandResponse>>(emptyList()) }
    var sellersList by remember { mutableStateOf<List<AdminSellerResponse>>(emptyList()) }
    var studentsList by remember { mutableStateOf<List<AdminStudentResponse>>(emptyList()) }
    var classesList by remember { mutableStateOf<List<AdminClassResponse>>(emptyList()) }
    var violationsList by remember { mutableStateOf<List<ViolationResponse>>(emptyList()) }

    // Loading & Refreshing States
    var isLoading by remember { mutableStateOf(false) }

    // Student Filter States
    var studentSearchQuery by remember { mutableStateOf("") }
    var studentStatusFilter by remember { mutableStateOf("Semua") } // "Semua", "Aktif", "Disuspend"
    var selectedStudentClassFilter by remember { mutableStateOf<String?>(null) }

    // Dialog States
    var selectedStandRevenueDetail by remember { mutableStateOf<StandRevenueDetailResponse?>(null) }
    var editingStand by remember { mutableStateOf<AdminStandResponse?>(null) }
    var editStandName by remember { mutableStateOf("") }
    var editStandSlot by remember { mutableStateOf("") }
    var editStandCategory by remember { mutableStateOf("Makanan") }
    var editStandIsOpen by remember { mutableStateOf(true) }

    var deletingStand by remember { mutableStateOf<AdminStandResponse?>(null) }
    var deletingSeller by remember { mutableStateOf<AdminSellerResponse?>(null) }
    var viewingStandMenuDetail by remember { mutableStateOf<AdminStandDetailResponse?>(null) }

    // Student Dialog States
    var showCreateStudentDialog by remember { mutableStateOf(false) }
    var newStudentName by remember { mutableStateOf("") }
    var newStudentPhone by remember { mutableStateOf("") }
    var newStudentNis by remember { mutableStateOf("") }
    var newStudentClass by remember { mutableStateOf("") }

    var editingStudent by remember { mutableStateOf<AdminStudentResponse?>(null) }
    var editStudentName by remember { mutableStateOf("") }
    var editStudentPhone by remember { mutableStateOf("") }
    var editStudentNis by remember { mutableStateOf("") }
    var editStudentClass by remember { mutableStateOf("") }
    var editStudentPoints by remember { mutableIntStateOf(0) }

    var togglingStudentStatus by remember { mutableStateOf<Pair<AdminStudentResponse, Boolean>?>(null) }
    var deletingStudent by remember { mutableStateOf<AdminStudentResponse?>(null) }
    var viewingStudentDetail by remember { mutableStateOf<AdminStudentDetailResponse?>(null) }

    fun refreshAllData() {
        isLoading = true
        coroutineScope.launch {
            // Load Revenue
            val periodParam = if (selectedPeriod == "today") "today" else null
            repository.getAdminRevenue(period = periodParam).onSuccess { res ->
                revenueData = res.data
            }

            // Load Stands
            repository.adminGetStands().onSuccess { res ->
                standsList = res.data ?: emptyList()
            }

            // Load Sellers
            repository.adminGetSellers().onSuccess { res ->
                sellersList = res.data ?: emptyList()
            }

            // Load Students
            val statusParam = when (studentStatusFilter) {
                "Aktif" -> "active"
                "Disuspend" -> "suspended"
                else -> null
            }
            repository.adminGetStudents(
                search = studentSearchQuery.ifBlank { null },
                className = selectedStudentClassFilter,
                status = statusParam
            ).onSuccess { res ->
                studentsList = res.data ?: emptyList()
            }

            // Load Classes
            repository.adminGetClasses().onSuccess { res ->
                classesList = res.data ?: emptyList()
            }

            // Load Violations
            repository.getAdminViolations().onSuccess { res ->
                violationsList = res.data ?: emptyList()
            }

            isLoading = false
        }
    }

    LaunchedEffect(selectedNavTab, selectedPeriod, studentStatusFilter, selectedStudentClassFilter) {
        refreshAllData()
    }

    Scaffold(
        containerColor = ScreenBg,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    EKantinLogoIcon(size = 38.dp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "E-KANTIN 8",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                color = TextPrimary,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFFDC2626))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "ADMIN",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                            }
                        }
                        Text(
                            text = "Portal Manajemen Koperasi Sekolah",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { refreshAllData() },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .border(1.dp, BorderColor, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh Data",
                            tint = BluePrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = onLogoutClick,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFEE2E2))
                            .border(1.dp, Color(0xFFFCA5A5), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Logout",
                            tint = Color(0xFFDC2626),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp,
                modifier = Modifier.border(1.dp, BorderColor)
            ) {
                NavigationBarItem(
                    selected = selectedNavTab == 0,
                    onClick = { selectedNavTab = 0 },
                    icon = { Icon(Icons.Default.MonetizationOn, contentDescription = "Keuangan") },
                    label = { Text("Keuangan", fontSize = 11.sp, fontWeight = if (selectedNavTab == 0) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = BluePrimary,
                        selectedTextColor = BluePrimary,
                        indicatorColor = BlueLightBg
                    )
                )

                NavigationBarItem(
                    selected = selectedNavTab == 1,
                    onClick = { selectedNavTab = 1 },
                    icon = { Icon(Icons.Default.Storefront, contentDescription = "Stan & Mitra") },
                    label = { Text("Stan & Mitra", fontSize = 11.sp, fontWeight = if (selectedNavTab == 1) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = BluePrimary,
                        selectedTextColor = BluePrimary,
                        indicatorColor = BlueLightBg
                    )
                )

                NavigationBarItem(
                    selected = selectedNavTab == 2,
                    onClick = { selectedNavTab = 2 },
                    icon = { Icon(Icons.Default.Group, contentDescription = "Siswa") },
                    label = { Text("Siswa", fontSize = 11.sp, fontWeight = if (selectedNavTab == 2) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = BluePrimary,
                        selectedTextColor = BluePrimary,
                        indicatorColor = BlueLightBg
                    )
                )

                NavigationBarItem(
                    selected = selectedNavTab == 3,
                    onClick = { selectedNavTab = 3 },
                    icon = { Icon(Icons.Default.Widgets, contentDescription = "Kelas & Sanksi") },
                    label = { Text("Kelas & Sanksi", fontSize = 11.sp, fontWeight = if (selectedNavTab == 3) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = BluePrimary,
                        selectedTextColor = BluePrimary,
                        indicatorColor = BlueLightBg
                    )
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (isLoading) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    color = BluePrimary,
                    trackColor = BlueLightBg
                )
            }

            Box(modifier = Modifier.fillMaxSize()) {
                when (selectedNavTab) {
                    0 -> AdminRevenueTab(
                        revenue = revenueData,
                        selectedPeriod = selectedPeriod,
                        onPeriodChange = { selectedPeriod = it },
                        onNavigateToAddStand = onNavigateToAddStand,
                        onNavigateToStudents = { selectedNavTab = 2 },
                        onStandClick = { standId ->
                            coroutineScope.launch {
                                repository.getAdminStandRevenue(standId).onSuccess { res ->
                                    selectedStandRevenueDetail = res.data
                                }.onFailure { e ->
                                    Toast.makeText(context, "Gagal memuat rincian: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    )

                    1 -> AdminStandsAndSellersTab(
                        stands = standsList,
                        sellers = sellersList,
                        subTab = selectedStandSubTab,
                        onSubTabChange = { selectedStandSubTab = it },
                        onAddNewStandAndSeller = onNavigateToAddStand,
                        onViewMenuDetail = { stand ->
                            coroutineScope.launch {
                                repository.adminGetStandDetail(stand.id).onSuccess { res ->
                                    viewingStandMenuDetail = res.data
                                }.onFailure { e ->
                                    Toast.makeText(context, "Gagal memuat menu: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        onEditStand = { stand ->
                            editingStand = stand
                            editStandName = stand.displayName
                            editStandSlot = stand.displaySlot
                            editStandCategory = stand.category ?: "Makanan"
                            editStandIsOpen = stand.isOpen ?: true
                        },
                        onDeleteStand = { stand ->
                            deletingStand = stand
                        },
                        onDeleteSeller = { seller ->
                            deletingSeller = seller
                        }
                    )

                    2 -> AdminStudentsTab(
                        students = studentsList,
                        classes = classesList,
                        searchQuery = studentSearchQuery,
                        onSearchChange = { studentSearchQuery = it },
                        onSearchSubmit = { refreshAllData() },
                        statusFilter = studentStatusFilter,
                        onStatusFilterChange = { studentStatusFilter = it },
                        classFilter = selectedStudentClassFilter,
                        onClassFilterChange = { selectedStudentClassFilter = it },
                        onAddNewStudent = {
                            newStudentName = ""
                            newStudentPhone = ""
                            newStudentNis = ""
                            newStudentClass = ""
                            showCreateStudentDialog = true
                        },
                        onViewDetail = { student ->
                            coroutineScope.launch {
                                repository.adminGetStudentDetail(student.id).onSuccess { res ->
                                    viewingStudentDetail = res.data
                                }.onFailure { e ->
                                    Toast.makeText(context, "Gagal memuat riwayat: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        onEditStudent = { student ->
                            editingStudent = student
                            editStudentName = student.displayName
                            editStudentPhone = student.phoneNumber ?: ""
                            editStudentNis = student.nis ?: ""
                            editStudentClass = student.displayClass
                            editStudentPoints = student.points ?: 0
                        },
                        onToggleSuspend = { student ->
                            val currentActive = student.isActive ?: true
                            togglingStudentStatus = Pair(student, !currentActive)
                        },
                        onDeleteStudent = { student ->
                            deletingStudent = student
                        }
                    )

                    3 -> AdminClassesAndViolationsTab(
                        classes = classesList,
                        violations = violationsList,
                        subTab = selectedClassSubTab,
                        onSubTabChange = { selectedClassSubTab = it },
                        onSelectClass = { cls ->
                            selectedStudentClassFilter = cls.className
                            selectedNavTab = 2 // Switch to Students tab
                        },
                        onAddViolationPoint = { v ->
                            coroutineScope.launch {
                                val targetId = v.userId ?: v.id
                                repository.addViolation(targetId, points = 1).onSuccess {
                                    Toast.makeText(context, "Sanksi 1 poin berhasil ditambahkan ke ${v.studentName}", Toast.LENGTH_SHORT).show()
                                    refreshAllData()
                                }.onFailure { e ->
                                    Toast.makeText(context, "Gagal: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    )
                }
            }
        }
    }

    // =========================================================================
    // DIALOGS SECTION (REAL ACTIONS)
    // =========================================================================

    // 1. Rincian Saldo Stand (QRIS Siap Cair & Kas Tunai)
    if (selectedStandRevenueDetail != null) {
        val detail = selectedStandRevenueDetail!!
        AlertDialog(
            onDismissRequest = { selectedStandRevenueDetail = null },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color.White,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "💰", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Rincian Saldo Stan", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = detail.standName ?: "Stand Kantin",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = BluePrimary
                    )
                    Text(
                        text = "Pemilik: ${detail.ownerName ?: "-"} (${detail.ownerPhone ?: "-"})",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )

                    HorizontalDivider(color = BorderColor)

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "Saldo QRIS (Siap Cair):", fontSize = 13.sp, color = TextPrimary)
                        Text(
                            text = "Rp ${String.format(Locale.GERMANY, "%,d", detail.qrisBalance ?: 0)}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF16A34A)
                        )
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "Saldo Kas Tunai:", fontSize = 13.sp, color = TextPrimary)
                        Text(
                            text = "Rp ${String.format(Locale.GERMANY, "%,d", detail.cashBalance ?: 0)}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFD97706)
                        )
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "Total Transaksi:", fontSize = 13.sp, color = TextPrimary)
                        Text(text = "${detail.totalOrders ?: 0} Pesanan", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { selectedStandRevenueDetail = null },
                    colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Tutup")
                }
            }
        )
    }

    // 2. Edit Stand Dialog
    if (editingStand != null) {
        val targetStand = editingStand!!
        AlertDialog(
            onDismissRequest = { editingStand = null },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color.White,
            title = {
                Text(text = "Edit Stan Kantin", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = editStandName,
                        onValueChange = { editStandName = it },
                        label = { Text("Nama Stan") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = editStandSlot,
                        onValueChange = { editStandSlot = it },
                        label = { Text("Lokasi / Nomor Slot Stand") },
                        placeholder = { Text("Stand 01 (Gedung C)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = editStandCategory,
                        onValueChange = { editStandCategory = it },
                        label = { Text("Kategori (Makanan / Minuman / Snack)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Status Buka Stan:", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        Switch(
                            checked = editStandIsOpen,
                            onCheckedChange = { editStandIsOpen = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFF16A34A))
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editStandName.isBlank()) {
                            Toast.makeText(context, "Nama stan wajib diisi", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        coroutineScope.launch {
                            repository.adminUpdateStand(
                                standId = targetStand.id,
                                name = editStandName.trim(),
                                counterSlot = editStandSlot.trim(),
                                category = editStandCategory.trim(),
                                isOpen = editStandIsOpen
                            ).onSuccess {
                                Toast.makeText(context, "Stan berhasil diperbarui!", Toast.LENGTH_SHORT).show()
                                editingStand = null
                                refreshAllData()
                            }.onFailure { e ->
                                Toast.makeText(context, "Gagal memperbarui: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Simpan Perubahan")
                }
            },
            dismissButton = {
                TextButton(onClick = { editingStand = null }) {
                    Text("Batal", color = TextSecondary)
                }
            }
        )
    }

    // 3. Delete Stand Dialog (Akun penjual tetap ada)
    if (deletingStand != null) {
        val stand = deletingStand!!
        AlertDialog(
            onDismissRequest = { deletingStand = null },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color.White,
            title = {
                Text(text = "Hapus Stan Kantin?", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFFDC2626))
            },
            text = {
                Column {
                    Text(
                        text = "Apakah Anda yakin ingin menghapus '${stand.displayName}' beserta seluruh menu miliknya?",
                        fontSize = 13.sp,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFFEF3C7))
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "ℹ️ Catatan: Akun penjual '${stand.ownerName ?: "Penjual"}' TETAP TERSIMPAN di sistem dan hanya stan fisiknya yang dihapus.",
                            fontSize = 11.5.sp,
                            color = Color(0xFF92400E)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            repository.adminDeleteStand(stand.id).onSuccess {
                                Toast.makeText(context, "Stan berhasil dihapus. Akun penjual tetap ada.", Toast.LENGTH_SHORT).show()
                                deletingStand = null
                                refreshAllData()
                            }.onFailure { e ->
                                Toast.makeText(context, "Gagal menghapus stan: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Hapus Stan")
                }
            },
            dismissButton = {
                TextButton(onClick = { deletingStand = null }) {
                    Text("Batal", color = TextSecondary)
                }
            }
        )
    }

    // 4. Delete Seller Dialog (Hapus seller + stan miliknya)
    if (deletingSeller != null) {
        val seller = deletingSeller!!
        AlertDialog(
            onDismissRequest = { deletingSeller = null },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color.White,
            title = {
                Text(text = "Hapus Akun Penjual?", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFFDC2626))
            },
            text = {
                Text(
                    text = "Apakah Anda yakin ingin menghapus akun penjual '${seller.displayName}'? Tindakan ini akan menghapus akun beserta seluruh stan dan menu miliknya dari sistem.",
                    fontSize = 13.sp,
                    color = TextPrimary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            repository.adminDeleteSeller(seller.id).onSuccess {
                                Toast.makeText(context, "Akun penjual dan stan miliknya berhasil dihapus.", Toast.LENGTH_SHORT).show()
                                deletingSeller = null
                                refreshAllData()
                            }.onFailure { e ->
                                Toast.makeText(context, "Gagal menghapus penjual: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Hapus Penjual")
                }
            },
            dismissButton = {
                TextButton(onClick = { deletingSeller = null }) {
                    Text("Batal", color = TextSecondary)
                }
            }
        )
    }

    // 5. Viewing Stand Menus Dialog
    if (viewingStandMenuDetail != null) {
        val st = viewingStandMenuDetail!!
        AlertDialog(
            onDismissRequest = { viewingStandMenuDetail = null },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color.White,
            title = {
                Text(text = "Daftar Menu: ${st.name}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
            },
            text = {
                val menus = st.menus ?: emptyList()
                if (menus.isEmpty()) {
                    Text("Belum ada menu yang didaftarkan pada stan ini.", fontSize = 13.sp, color = TextSecondary)
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 300.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        menus.forEach { m ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFFF8FAFC))
                                    .border(1.dp, BorderColor, RoundedCornerShape(10.dp))
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = m.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                    Text(
                                        text = if (m.isAvailable) "Tersedia (Stok: ${m.stock})" else "Habis / Tidak Tersedia",
                                        fontSize = 11.sp,
                                        color = if (m.isAvailable) Color(0xFF16A34A) else Color(0xFFDC2626)
                                    )
                                }
                                Text(
                                    text = "Rp ${String.format(Locale.GERMANY, "%,d", m.price)}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BluePrimary
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewingStandMenuDetail = null },
                    colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Tutup")
                }
            }
        )
    }

    // 6. Create Student Dialog
    if (showCreateStudentDialog) {
        AlertDialog(
            onDismissRequest = { showCreateStudentDialog = false },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color.White,
            title = {
                Text(text = "Tambah Akun Siswa Baru", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newStudentName,
                        onValueChange = { newStudentName = it },
                        label = { Text("Nama Lengkap Siswa") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = newStudentPhone,
                        onValueChange = { newStudentPhone = it },
                        label = { Text("Nomor WhatsApp (Contoh: 08123456789)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = newStudentNis,
                        onValueChange = { newStudentNis = it },
                        label = { Text("NIS Siswa (Opsional)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = newStudentClass,
                        onValueChange = { newStudentClass = it },
                        label = { Text("Kelas (Contoh: XII RPL)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newStudentName.isBlank() || newStudentPhone.isBlank()) {
                            Toast.makeText(context, "Nama dan nomor HP wajib diisi", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        coroutineScope.launch {
                            repository.adminCreateStudent(
                                fullName = newStudentName.trim(),
                                phoneNumber = newStudentPhone.trim(),
                                nis = newStudentNis.trim().ifBlank { null },
                                className = newStudentClass.trim().ifBlank { null }
                            ).onSuccess {
                                Toast.makeText(context, "Akun siswa berhasil dibuat!", Toast.LENGTH_SHORT).show()
                                showCreateStudentDialog = false
                                refreshAllData()
                            }.onFailure { e ->
                                Toast.makeText(context, "Gagal membuat siswa: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Daftarkan Siswa")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateStudentDialog = false }) {
                    Text("Batal", color = TextSecondary)
                }
            }
        )
    }

    // 7. Edit Student Dialog
    if (editingStudent != null) {
        val targetStudent = editingStudent!!
        AlertDialog(
            onDismissRequest = { editingStudent = null },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color.White,
            title = {
                Text(text = "Edit Data Siswa", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = editStudentName,
                        onValueChange = { editStudentName = it },
                        label = { Text("Nama Lengkap") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = editStudentClass,
                        onValueChange = { editStudentClass = it },
                        label = { Text("Kelas") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = editStudentNis,
                        onValueChange = { editStudentNis = it },
                        label = { Text("NIS") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = editStudentPhone,
                        onValueChange = { editStudentPhone = it },
                        label = { Text("Nomor HP") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            repository.adminUpdateStudent(
                                studentId = targetStudent.id,
                                fullName = editStudentName.trim(),
                                phoneNumber = editStudentPhone.trim(),
                                nis = editStudentNis.trim().ifBlank { null },
                                className = editStudentClass.trim().ifBlank { null }
                            ).onSuccess {
                                Toast.makeText(context, "Data siswa berhasil disimpan!", Toast.LENGTH_SHORT).show()
                                editingStudent = null
                                refreshAllData()
                            }.onFailure { e ->
                                Toast.makeText(context, "Gagal mengupdate: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Simpan")
                }
            },
            dismissButton = {
                TextButton(onClick = { editingStudent = null }) {
                    Text("Batal", color = TextSecondary)
                }
            }
        )
    }

    // 8. Suspend / Activate Student Confirmation Dialog
    if (togglingStudentStatus != null) {
        val (student, newStatus) = togglingStudentStatus!!
        AlertDialog(
            onDismissRequest = { togglingStudentStatus = null },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color.White,
            title = {
                Text(
                    text = if (newStatus) "Aktifkan Akun Siswa?" else "Suspend (Nonaktifkan) Akun Siswa?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = if (newStatus) Color(0xFF16A34A) else Color(0xFFDC2626)
                )
            },
            text = {
                Text(
                    text = if (newStatus) {
                        "Apakah Anda yakin ingin mengaktifkan kembali akun '${student.displayName}'? Siswa akan dapat masuk dan memesan makanan kembali."
                    } else {
                        "Apakah Anda yakin ingin menonaktifkan (suspend) akun '${student.displayName}'? Siswa TIDAK akan dapat login hingga diaktifkan kembali oleh Admin."
                    },
                    fontSize = 13.sp,
                    color = TextPrimary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            repository.adminToggleStudentStatus(student.id, newStatus).onSuccess {
                                val msg = if (newStatus) "Akun siswa berhasil diaktifkan kembali!" else "Akun siswa berhasil disuspend!"
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                togglingStudentStatus = null
                                refreshAllData()
                            }.onFailure { e ->
                                Toast.makeText(context, "Gagal mengubah status: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = if (newStatus) Color(0xFF16A34A) else Color(0xFFDC2626)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(if (newStatus) "Aktifkan Sekarang" else "Suspend Akun")
                }
            },
            dismissButton = {
                TextButton(onClick = { togglingStudentStatus = null }) {
                    Text("Batal", color = TextSecondary)
                }
            }
        )
    }

    // 9. Delete Student Dialog
    if (deletingStudent != null) {
        val student = deletingStudent!!
        AlertDialog(
            onDismissRequest = { deletingStudent = null },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color.White,
            title = {
                Text(text = "Hapus Akun Siswa?", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFFDC2626))
            },
            text = {
                Text(
                    text = "Apakah Anda yakin ingin menghapus akun siswa '${student.displayName}'? Seluruh data akun akan dihapus permanen.",
                    fontSize = 13.sp,
                    color = TextPrimary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            repository.adminDeleteStudent(student.id).onSuccess {
                                Toast.makeText(context, "Akun siswa berhasil dihapus.", Toast.LENGTH_SHORT).show()
                                deletingStudent = null
                                refreshAllData()
                            }.onFailure { e ->
                                Toast.makeText(context, "Gagal menghapus siswa: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Hapus Siswa")
                }
            },
            dismissButton = {
                TextButton(onClick = { deletingStudent = null }) {
                    Text("Batal", color = TextSecondary)
                }
            }
        )
    }

    // 10. Student Detail & Order History Dialog
    if (viewingStudentDetail != null) {
        val st = viewingStudentDetail!!
        AlertDialog(
            onDismissRequest = { viewingStudentDetail = null },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color.White,
            title = {
                Text(text = "Detail & Riwayat Siswa", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = st.fullName ?: st.name ?: "Siswa", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = BluePrimary)
                    Text(text = "Kelas: ${st.className ?: "-"}  •  NIS: ${st.nis ?: "-"}  •  HP: ${st.phoneNumber ?: "-"}", fontSize = 11.5.sp, color = TextSecondary)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(text = "Poin Reward: ${st.points ?: 0}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD97706))
                        Text(text = "Pelanggaran: ${st.violationCount ?: 0}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFDC2626))
                    }

                    HorizontalDivider(color = BorderColor)

                    Text(text = "10 Transaksi Terakhir:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    val orders = st.recentOrders ?: emptyList()
                    if (orders.isEmpty()) {
                        Text("Belum ada riwayat transaksi.", fontSize = 12.sp, color = TextMuted)
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 240.dp)
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            orders.forEach { ord ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFFF8FAFC))
                                        .padding(8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(text = "#${ord.orderNumber ?: "-"}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        Text(text = "${ord.paymentMethod ?: "-"} • ${ord.status ?: "-"}", fontSize = 10.sp, color = TextSecondary)
                                    }
                                    Text(
                                        text = "Rp ${String.format(Locale.GERMANY, "%,d", ord.totalAmount ?: 0)}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BluePrimary
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewingStudentDetail = null },
                    colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Tutup")
                }
            }
        )
    }
}

// =============================================================================
// SUB-VIEWS FOR EACH TAB
// =============================================================================

@Composable
fun AdminRevenueTab(
    revenue: AdminRevenueResponse?,
    selectedPeriod: String,
    onPeriodChange: (String) -> Unit,
    onNavigateToAddStand: () -> Unit,
    onNavigateToStudents: () -> Unit,
    onStandClick: (String) -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Period Filter Chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val isToday = selectedPeriod == "today"
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isToday) BluePrimary else Color.White)
                    .border(1.dp, if (isToday) BluePrimary else BorderColor, RoundedCornerShape(20.dp))
                    .clickable { onPeriodChange("today") }
                    .padding(horizontal = 14.dp, vertical = 7.dp)
            ) {
                Text(
                    text = "Hari Ini",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isToday) Color.White else TextPrimary
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (!isToday) BluePrimary else Color.White)
                    .border(1.dp, if (!isToday) BluePrimary else BorderColor, RoundedCornerShape(20.dp))
                    .clickable { onPeriodChange("all") }
                    .padding(horizontal = 14.dp, vertical = 7.dp)
            ) {
                Text(
                    text = "Semua Riwayat Transaksi",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (!isToday) Color.White else TextPrimary
                )
            }
        }

        // 4 Financial Summary Metric Cards
        val gross = revenue?.grossIncome ?: 0L
        val net = revenue?.netIncome ?: 0L
        val fee = revenue?.koperasiFee ?: 0L
        val orders = revenue?.totalOrders ?: 0

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Omset Kotor
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = BluePrimary)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(text = "Omset Kotor", fontSize = 11.sp, color = Color.White.copy(alpha = 0.85f))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Rp ${String.format(Locale.GERMANY, "%,d", gross)}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = "$orders Pesanan Sukses", fontSize = 10.sp, color = Color.White.copy(alpha = 0.75f))
                }
            }

            // Bersih Mitra
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF16A34A))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(text = "Pendapatan Mitra", fontSize = 11.sp, color = Color.White.copy(alpha = 0.85f))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Rp ${String.format(Locale.GERMANY, "%,d", net)}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = "Setelah Bagi Hasil", fontSize = 10.sp, color = Color.White.copy(alpha = 0.75f))
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Kas Koperasi 5%
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, BorderColor)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(text = "Kas Koperasi (5%)", fontSize = 11.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Rp ${String.format(Locale.GERMANY, "%,d", fee)}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFFD97706)
                    )
                }
            }

            // Transaksi Selesai
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, BorderColor)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(text = "Total Transaksi", fontSize = 11.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$orders Transaksi",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary
                    )
                }
            }
        }

        // Quick Actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = onNavigateToAddStand,
                modifier = Modifier.weight(1f).height(44.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.AddBusiness, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "+ Stan & Penjual", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            OutlinedButton(
                onClick = onNavigateToStudents,
                modifier = Modifier.weight(1f).height(44.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, BluePrimary)
            ) {
                Icon(Icons.Default.Group, contentDescription = null, tint = BluePrimary, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Kelola Siswa", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BluePrimary)
            }
        }

        // Stands Revenue Breakdown List
        Text(
            text = "Omset Per Stan Kantin",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        val standsRev = revenue?.standsRevenue ?: emptyList()
        if (standsRev.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Belum ada transaksi pada periode ini.", fontSize = 13.sp, color = TextMuted)
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                standsRev.forEach { st ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onStandClick(st.standId) },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, BorderColor)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = st.standName,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "${st.ownerName ?: "Penjual"} • ${st.counterSlot ?: "Stand 01"}",
                                        fontSize = 11.5.sp,
                                        color = TextSecondary
                                    )
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "Rp ${String.format(Locale.GERMANY, "%,d", st.grossIncome)}",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = BluePrimary
                                    )
                                    Text(
                                        text = "${st.totalOrders ?: 0} Pesanan",
                                        fontSize = 10.5.sp,
                                        color = TextMuted
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            LinearProgressIndicator(
                                progress = (st.progress ?: 0f).coerceIn(0f, 1f),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = BluePrimary,
                                trackColor = Color(0xFFF1F5F9)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun AdminStandsAndSellersTab(
    stands: List<AdminStandResponse>,
    sellers: List<AdminSellerResponse>,
    subTab: Int,
    onSubTabChange: (Int) -> Unit,
    onAddNewStandAndSeller: () -> Unit,
    onViewMenuDetail: (AdminStandResponse) -> Unit,
    onEditStand: (AdminStandResponse) -> Unit,
    onDeleteStand: (AdminStandResponse) -> Unit,
    onDeleteSeller: (AdminSellerResponse) -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Action Button: Add Stand & Seller (Sepaket)
        Button(
            onClick = onAddNewStandAndSeller,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
            shape = RoundedCornerShape(14.dp)
        ) {
            Icon(Icons.Default.AddBusiness, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "+ Tambah Stand & Penjual (Sepaket)", fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }

        // Sub-tabs: Stands vs Sellers
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFE2E8F0))
                .padding(3.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (subTab == 0) Color.White else Color.Transparent)
                    .clickable { onSubTabChange(0) }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Daftar Stan Kantin (${stands.size})",
                    fontSize = 12.sp,
                    fontWeight = if (subTab == 0) FontWeight.Bold else FontWeight.Medium,
                    color = if (subTab == 0) TextPrimary else TextSecondary
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (subTab == 1) Color.White else Color.Transparent)
                    .clickable { onSubTabChange(1) }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Akun Penjual (${sellers.size})",
                    fontSize = 12.sp,
                    fontWeight = if (subTab == 1) FontWeight.Bold else FontWeight.Medium,
                    color = if (subTab == 1) TextPrimary else TextSecondary
                )
            }
        }

        if (subTab == 0) {
            // STANDS LIST
            if (stands.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    Text(text = "Belum ada stand kantin terdaftar.", fontSize = 13.sp, color = TextMuted)
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    stands.forEach { st ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, BorderColor)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = st.displayName,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )

                                    val isOpen = st.isOpen ?: true
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(if (isOpen) Color(0xFFDCFCE7) else Color(0xFFFEE2E2))
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = if (isOpen) "Buka" else "Tutup",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isOpen) Color(0xFF16A34A) else Color(0xFFDC2626)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = "Lokasi: ${st.displaySlot}  •  Kategori: ${st.category ?: "Makanan"}",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )

                                Text(
                                    text = "Pemilik: ${st.ownerName ?: "-"} (WA: ${st.ownerPhone ?: "-"})",
                                    fontSize = 11.5.sp,
                                    color = TextSecondary
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Menu: ${st.menuCount ?: 0} Menu",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = BluePrimary
                                    )

                                    Text(
                                        text = "Hari Ini: Rp ${String.format(Locale.GERMANY, "%,d", st.todayRevenue ?: 0)}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF16A34A)
                                    )
                                }

                                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = BorderColor)

                                // Action Buttons
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = { onViewMenuDetail(st) },
                                        modifier = Modifier.weight(1f).height(36.dp),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(0.dp)
                                    ) {
                                        Text("Lihat Menu", fontSize = 11.sp)
                                    }

                                    OutlinedButton(
                                        onClick = { onEditStand(st) },
                                        modifier = Modifier.weight(1f).height(36.dp),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(0.dp)
                                    ) {
                                        Text("Edit Stand", fontSize = 11.sp)
                                    }

                                    OutlinedButton(
                                        onClick = { onDeleteStand(st) },
                                        modifier = Modifier.weight(1f).height(36.dp),
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626)),
                                        border = BorderStroke(1.dp, Color(0xFFFCA5A5)),
                                        contentPadding = PaddingValues(0.dp)
                                    ) {
                                        Text("Hapus", fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // SELLERS LIST
            if (sellers.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    Text(text = "Belum ada akun penjual terdaftar.", fontSize = 13.sp, color = TextMuted)
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    sellers.forEach { seller ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, BorderColor)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = seller.displayName,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )

                                    val isActive = seller.isActive ?: true
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(if (isActive) Color(0xFFDCFCE7) else Color(0xFFFEE2E2))
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = if (isActive) "Aktif" else "Nonaktif",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isActive) Color(0xFF16A34A) else Color(0xFFDC2626)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(text = "No. WhatsApp: ${seller.phoneNumber ?: "-"}", fontSize = 12.sp, color = TextSecondary)
                                Text(
                                    text = if (seller.stand != null) "Stan: ${seller.stand.name} (${seller.stand.standNumber ?: "-"})" else "Belum memiliki stan",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (seller.stand != null) BluePrimary else Color(0xFFD97706)
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Button(
                                    onClick = { onDeleteSeller(seller) },
                                    modifier = Modifier.fillMaxWidth().height(36.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEE2E2), contentColor = Color(0xFFDC2626)),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Hapus Akun Penjual Beserta Stan", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun AdminStudentsTab(
    students: List<AdminStudentResponse>,
    classes: List<AdminClassResponse>,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onSearchSubmit: () -> Unit,
    statusFilter: String,
    onStatusFilterChange: (String) -> Unit,
    classFilter: String?,
    onClassFilterChange: (String?) -> Unit,
    onAddNewStudent: () -> Unit,
    onViewDetail: (AdminStudentResponse) -> Unit,
    onEditStudent: (AdminStudentResponse) -> Unit,
    onToggleSuspend: (AdminStudentResponse) -> Unit,
    onDeleteStudent: (AdminStudentResponse) -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Search & Add Button Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                placeholder = { Text("Cari nama, NIS, atau HP...", fontSize = 12.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp)) },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = { onSearchChange(""); onSearchSubmit() }) {
                            Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    }
                },
                modifier = Modifier.weight(1f).height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                singleLine = true
            )

            Button(
                onClick = onAddNewStudent,
                modifier = Modifier.height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 14.dp)
            ) {
                Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Tambah", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Status Filter Chips
        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Semua", "Aktif", "Disuspend").forEach { st ->
                val isSelected = statusFilter == st
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(18.dp))
                        .background(if (isSelected) BluePrimary else Color.White)
                        .border(1.dp, if (isSelected) BluePrimary else BorderColor, RoundedCornerShape(18.dp))
                        .clickable { onStatusFilterChange(st) }
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = st,
                        fontSize = 11.5.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color.White else TextPrimary
                    )
                }
            }

            // Class Filter Chips
            if (classFilter != null) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color(0xFFFEF3C7))
                        .border(1.dp, Color(0xFFFDE68A), RoundedCornerShape(18.dp))
                        .clickable { onClassFilterChange(null) }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Kelas: $classFilter ✕",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF92400E)
                    )
                }
            }
        }

        Text(
            text = "Total ${students.size} Siswa Ditemukan",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextSecondary
        )

        // Students List
        if (students.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(40.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Tidak ada data siswa yang cocok.", fontSize = 13.sp, color = TextMuted)
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                students.forEach { st ->
                    val isActive = st.isActive ?: true
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, if (isActive) BorderColor else Color(0xFFFCA5A5))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = st.displayName,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "Kelas: ${st.displayClass}  •  NIS: ${st.nis ?: "-"}  •  WA: ${st.phoneNumber ?: "-"}",
                                        fontSize = 11.5.sp,
                                        color = TextSecondary
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isActive) Color(0xFFDCFCE7) else Color(0xFFFEE2E2))
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = if (isActive) "AKTIF" else "DISUSPEND",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (isActive) Color(0xFF16A34A) else Color(0xFFDC2626)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text(
                                    text = "🪙 ${st.points ?: 0} Poin",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFFD97706)
                                )
                                Text(
                                    text = "⚠️ ${st.violationCount ?: 0} Pelanggaran",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if ((st.violationCount ?: 0) > 0) Color(0xFFDC2626) else TextMuted
                                )
                            }

                            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = BorderColor)

                            // Actions Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { onViewDetail(st) },
                                    modifier = Modifier.weight(1f).height(34.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text("Riwayat", fontSize = 10.5.sp)
                                }

                                OutlinedButton(
                                    onClick = { onEditStudent(st) },
                                    modifier = Modifier.weight(1f).height(34.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text("Edit", fontSize = 10.5.sp)
                                }

                                Button(
                                    onClick = { onToggleSuspend(st) },
                                    modifier = Modifier.weight(1.2f).height(34.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isActive) Color(0xFFFEE2E2) else Color(0xFFDCFCE7),
                                        contentColor = if (isActive) Color(0xFFDC2626) else Color(0xFF16A34A)
                                    ),
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text(if (isActive) "Suspend" else "Aktifkan", fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                                }

                                OutlinedButton(
                                    onClick = { onDeleteStudent(st) },
                                    modifier = Modifier.weight(0.8f).height(34.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626)),
                                    border = BorderStroke(1.dp, Color(0xFFFCA5A5)),
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text("Hapus", fontSize = 10.5.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun AdminClassesAndViolationsTab(
    classes: List<AdminClassResponse>,
    violations: List<ViolationResponse>,
    subTab: Int,
    onSubTabChange: (Int) -> Unit,
    onSelectClass: (AdminClassResponse) -> Unit,
    onAddViolationPoint: (ViolationResponse) -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Sub-tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFE2E8F0))
                .padding(3.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (subTab == 0) Color.White else Color.Transparent)
                    .clickable { onSubTabChange(0) }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Rekapitulasi Kelas (${classes.size})",
                    fontSize = 12.sp,
                    fontWeight = if (subTab == 0) FontWeight.Bold else FontWeight.Medium,
                    color = if (subTab == 0) TextPrimary else TextSecondary
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (subTab == 1) Color.White else Color.Transparent)
                    .clickable { onSubTabChange(1) }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Pelanggaran Kantin (${violations.size})",
                    fontSize = 12.sp,
                    fontWeight = if (subTab == 1) FontWeight.Bold else FontWeight.Medium,
                    color = if (subTab == 1) TextPrimary else TextSecondary
                )
            }
        }

        if (subTab == 0) {
            // CLASSES LIST
            if (classes.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    Text(text = "Belum ada data kelas.", fontSize = 13.sp, color = TextMuted)
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    classes.forEach { cls ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelectClass(cls) },
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, BorderColor)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(BlueLightBg),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = "🏫", fontSize = 16.sp)
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = cls.className,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                        Text(
                                            text = "Klik untuk melihat daftar siswa",
                                            fontSize = 11.sp,
                                            color = TextSecondary
                                        )
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(BlueLightCard)
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "${cls.studentCount} Siswa",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BluePrimary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // VIOLATIONS LIST
            if (violations.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    Text(text = "Tidak ada siswa yang memiliki catatan pelanggaran.", fontSize = 13.sp, color = TextMuted)
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    violations.forEach { v ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, Color(0xFFFECACA))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = v.studentName ?: "Siswa",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Color(0xFFDC2626))
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "${v.amount ?: 1} Pelanggaran",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = "Kelas: ${v.studentClass ?: "-"}  •  Stan: ${v.standName ?: "-"}",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )

                                if (!v.note.isNullOrBlank()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Catatan: ${v.note}",
                                        fontSize = 11.sp,
                                        color = Color(0xFFB45309)
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Button(
                                    onClick = { onAddViolationPoint(v) },
                                    modifier = Modifier.fillMaxWidth().height(36.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.Warning, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Tambah Sanksi / Poin Pelanggaran (+1)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}
