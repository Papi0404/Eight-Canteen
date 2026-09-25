package com.januarzidanetinendeng.eightcanteen.ui.stand

import android.widget.Toast
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.januarzidanetinendeng.eightcanteen.ui.components.EKantinLogoIcon
import com.januarzidanetinendeng.eightcanteen.ui.components.ShieldCheckIcon
import com.januarzidanetinendeng.eightcanteen.ui.theme.BlueChipBg
import com.januarzidanetinendeng.eightcanteen.ui.theme.BlueLightBg
import com.januarzidanetinendeng.eightcanteen.ui.theme.BlueLightCard
import com.januarzidanetinendeng.eightcanteen.ui.theme.BluePrimary
import com.januarzidanetinendeng.eightcanteen.ui.theme.BorderColor
import com.januarzidanetinendeng.eightcanteen.ui.theme.CardBg
import com.januarzidanetinendeng.eightcanteen.ui.theme.EightCanteenTheme
import com.januarzidanetinendeng.eightcanteen.ui.theme.InputBg
import com.januarzidanetinendeng.eightcanteen.ui.theme.ScreenBg
import com.januarzidanetinendeng.eightcanteen.ui.theme.TextMuted
import com.januarzidanetinendeng.eightcanteen.ui.theme.TextPrimary
import com.januarzidanetinendeng.eightcanteen.ui.theme.TextSecondary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun StandRegisterScreen(
    onBackClick: () -> Unit = {},
    onRegisterSuccess: (String, String) -> Unit = { _, _ -> }
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    var ownerName by remember { mutableStateOf("") }
    var standName by remember { mutableStateOf("") }
    var counterSlot by remember { mutableStateOf("") }
    var foodCategory by remember { mutableStateOf("") }
    var bankAccount by remember { mutableStateOf("") }

    var isCounterDropdownExpanded by remember { mutableStateOf(false) }
    var isCategoryDropdownExpanded by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val counterOptions = listOf(
        "Loket Stand 01 (Lantai 1 Utama SMKN 8)",
        "Loket Stand 02 (Lantai 1 Utama SMKN 8)",
        "Loket Stand 03 (Lantai 1 Timur SMKN 8)",
        "Loket Stand 04 (Lantai 1 Barat SMKN 8)",
        "Loket Stand 05 (Samping Lapangan Olahraga)",
        "Loket Stand 06 (Area Koperasi Sekolah)"
    )

    val categoryOptions = listOf(
        "Makanan Berat / Nasi & Ayam",
        "Minuman & Es Segar",
        "Gorengan & Makanan Ringan",
        "Mie, Bakso & Soto",
        "Kebab, Burger & Pastry"
    )

    Scaffold(
        containerColor = ScreenBg,
        topBar = {
            // Top Header Navigation Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Back Arrow Button
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

                // Center Title with Logo
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    EKantinLogoIcon(size = 36.dp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "E-KANTIN",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = BluePrimary,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "Daftar Akun Baru",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                }

                // Profile Avatar Button
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(BluePrimary)
                        .clickable { },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profil",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
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
                .padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Step Progress Pill
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(BlueChipBg)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Store,
                        contentDescription = "Tenant",
                        tint = BluePrimary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "REGISTRASI MITRA TENANT • KANTIN SEKOLAH",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = BluePrimary,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Headline Title & Subtitle
            Text(
                text = "Daftarkan Stand Kantin",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Bergabung bersama Koperasi SMKN 8 Jakarta untuk melayani pre-order terjadwal siswa & guru.",
                fontSize = 13.sp,
                color = TextSecondary,
                lineHeight = 18.sp,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(18.dp))

            // 3. Koperasi System Pre-Order Info Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(BlueLightCard)
                    .border(1.dp, BlueChipBg, RoundedCornerShape(18.dp))
                    .padding(14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFDBEAFE)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Restaurant,
                            contentDescription = "Pre-Order",
                            tint = BluePrimary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            ShieldCheckIcon(size = 12.dp, tint = BluePrimary)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "KOPERASI SISWA MANDIRI",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = BluePrimary,
                                letterSpacing = 0.5.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Sistem Pre-Order Digital",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Pesanan diterima sebelum jam istirahat tiba",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 4. Registration Form Fields Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BorderColor, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    // Field 1: Nama Lengkap Pemilik
                    Text(
                        text = "Nama Lengkap Pemilik / Penjual",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = ownerName,
                        onValueChange = { ownerName = it },
                        placeholder = { Text("e.g. Ibu Hj. Maryati / Bang Ali", fontSize = 13.sp, color = TextMuted) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.PersonOutline,
                                contentDescription = "Owner",
                                tint = TextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
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

                    Spacer(modifier = Modifier.height(16.dp))

                    // Field 2: Nama Stand / Toko
                    Text(
                        text = "Nama Stand / Toko Kantin",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = standName,
                        onValueChange = { standName = it },
                        placeholder = { Text("e.g. Kebab Bang Ali / Kantin Bu Joko", fontSize = 13.sp, color = TextMuted) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Store,
                                contentDescription = "Stand",
                                tint = TextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
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

                    Spacer(modifier = Modifier.height(16.dp))

                    // Field 3: Nomor Loket Dropdown
                    Text(
                        text = "Nomor Loket Stand SMKN 8",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = counterSlot,
                            onValueChange = {},
                            readOnly = true,
                            placeholder = { Text("Pilih Loket Kantin", fontSize = 13.sp, color = TextMuted) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Numbers,
                                    contentDescription = "Counter",
                                    tint = TextSecondary,
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Dropdown",
                                    tint = TextSecondary,
                                    modifier = Modifier.size(22.dp)
                                )
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = InputBg,
                                unfocusedContainerColor = InputBg,
                                focusedBorderColor = BluePrimary,
                                unfocusedBorderColor = BorderColor
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .clickable { isCounterDropdownExpanded = true }
                        )

                        DropdownMenu(
                            expanded = isCounterDropdownExpanded,
                            onDismissRequest = { isCounterDropdownExpanded = false },
                            modifier = Modifier
                                .fillMaxWidth(0.85f)
                                .background(Color.White)
                        ) {
                            counterOptions.forEach { opt ->
                                DropdownMenuItem(
                                    text = { Text(text = opt, fontSize = 13.sp) },
                                    onClick = {
                                        counterSlot = opt
                                        isCounterDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Field 4: Kategori Makanan
                    Text(
                        text = "Kategori Makanan Utama",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = foodCategory,
                            onValueChange = {},
                            readOnly = true,
                            placeholder = { Text("Pilih Kategori Makanan", fontSize = 13.sp, color = TextMuted) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Fastfood,
                                    contentDescription = "Category",
                                    tint = TextSecondary,
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Dropdown",
                                    tint = TextSecondary,
                                    modifier = Modifier.size(22.dp)
                                )
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = InputBg,
                                unfocusedContainerColor = InputBg,
                                focusedBorderColor = BluePrimary,
                                unfocusedBorderColor = BorderColor
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .clickable { isCategoryDropdownExpanded = true }
                        )

                        DropdownMenu(
                            expanded = isCategoryDropdownExpanded,
                            onDismissRequest = { isCategoryDropdownExpanded = false },
                            modifier = Modifier
                                .fillMaxWidth(0.85f)
                                .background(Color.White)
                        ) {
                            categoryOptions.forEach { cat ->
                                DropdownMenuItem(
                                    text = { Text(text = cat, fontSize = 13.sp) },
                                    onClick = {
                                        foodCategory = cat
                                        isCategoryDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Field 5: Rekening Pencairan
                    Text(
                        text = "Nomor Rekening / E-Wallet Pencairan",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = bankAccount,
                        onValueChange = { bankAccount = it },
                        placeholder = { Text("Bank DKI / BCA / GoPay / OVO & No. Rek", fontSize = 13.sp, color = TextMuted) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.CreditCard,
                                contentDescription = "Bank",
                                tint = TextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
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

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Pencairan otomatis diproses koperasi sekolah setiap hari Jumat pukul 15.00 WIB.",
                        fontSize = 11.sp,
                        color = TextMuted,
                        lineHeight = 15.sp
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Field 6: Foto Banner Stand Upload Box
                    Text(
                        text = "Foto Banner Stand & Display Makanan",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(BlueLightBg)
                            .border(1.dp, BlueChipBg, RoundedCornerShape(20.dp))
                            .clickable {
                                Toast.makeText(context, "Foto Stand Kantin Berhasil Diunggah!", Toast.LENGTH_SHORT).show()
                            }
                            .padding(18.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFDBEAFE)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AddAPhoto,
                                    contentDescription = "Upload Photo",
                                    tint = BluePrimary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Unggah Foto Stand",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Maksimal 5MB (JPG/PNG). Foto bersih & higienis.",
                                fontSize = 11.sp,
                                color = TextMuted,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Sample Uploaded Thumbnails Row
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color(0xFFFEF3C7)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = "🍲", fontSize = 20.sp)
                                }
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color(0xFFDCFCE7)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = "🍱", fontSize = 20.sp)
                                }
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color(0xFFDBEAFE)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Add More",
                                        tint = BluePrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 5. Verification & Fee Approval Info Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xFFFFEDD5))
                    .border(1.dp, Color(0xFFFED7AA), RoundedCornerShape(18.dp))
                    .padding(14.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.Top) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFEA580C)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "📋", fontSize = 18.sp)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Verifikasi Akun Koperasi",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFC2410C)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Catatan: Akun penjual membutuhkan verifikasi & persetujuan (Approval) Koperasi SMKN 8 sebelum Anda dapat mengatur stok menu dan menerima pesanan siswa.",
                                fontSize = 11.sp,
                                color = Color(0xFF9A3412),
                                lineHeight = 15.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFFED7AA).copy(alpha = 0.6f))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            ShieldCheckIcon(size = 12.dp, tint = Color(0xFFC2410C))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Bagi hasil 5% untuk pemeliharaan fasilitas kantin sekolah",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFC2410C)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 6. Action Button: Kirim Pendaftaran Stand →
            Button(
                onClick = {
                    if (ownerName.trim().isEmpty() || standName.trim().isEmpty()) {
                        Toast.makeText(context, "Lengkapi nama pemilik & nama stand kantin", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    isLoading = true
                    coroutineScope.launch {
                        // Simulate POST /admin/stands API payload execution
                        delay(1200)
                        isLoading = false
                        Toast.makeText(context, "Pendaftaran Stand $standName Berhasil Dikirim ke Koperasi!", Toast.LENGTH_LONG).show()
                        onRegisterSuccess(standName, ownerName)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = !isLoading,
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BluePrimary,
                    contentColor = Color.White
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = Color.White,
                        strokeWidth = 2.5.dp
                    )
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Kirim Pendaftaran Stand",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Send",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 7. Support Link Footer
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Butuh koordinasi atau bantuan pendaftaran?",
                    fontSize = 11.sp,
                    color = TextMuted
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable {
                            Toast.makeText(context, "Menghubungi Pengurus Koperasi SMKN 8...", Toast.LENGTH_SHORT).show()
                        }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.SupportAgent,
                        contentDescription = "Support",
                        tint = BluePrimary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Hubungi Pengurus Koperasi SMKN 8",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = BluePrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun StandRegisterScreenPreview() {
    EightCanteenTheme {
        StandRegisterScreen()
    }
}
