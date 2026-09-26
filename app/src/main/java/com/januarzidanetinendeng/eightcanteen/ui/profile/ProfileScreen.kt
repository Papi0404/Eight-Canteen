package com.januarzidanetinendeng.eightcanteen.ui.profile

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
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.januarzidanetinendeng.eightcanteen.data.local.SessionManager
import com.januarzidanetinendeng.eightcanteen.data.repository.CanteenRepository
import com.januarzidanetinendeng.eightcanteen.ui.theme.BluePrimary
import com.januarzidanetinendeng.eightcanteen.ui.theme.BorderColor
import com.januarzidanetinendeng.eightcanteen.ui.theme.EightCanteenTheme
import com.januarzidanetinendeng.eightcanteen.ui.theme.InputBg
import com.januarzidanetinendeng.eightcanteen.ui.theme.ScreenBg
import com.januarzidanetinendeng.eightcanteen.ui.theme.TextMuted
import com.januarzidanetinendeng.eightcanteen.ui.theme.TextPrimary
import com.januarzidanetinendeng.eightcanteen.ui.theme.TextSecondary
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    currentName: String = "Fajar Pratama",
    currentClass: String = "XII RPL",
    currentPhone: String = "81234567890",
    onBackClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    onSaveSuccess: (String, String) -> Unit = { _, _ -> }
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val repository = remember { CanteenRepository() }

    // Dropdown list kelas & jurusan identik dengan formulir pendaftaran
    val classOptions = remember {
        val levels = listOf("X", "XI", "XII")
        levels.flatMap { level ->
            val majors = if (level == "XII") {
                listOf("AK1", "AK2", "AK3", "MP", "MP2", "BD", "BR1", "BR2", "ULW", "RPL")
            } else {
                listOf("AK1", "AK2", "AK3", "MP", "ManLog", "BD", "BR1", "BR2", "ULW", "RPL")
            }
            majors.map { major -> "$level $major" }
        }
    }

    var nameInput by remember(currentName) { mutableStateOf(currentName) }
    var classInput by remember(currentClass) {
        val cleaned = currentClass.replace(" • SMKN 8", "").trim()
        mutableStateOf(cleaned)
    }
    var isClassDropdownExpanded by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    // Sinkronisasi data profil terbaru langsung dari backend jika sudah tersimpan
    LaunchedEffect(Unit) {
        repository.getMyProfile().onSuccess { res ->
            res.data?.let { profile ->
                val serverName = (profile.fullName ?: profile.name)?.trim()
                if (!serverName.isNullOrBlank() &&
                    !serverName.equals("EMPTY", ignoreCase = true) &&
                    !serverName.equals("Pengguna", ignoreCase = true)
                ) {
                    nameInput = serverName
                }
                val serverClass = (profile.resolvedClass ?: profile.studentClass ?: profile.className)?.trim()
                if (!serverClass.isNullOrBlank()) {
                    classInput = serverClass.replace(" • SMKN 8", "").trim()
                }
            }
        }
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

                    Text(
                        text = "Edit Profil Saya",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                IconButton(onClick = onLogoutClick) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.Logout, contentDescription = "Logout", tint = Color(0xFFDC2626))
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Picture Section
            Box(
                contentAlignment = Alignment.BottomEnd,
                modifier = Modifier.padding(top = 10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFEF3C7))
                        .border(2.dp, BluePrimary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "👨‍🎓", fontSize = 50.sp)
                }

                // Camera Icon Badge
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(BluePrimary)
                        .border(2.dp, Color.White, CircleShape)
                        .clickable { Toast.makeText(context, "Membuka Galeri...", Toast.LENGTH_SHORT).show() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Ganti Foto",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Ketuk ikon kamera untuk mengganti foto profil",
                fontSize = 11.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, BorderColor)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Name Input
                    Text(text = "Nama Lengkap", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        singleLine = true,
                        leadingIcon = { Icon(imageVector = Icons.Default.Person, contentDescription = "Name", tint = TextSecondary) },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = InputBg,
                            unfocusedContainerColor = InputBg,
                            focusedBorderColor = BluePrimary,
                            unfocusedBorderColor = BorderColor
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Class & Jurusan Dropdown Picker
                    Text(text = "Kelas / Jurusan", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = classInput,
                            onValueChange = {},
                            readOnly = true,
                            placeholder = { Text("Pilih Kelas / Jurusan", fontSize = 14.sp, color = TextMuted) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.School,
                                    contentDescription = "Class",
                                    tint = TextSecondary,
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Dropdown",
                                    tint = TextSecondary,
                                    modifier = Modifier.size(24.dp)
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
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Transparent clickable overlay ensuring dropdown opens
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { isClassDropdownExpanded = true }
                        )

                        DropdownMenu(
                            expanded = isClassDropdownExpanded,
                            onDismissRequest = { isClassDropdownExpanded = false },
                            modifier = Modifier
                                .fillMaxWidth(0.85f)
                                .heightIn(max = 280.dp)
                                .background(Color.White)
                        ) {
                            classOptions.forEach { cls ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = cls,
                                            fontSize = 13.sp,
                                            fontWeight = if (classInput == cls) FontWeight.Bold else FontWeight.Normal,
                                            color = if (classInput == cls) BluePrimary else TextPrimary
                                        )
                                    },
                                    onClick = {
                                        classInput = cls
                                        isClassDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Phone (Read-Only)
                    Text(text = "Nomor WhatsApp (Terkunci)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = "+62 $currentPhone",
                        onValueChange = { },
                        readOnly = true,
                        enabled = false,
                        leadingIcon = { Icon(imageVector = Icons.Default.Phone, contentDescription = "Phone", tint = TextMuted) },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledContainerColor = Color(0xFFF1F5F9),
                            disabledBorderColor = BorderColor,
                            disabledTextColor = TextMuted,
                            disabledLeadingIconColor = TextMuted
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = "*Nomor WA tidak bisa diubah karena terhubung dengan database koperasi.", fontSize = 10.sp, color = TextMuted)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Save Button
            Button(
                onClick = {
                    if (nameInput.isBlank()) {
                        Toast.makeText(context, "Nama lengkap tidak boleh kosong!", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (classInput.isBlank()) {
                        Toast.makeText(context, "Silakan pilih kelas / jurusan!", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    isLoading = true
                    coroutineScope.launch {
                        val session = SessionManager.getInstance(context)
                        val cleanName = nameInput.trim()
                        val cleanClass = classInput.trim()

                        val result = repository.updateProfile(
                            name = cleanName,
                            studentClass = cleanClass
                        )
                        isLoading = false

                        result.onSuccess { response ->
                            val updated = response.data
                            val savedName = (updated?.fullName ?: updated?.name ?: cleanName).trim()
                            val savedClass = (updated?.resolvedClass ?: updated?.studentClass ?: updated?.className ?: cleanClass).trim()

                            session.updateProfile(name = savedName, studentClass = savedClass)
                            Toast.makeText(context, response.message ?: "Profil Berhasil Diperbarui!", Toast.LENGTH_SHORT).show()
                            onSaveSuccess(savedName, savedClass)
                        }.onFailure { error ->
                            // Tetap sinkronkan ke lokal dan laporkan respon server
                            session.updateProfile(name = cleanName, studentClass = cleanClass)
                            val errorMsg = error.message ?: "Gagal terhubung ke server"
                            Toast.makeText(context, "Disimpan lokal ($errorMsg)", Toast.LENGTH_SHORT).show()
                            onSaveSuccess(cleanName, cleanClass)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = !isLoading,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BluePrimary,
                    contentColor = Color.White
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Save, contentDescription = "Save", modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Simpan Perubahan", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProfileScreenPreview() {
    EightCanteenTheme {
        ProfileScreen()
    }
}
