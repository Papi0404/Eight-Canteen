package com.januarzidanetinendeng.eightcanteen.ui.otp

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.januarzidanetinendeng.eightcanteen.data.local.SessionManager
import com.januarzidanetinendeng.eightcanteen.data.remote.ApiConfig
import com.januarzidanetinendeng.eightcanteen.data.repository.CanteenRepository
import com.januarzidanetinendeng.eightcanteen.ui.components.EKantinLogoIcon
import com.januarzidanetinendeng.eightcanteen.ui.components.ShieldCheckIcon
import com.januarzidanetinendeng.eightcanteen.ui.theme.BlueChipBg
import com.januarzidanetinendeng.eightcanteen.ui.theme.BlueLightBg
import com.januarzidanetinendeng.eightcanteen.ui.theme.BluePrimary
import com.januarzidanetinendeng.eightcanteen.ui.theme.BorderColor
import com.januarzidanetinendeng.eightcanteen.ui.theme.EightCanteenTheme
import com.januarzidanetinendeng.eightcanteen.ui.theme.InputBg
import com.januarzidanetinendeng.eightcanteen.ui.theme.ScreenBg
import com.januarzidanetinendeng.eightcanteen.ui.theme.TextMuted
import com.januarzidanetinendeng.eightcanteen.ui.theme.TextPrimary
import com.januarzidanetinendeng.eightcanteen.ui.theme.TextSecondary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun OtpVerificationScreen(
    phoneNumber: String = "812-3456-7890",
    onBackClick: () -> Unit = {},
    onEditPhoneClick: () -> Unit = {},
    onVerificationSuccess: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    var otpCode by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var timerSeconds by remember { mutableIntStateOf(58) }

    // Intercept system back button/gesture to return to previous screen
    BackHandler {
        onBackClick()
    }

    // Countdown timer for resend OTP
    LaunchedEffect(timerSeconds) {
        if (timerSeconds > 0) {
            delay(1000)
            timerSeconds--
        }
    }

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
                            text = "Verifikasi Otp",
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
            Spacer(modifier = Modifier.height(8.dp))

            // 1. WhatsApp Security Header Icon
            OtpIllustrationHeader()

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Headline Title & Subtitle
            Text(
                text = "Verifikasi Nomor WhatsApp",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Kami telah mengirim 4 digit kode rahasia E-\nKantin ke akun WhatsApp kamu",
                fontSize = 13.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 3. Phone Number Pill Badge + Ubah Link
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(30.dp))
                    .background(BlueLightBg)
                    .border(1.dp, BlueChipBg, RoundedCornerShape(30.dp))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = "Phone",
                        tint = BluePrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "+62 $phoneNumber",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Row(
                        modifier = Modifier.clickable { onEditPhoneClick() },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Ubah",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = BluePrimary
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Phone",
                            tint = BluePrimary,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // 4. 4-Digit OTP Boxes
            OtpDigitRow(otpCode = otpCode, isError = errorMessage != null)

            Spacer(modifier = Modifier.height(10.dp))

            // Error Warning Banner
            AnimatedVisibility(
                visible = errorMessage != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFFEF2F2))
                        .border(1.dp, Color(0xFFFCA5A5), RoundedCornerShape(14.dp))
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Error",
                            tint = Color(0xFFDC2626),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = errorMessage ?: "Kode OTP salah",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFB91C1C)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 5. Official Koperasi Notice Chip
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFFFEF9C3))
                    .border(1.dp, Color(0xFFFEF08A), RoundedCornerShape(20.dp))
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🔒", fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Cek chat resmi dari Koperasi SMKN 8",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF854D0E)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 6. Action Button: Verifikasi & Lanjutkan
            Button(
                onClick = {
                    if (otpCode.length < 4) {
                        errorMessage = "Masukkan 4 digit OTP terlebih dahulu"
                        Toast.makeText(context, "Masukkan 4 digit OTP terlebih dahulu", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    isLoading = true
                    errorMessage = null
                    coroutineScope.launch {
                        val repository = CanteenRepository()
                        val cleanPhone = phoneNumber.replace("-", "").trim()
                        val result = repository.verifyOtp(cleanPhone, otpCode)
                        isLoading = false
                        result.onSuccess { response ->
                            val loginData = response.data
                            if (loginData != null) {
                                // Simpan JWT token ke SessionManager & ApiConfig
                                val session = SessionManager.getInstance(context)
                                session.saveAuthToken(loginData.token)
                                session.saveUser(
                                    id = loginData.user.id,
                                    name = loginData.user.fullName ?: loginData.user.name ?: "User",
                                    role = loginData.user.role ?: "Siswa",
                                    phone = cleanPhone,
                                    standId = loginData.user.stand?.id,
                                    points = loginData.user.points ?: 0
                                )
                                ApiConfig.setAuthToken(loginData.token)
                            }
                            errorMessage = null
                            Toast.makeText(context, response.message ?: "Verifikasi Berhasil!", Toast.LENGTH_SHORT).show()
                            onVerificationSuccess(otpCode)
                        }.onFailure { e ->
                            val errorMsg = e.message ?: "Kode OTP salah atau telah kadaluarsa"
                            errorMessage = errorMsg
                            otpCode = "" // Reset kode agar user bisa mengetik ulang dengan mudah
                            Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
                            // PENTING: JANGAN panggil onVerificationSuccess jika OTP gagal!
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = !isLoading,
                shape = RoundedCornerShape(25.dp),
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
                            text = "Verifikasi & Lanjutkan",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Lanjutkan",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 7. Resend Timer & Action Options
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "⏱️ Belum terima kode? Kirim ulang dalam ",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                if (timerSeconds > 0) {
                    Text(
                        text = "00:${if (timerSeconds < 10) "0$timerSeconds" else timerSeconds}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                } else {
                    Text(
                        text = "Kirim Sekarang",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = BluePrimary,
                        modifier = Modifier.clickable {
                            timerSeconds = 58
                            Toast.makeText(context, "OTP baru dikirim via WhatsApp!", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Alternative Resend Buttons (SMS / CS)
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(InputBg)
                        .clickable {
                            Toast.makeText(context, "Mencoba kirim OTP via SMS...", Toast.LENGTH_SHORT).show()
                        }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Sms,
                            contentDescription = "SMS",
                            tint = TextSecondary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Kirim via SMS",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(InputBg)
                        .clickable {
                            Toast.makeText(context, "Menghubungi Bantuan CS Kantin...", Toast.LENGTH_SHORT).show()
                        }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.SupportAgent,
                            contentDescription = "CS",
                            tint = TextSecondary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Bantuan CS",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 8. Custom On-Screen Keypad Component
            NumericKeypad(
                onNumberClick = { num ->
                    if (otpCode.length < 4) {
                        otpCode += num
                        errorMessage = null
                    }
                },
                onBackspaceClick = {
                    if (otpCode.isNotEmpty()) {
                        otpCode = otpCode.dropLast(1)
                        errorMessage = null
                    }
                },
                onBiometricClick = {
                    Toast.makeText(context, "Otentikasi Biometrik Aktif", Toast.LENGTH_SHORT).show()
                }
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun OtpIllustrationHeader() {
    Box(
        modifier = Modifier
            .size(90.dp)
            .shadow(6.dp, RoundedCornerShape(24.dp), ambientColor = Color(0x222563EB))
            .clip(RoundedCornerShape(24.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFE0F2FE),
                        Color(0xFFEFF6FF),
                        Color(0xFFDBEAFE)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // WhatsApp Message Chat Icon inside rounded square
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFF22C55E)),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(24.dp)) {
                val w = size.width
                val h = size.height

                // White Chat bubble outline with text lines
                val path = Path().apply {
                    moveTo(w * 0.1f, h * 0.2f)
                    lineTo(w * 0.9f, h * 0.2f)
                    lineTo(w * 0.9f, h * 0.7f)
                    lineTo(w * 0.35f, h * 0.7f)
                    lineTo(w * 0.15f, h * 0.9f)
                    lineTo(w * 0.15f, h * 0.7f)
                    lineTo(w * 0.1f, h * 0.7f)
                    close()
                }
                drawPath(path, Color.White)

                // Green text lines inside bubble
                drawLine(Color(0xFF22C55E), Offset(w * 0.25f, h * 0.38f), Offset(w * 0.75f, h * 0.38f), strokeWidth = 5f)
                drawLine(Color(0xFF22C55E), Offset(w * 0.25f, h * 0.52f), Offset(w * 0.60f, h * 0.52f), strokeWidth = 5f)
            }
        }

        // Blue Shield Check Badge attached to bottom right
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(28.dp)
                .clip(CircleShape)
                .background(BluePrimary)
                .border(2.dp, Color.White, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            ShieldCheckIcon(size = 14.dp, tint = Color.White)
        }
    }
}

@Composable
fun OtpDigitRow(otpCode: String, isError: Boolean = false) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0 until 4) {
            val isFilled = i < otpCode.length
            val isCurrentFocused = i == otpCode.length
            val char = if (isFilled) otpCode[i].toString() else ""

            val bgColor = when {
                isError -> Color(0xFFFEF2F2)
                isCurrentFocused -> BlueLightBg
                isFilled -> Color.White
                else -> Color.White
            }

            val borderColor = when {
                isError -> Color(0xFFDC2626)
                isCurrentFocused -> BluePrimary
                isFilled -> Color(0xFFCBD5E1)
                else -> BorderColor
            }

            Box(
                modifier = Modifier
                    .size(width = 62.dp, height = 64.dp)
                    .shadow(elevation = if (isFilled || isCurrentFocused) 4.dp else 1.dp, shape = RoundedCornerShape(16.dp), ambientColor = if (isError) Color(0x33DC2626) else Color(0x152563EB))
                    .clip(RoundedCornerShape(16.dp))
                    .background(bgColor)
                    .border(
                        width = if (isError || isCurrentFocused) 2.dp else 1.dp,
                        color = borderColor,
                        shape = RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isFilled) {
                    Text(
                        text = char,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isError) Color(0xFFDC2626) else BluePrimary
                    )
                } else if (isCurrentFocused) {
                    // Vertical Cursor Line |
                    Box(
                        modifier = Modifier
                            .width(2.dp)
                            .height(24.dp)
                            .background(if (isError) Color(0xFFDC2626) else BluePrimary)
                    )
                } else {
                    // Empty dash -
                    Text(
                        text = "-",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isError) Color(0xFFFCA5A5) else TextMuted
                    )
                }
            }
        }
    }
}

@Composable
fun NumericKeypad(
    onNumberClick: (String) -> Unit,
    onBackspaceClick: () -> Unit,
    onBiometricClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFFF1F5F9))
            .padding(14.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Row 1: 1, 2, 3
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                KeypadButton("1", modifier = Modifier.weight(1f)) { onNumberClick("1") }
                KeypadButton("2", modifier = Modifier.weight(1f)) { onNumberClick("2") }
                KeypadButton("3", modifier = Modifier.weight(1f)) { onNumberClick("3") }
            }
            // Row 2: 4, 5, 6
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                KeypadButton("4", modifier = Modifier.weight(1f)) { onNumberClick("4") }
                KeypadButton("5", modifier = Modifier.weight(1f)) { onNumberClick("5") }
                KeypadButton("6", modifier = Modifier.weight(1f)) { onNumberClick("6") }
            }
            // Row 3: 7, 8, 9
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                KeypadButton("7", modifier = Modifier.weight(1f)) { onNumberClick("7") }
                KeypadButton("8", modifier = Modifier.weight(1f)) { onNumberClick("8") }
                KeypadButton("9", modifier = Modifier.weight(1f)) { onNumberClick("9") }
            }
            // Row 4: Biometric/Extra, 0, Backspace
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { onBiometricClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Fingerprint,
                        contentDescription = "Biometric",
                        tint = TextMuted,
                        modifier = Modifier.size(22.dp)
                    )
                }

                KeypadButton("0", modifier = Modifier.weight(1f)) { onNumberClick("0") }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .shadow(2.dp, RoundedCornerShape(14.dp))
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White)
                        .clickable { onBackspaceClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Backspace,
                        contentDescription = "Backspace",
                        tint = TextPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun KeypadButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(52.dp)
            .shadow(2.dp, RoundedCornerShape(14.dp))
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun OtpVerificationScreenPreview() {
    EightCanteenTheme {
        OtpVerificationScreen()
    }
}
