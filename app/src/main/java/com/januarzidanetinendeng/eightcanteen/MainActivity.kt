package com.januarzidanetinendeng.eightcanteen

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.januarzidanetinendeng.eightcanteen.auth.AuthViewModel
import com.januarzidanetinendeng.eightcanteen.auth.UserRole
import com.januarzidanetinendeng.eightcanteen.ui.admin.AdminAddStandScreen
import com.januarzidanetinendeng.eightcanteen.ui.admin.AdminDashboardScreen
import com.januarzidanetinendeng.eightcanteen.ui.checkout.CartViewModel
import com.januarzidanetinendeng.eightcanteen.ui.checkout.CheckoutScreen
import com.januarzidanetinendeng.eightcanteen.ui.dashboard.StudentDashboardScreen
import com.januarzidanetinendeng.eightcanteen.ui.login.LoginAdminScreen
import com.januarzidanetinendeng.eightcanteen.ui.login.LoginScreen
import com.januarzidanetinendeng.eightcanteen.ui.otp.OtpVerificationScreen
import com.januarzidanetinendeng.eightcanteen.ui.payment.PayAtCounterScreen
import com.januarzidanetinendeng.eightcanteen.ui.payment.QrisPaymentScreen
import com.januarzidanetinendeng.eightcanteen.ui.payment.StrukLunasScreen
import com.januarzidanetinendeng.eightcanteen.ui.points.PointsRewardScreen
import com.januarzidanetinendeng.eightcanteen.ui.register.StudentRegisterScreen
import com.januarzidanetinendeng.eightcanteen.ui.seller.SellerDashboardScreen
import com.januarzidanetinendeng.eightcanteen.ui.stand.StandRegisterScreen
import com.januarzidanetinendeng.eightcanteen.ui.theme.EightCanteenTheme

enum class ScreenState {
    LOGIN,               // Halaman 1: Login Siswa
    LOGIN_ADMIN,         // Form Otentikasi Admin (WA + OTP)
    OTP_VERIFICATION,    // Halaman 2: Verifikasi OTP Siswa
    STUDENT_REGISTER,    // Halaman 3: Registrasi Siswa
    HOME_LOGGED_IN,      // Beranda / Dashboard Utama
    CHECKOUT,            // Checkout Pesanan
    QRIS_PAYMENT,        // Pembayaran QRIS Dinamis
    PAY_AT_COUNTER,      // Pembayaran Cash / Loket Stand
    STRUK_LUNAS,         // Struk Lunas & Barcode Pickup
    POINTS_REWARD,       // Hadiah & Poin Kantin
    STAND_REGISTER,      // Pendaftaran Stand Baru
    ADMIN_ADD_STAND,     // Admin Tambah Stand
    SELLER_DASHBOARD,    // Dashboard Penjual Stand
    ADMIN_DASHBOARD      // Dashboard Admin (Protected Route)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EightCanteenTheme {
                MainAppNavigation()
            }
        }
    }
}

@Composable
fun MainAppNavigation() {
    val context = LocalContext.current

    // Start Destination diatur ke Halaman 1 (LOGIN)
    var currentScreen by remember { mutableStateOf(ScreenState.LOGIN) }

    // User Session & Role ViewModel
    val authViewModel: AuthViewModel = remember { AuthViewModel() }
    val currentUserRole by authViewModel.currentUserRole.collectAsState()
    val userName by authViewModel.userName.collectAsState()
    val userClass by authViewModel.userClass.collectAsState()

    var userPhoneNumber by remember { mutableStateOf("812-3456-7890") }
    var sellerStandName by remember { mutableStateOf("Kebab Bang Ali") }
    var sellerCounterSlot by remember { mutableStateOf("Stand 04") }

    // Shared CartViewModel (Single Source of Truth)
    val cartViewModel: CartViewModel = remember { CartViewModel() }

    when (currentScreen) {
        // ---------------------------------------------------------------------
        // HALAMAN 1: LOGIN SISWA
        // ---------------------------------------------------------------------
        ScreenState.LOGIN -> {
            LoginScreen(
                initialPhoneNumber = userPhoneNumber,
                onNavigateToAdmin = {
                    currentScreen = ScreenState.LOGIN_ADMIN // Masuk ke Form OTP Admin Khusus
                },
                onNavigateToRegister = {
                    currentScreen = ScreenState.STUDENT_REGISTER
                },
                onRequestOtpSuccess = { phone ->
                    userPhoneNumber = if (phone.isNotBlank()) phone else "812-3456-7890"
                    currentScreen = ScreenState.OTP_VERIFICATION
                }
            )
        }

        // ---------------------------------------------------------------------
        // FORM PORTAL LOGIN ADMIN (VERIFIKASI WA ADMIN & OTP 4-DIGIT)
        // ---------------------------------------------------------------------
        ScreenState.LOGIN_ADMIN -> {
            LoginAdminScreen(
                authViewModel = authViewModel,
                onBackClick = {
                    currentScreen = ScreenState.LOGIN
                },
                onAdminAuthSuccess = {
                    currentScreen = ScreenState.ADMIN_DASHBOARD
                }
            )
        }

        // ---------------------------------------------------------------------
        // HALAMAN 2: VERIFIKASI OTP SISWA
        // ---------------------------------------------------------------------
        ScreenState.OTP_VERIFICATION -> {
            OtpVerificationScreen(
                phoneNumber = userPhoneNumber,
                onBackClick = {
                    currentScreen = ScreenState.LOGIN
                },
                onEditPhoneClick = {
                    currentScreen = ScreenState.LOGIN
                },
                onVerificationSuccess = { _ ->
                    currentScreen = ScreenState.STUDENT_REGISTER
                }
            )
        }

        // ---------------------------------------------------------------------
        // HALAMAN 3: REGISTRASI DATA SISWA & KELAS
        // ---------------------------------------------------------------------
        ScreenState.STUDENT_REGISTER -> {
            StudentRegisterScreen(
                verifiedPhoneNumber = userPhoneNumber,
                onBackClick = {
                    currentScreen = ScreenState.OTP_VERIFICATION
                },
                onRegisterSuccess = { name, className ->
                    authViewModel.loginAsStudent(name, className, userPhoneNumber)
                    currentScreen = ScreenState.HOME_LOGGED_IN
                    Toast.makeText(context, "Selamat datang, $name!", Toast.LENGTH_SHORT).show()
                }
            )
        }

        // ---------------------------------------------------------------------
        // BERANDA / DASHBOARD UTAMA ("beranda")
        // ---------------------------------------------------------------------
        ScreenState.HOME_LOGGED_IN -> {
            StudentDashboardScreen(
                cartViewModel = cartViewModel,
                userRole = currentUserRole,
                studentName = userName,
                studentClass = userClass,
                loyaltyPoints = 25,
                onPointsClick = {
                    currentScreen = ScreenState.POINTS_REWARD
                },
                onNavigateToAdmin = {
                    if (currentUserRole == UserRole.ADMIN) {
                        currentScreen = ScreenState.ADMIN_DASHBOARD
                    } else {
                        Toast.makeText(
                            context,
                            "Akses Ditolak: Area Khusus Administrator Koperasi SMKN 8",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                },
                onCheckoutClick = {
                    currentScreen = ScreenState.CHECKOUT
                }
            )
        }

        // ---------------------------------------------------------------------
        // CHECKOUT SCREEN ("checkout")
        // ---------------------------------------------------------------------
        ScreenState.CHECKOUT -> {
            CheckoutScreen(
                viewModel = cartViewModel,
                onBackClick = {
                    currentScreen = ScreenState.HOME_LOGGED_IN
                },
                onNavigateToQris = {
                    currentScreen = ScreenState.QRIS_PAYMENT
                },
                onNavigateToCash = {
                    currentScreen = ScreenState.PAY_AT_COUNTER
                },
                onPaymentComplete = {
                    currentScreen = ScreenState.STRUK_LUNAS
                }
            )
        }

        // ---------------------------------------------------------------------
        // LAYAR PEMBAYARAN QRIS ("qris_payment")
        // ---------------------------------------------------------------------
        ScreenState.QRIS_PAYMENT -> {
            QrisPaymentScreen(
                viewModel = cartViewModel,
                onBackClick = {
                    currentScreen = ScreenState.CHECKOUT
                },
                onPaymentSuccess = {
                    currentScreen = ScreenState.STRUK_LUNAS
                }
            )
        }

        // ---------------------------------------------------------------------
        // LAYAR PEMBAYARAN CASH DI LOKET ("pay_at_counter")
        // ---------------------------------------------------------------------
        ScreenState.PAY_AT_COUNTER -> {
            PayAtCounterScreen(
                viewModel = cartViewModel,
                onBackClick = {
                    currentScreen = ScreenState.CHECKOUT
                },
                onConfirmCashPayment = {
                    currentScreen = ScreenState.STRUK_LUNAS
                },
                onCancelOrder = {
                    currentScreen = ScreenState.HOME_LOGGED_IN
                }
            )
        }

        // ---------------------------------------------------------------------
        // STRUK LUNAS & BARCODE PICKUP ("struk_lunas")
        // ---------------------------------------------------------------------
        ScreenState.STRUK_LUNAS -> {
            StrukLunasScreen(
                viewModel = cartViewModel,
                onBackClick = {
                    currentScreen = ScreenState.HOME_LOGGED_IN
                },
                onBackToHome = {
                    currentScreen = ScreenState.HOME_LOGGED_IN
                }
            )
        }

        // ---------------------------------------------------------------------
        // HADIAH & POIN KANTIN
        // ---------------------------------------------------------------------
        ScreenState.POINTS_REWARD -> {
            PointsRewardScreen(
                studentName = userName,
                studentClass = userClass.take(12),
                currentPoints = 25,
                onBackClick = {
                    currentScreen = ScreenState.HOME_LOGGED_IN
                },
                onNavigateToHome = {
                    currentScreen = ScreenState.HOME_LOGGED_IN
                },
                onNavigateToAdmin = {
                    if (currentUserRole == UserRole.ADMIN) {
                        currentScreen = ScreenState.ADMIN_DASHBOARD
                    } else {
                        Toast.makeText(
                            context,
                            "Akses Ditolak: Area Khusus Administrator Koperasi SMKN 8",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            )
        }

        // ---------------------------------------------------------------------
        // SELLER DASHBOARD (PENJUAL STAND)
        // ---------------------------------------------------------------------
        ScreenState.SELLER_DASHBOARD -> {
            SellerDashboardScreen(
                standName = sellerStandName,
                counterSlot = sellerCounterSlot,
                todayIncome = 150000,
                completedOrders = 18,
                activeQueueCount = 12,
                readyCount = 4,
                cookingCount = 8,
                averagePrepMinutes = 7,
                onScanQrClick = {
                    Toast.makeText(context, "Kamera QR Scanner Pengambilan Pesanan Siswa Aktif", Toast.LENGTH_SHORT).show()
                },
                onNavigateToAdmin = {
                    if (currentUserRole == UserRole.ADMIN) {
                        currentScreen = ScreenState.ADMIN_DASHBOARD
                    } else {
                        Toast.makeText(
                            context,
                            "Akses Ditolak: Area Khusus Administrator Koperasi SMKN 8",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                },
                onNavigateToHome = {
                    currentScreen = ScreenState.HOME_LOGGED_IN
                }
            )
        }

        // ---------------------------------------------------------------------
        // ADMIN DASHBOARD (PROTECTED ROUTE GUARD)
        // ---------------------------------------------------------------------
        ScreenState.ADMIN_DASHBOARD -> {
            if (currentUserRole != UserRole.ADMIN) {
                Toast.makeText(
                    context,
                    "Akses Ditolak: Area Khusus Administrator Koperasi SMKN 8",
                    Toast.LENGTH_LONG
                ).show()
                currentScreen = ScreenState.HOME_LOGGED_IN
            } else {
                AdminDashboardScreen(
                    onNavigateToHome = {
                        currentScreen = ScreenState.HOME_LOGGED_IN
                    },
                    onNavigateToStand = {
                        currentScreen = ScreenState.SELLER_DASHBOARD
                    }
                )
            }
        }

        // ---------------------------------------------------------------------
        // STAND REGISTER / ADMIN ADD STAND
        // ---------------------------------------------------------------------
        ScreenState.STAND_REGISTER -> {
            StandRegisterScreen(
                onBackClick = {
                    currentScreen = ScreenState.LOGIN
                },
                onRegisterSuccess = { stand, _ ->
                    sellerStandName = stand
                    sellerCounterSlot = "Stand 04"
                    currentScreen = ScreenState.SELLER_DASHBOARD
                }
            )
        }

        ScreenState.ADMIN_ADD_STAND -> {
            if (currentUserRole != UserRole.ADMIN) {
                Toast.makeText(
                    context,
                    "Akses Ditolak: Area Khusus Administrator Koperasi SMKN 8",
                    Toast.LENGTH_LONG
                ).show()
                currentScreen = ScreenState.HOME_LOGGED_IN
            } else {
                AdminAddStandScreen(
                    onBackClick = {
                        currentScreen = ScreenState.ADMIN_DASHBOARD
                    },
                    onAddStandSuccess = { stand, _ ->
                        sellerStandName = stand
                        sellerCounterSlot = "Stand 01"
                        currentScreen = ScreenState.SELLER_DASHBOARD
                    }
                )
            }
        }
    }
}
