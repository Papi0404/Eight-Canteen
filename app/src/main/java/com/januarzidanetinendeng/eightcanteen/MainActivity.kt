package com.januarzidanetinendeng.eightcanteen

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.januarzidanetinendeng.eightcanteen.ui.admin.AdminAddStandScreen
import com.januarzidanetinendeng.eightcanteen.ui.admin.AdminDashboardScreen
import com.januarzidanetinendeng.eightcanteen.ui.checkout.CartViewModel
import com.januarzidanetinendeng.eightcanteen.ui.checkout.CheckoutScreen
import com.januarzidanetinendeng.eightcanteen.ui.dashboard.StudentDashboardScreen
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
    LOGIN,               // Halaman 1: Login
    OTP_VERIFICATION,    // Halaman 2: Verifikasi OTP
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
    ADMIN_DASHBOARD      // Dashboard Admin
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

    // State data pengguna terautentikasi
    var userPhoneNumber by remember { mutableStateOf("812-3456-7890") }
    var accountName by remember { mutableStateOf("Dimas Pratama") }
    var accountRoleOrClass by remember { mutableStateOf("XII RPL 2 • SMKN 8") }
    var sellerStandName by remember { mutableStateOf("Kebab Bang Ali") }
    var sellerCounterSlot by remember { mutableStateOf("Stand 04") }

    // Shared CartViewModel (Single Source of Truth terikat di tingkat NavHost)
    val cartViewModel: CartViewModel = remember { CartViewModel() }

    when (currentScreen) {
        // ---------------------------------------------------------------------
        // HALAMAN 1: LOGIN (Nomor HP & WhatsApp OTP)
        // ---------------------------------------------------------------------
        ScreenState.LOGIN -> {
            LoginScreen(
                initialPhoneNumber = userPhoneNumber,
                onNavigateToAdmin = {
                    currentScreen = ScreenState.ADMIN_DASHBOARD
                },
                onNavigateToRegister = {
                    currentScreen = ScreenState.STUDENT_REGISTER
                },
                onRequestOtpSuccess = { phone ->
                    userPhoneNumber = if (phone.isNotBlank()) phone else "812-3456-7890"
                    currentScreen = ScreenState.OTP_VERIFICATION // Lanjut ke Halaman 2
                }
            )
        }

        // ---------------------------------------------------------------------
        // HALAMAN 2: VERIFIKASI OTP
        // ---------------------------------------------------------------------
        ScreenState.OTP_VERIFICATION -> {
            OtpVerificationScreen(
                phoneNumber = userPhoneNumber,
                onBackClick = {
                    currentScreen = ScreenState.LOGIN // Kembali ke Halaman 1
                },
                onEditPhoneClick = {
                    currentScreen = ScreenState.LOGIN
                },
                onVerificationSuccess = { _ ->
                    currentScreen = ScreenState.STUDENT_REGISTER // Lanjut ke Halaman 3
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
                    currentScreen = ScreenState.OTP_VERIFICATION // Kembali ke Halaman 2
                },
                onRegisterSuccess = { name, className ->
                    accountName = name
                    accountRoleOrClass = className
                    // Transitions to Beranda and pops authentication stack from BackHistory
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
                studentName = accountName,
                studentClass = accountRoleOrClass,
                loyaltyPoints = 25,
                onPointsClick = {
                    currentScreen = ScreenState.POINTS_REWARD
                },
                onNavigateToAdmin = {
                    currentScreen = ScreenState.ADMIN_DASHBOARD
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
                studentName = accountName,
                studentClass = accountRoleOrClass.take(12),
                currentPoints = 25,
                onBackClick = {
                    currentScreen = ScreenState.HOME_LOGGED_IN
                },
                onNavigateToHome = {
                    currentScreen = ScreenState.HOME_LOGGED_IN
                },
                onNavigateToAdmin = {
                    currentScreen = ScreenState.ADMIN_DASHBOARD
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
                    currentScreen = ScreenState.ADMIN_DASHBOARD
                },
                onNavigateToHome = {
                    currentScreen = ScreenState.HOME_LOGGED_IN
                }
            )
        }

        // ---------------------------------------------------------------------
        // ADMIN DASHBOARD
        // ---------------------------------------------------------------------
        ScreenState.ADMIN_DASHBOARD -> {
            AdminDashboardScreen(
                onNavigateToHome = {
                    currentScreen = ScreenState.HOME_LOGGED_IN
                },
                onNavigateToStand = {
                    currentScreen = ScreenState.SELLER_DASHBOARD
                }
            )
        }

        // ---------------------------------------------------------------------
        // STAND REGISTER / ADMIN ADD STAND
        // ---------------------------------------------------------------------
        ScreenState.STAND_REGISTER -> {
            StandRegisterScreen(
                onBackClick = {
                    currentScreen = ScreenState.LOGIN
                },
                onRegisterSuccess = { stand, owner ->
                    sellerStandName = stand
                    sellerCounterSlot = "Stand 04"
                    currentScreen = ScreenState.SELLER_DASHBOARD
                }
            )
        }

        ScreenState.ADMIN_ADD_STAND -> {
            AdminAddStandScreen(
                onBackClick = {
                    currentScreen = ScreenState.ADMIN_DASHBOARD
                },
                onAddStandSuccess = { stand, owner ->
                    sellerStandName = stand
                    sellerCounterSlot = "Stand 01"
                    currentScreen = ScreenState.SELLER_DASHBOARD
                }
            )
        }
    }
}
