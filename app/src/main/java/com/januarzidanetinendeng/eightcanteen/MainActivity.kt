package com.januarzidanetinendeng.eightcanteen

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.januarzidanetinendeng.eightcanteen.data.local.SessionManager
import com.januarzidanetinendeng.eightcanteen.data.remote.ApiConfig
import com.januarzidanetinendeng.eightcanteen.data.repository.ApiException
import com.januarzidanetinendeng.eightcanteen.data.repository.CanteenRepository
import com.januarzidanetinendeng.eightcanteen.ui.admin.AdminAddStandScreen
import com.januarzidanetinendeng.eightcanteen.ui.admin.AdminDashboardScreen
import com.januarzidanetinendeng.eightcanteen.ui.checkout.CartViewModel
import com.januarzidanetinendeng.eightcanteen.ui.checkout.CheckoutPaymentScreen
import com.januarzidanetinendeng.eightcanteen.ui.components.NotificationScreen
import com.januarzidanetinendeng.eightcanteen.ui.components.QrScannerScreen
import com.januarzidanetinendeng.eightcanteen.ui.dashboard.StudentDashboardScreen
import com.januarzidanetinendeng.eightcanteen.ui.dashboard.StudentOrderHistoryScreen
import com.januarzidanetinendeng.eightcanteen.ui.login.LoginScreen
import com.januarzidanetinendeng.eightcanteen.ui.otp.OtpVerificationScreen
import com.januarzidanetinendeng.eightcanteen.ui.payment.StrukLunasScreen
import com.januarzidanetinendeng.eightcanteen.ui.points.PointsRewardScreen
import com.januarzidanetinendeng.eightcanteen.ui.profile.ProfileScreen
import com.januarzidanetinendeng.eightcanteen.ui.register.StudentRegisterScreen
import com.januarzidanetinendeng.eightcanteen.ui.seller.SellerDashboardScreen
import com.januarzidanetinendeng.eightcanteen.ui.splash.SplashScreen
import com.januarzidanetinendeng.eightcanteen.ui.stand.AllStandsScreen
import com.januarzidanetinendeng.eightcanteen.ui.stand.StandMenuScreen
import com.januarzidanetinendeng.eightcanteen.ui.stand.StandRegisterScreen
import com.januarzidanetinendeng.eightcanteen.ui.theme.EightCanteenTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class ScreenState {
    SPLASH,
    LOGIN,
    OTP_VERIFICATION,
    STUDENT_REGISTER,
    STAND_REGISTER,
    ADMIN_ADD_STAND,
    HOME_LOGGED_IN,
    ALL_STANDS,
    STAND_MENU,
    ORDER_HISTORY,
    POINTS_REWARD,
    PROFILE,
    CHECKOUT,
    STRUK_LUNAS,
    SELLER_DASHBOARD,
    ADMIN_DASHBOARD,
    SCAN_QR,
    NOTIFICATIONS
}

enum class UserRole {
    STUDENT, SELLER, ADMIN
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
fun MainAppNavigation(
    cartViewModel: CartViewModel = viewModel()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    // Initial state dimulai dari SPLASH untuk memeriksa sesi tersimpan
    var currentScreen by remember { mutableStateOf(ScreenState.SPLASH) }
    var splashStatusMessage by remember { mutableStateOf("Memeriksa sesi login...") }
    var currentUserRole by remember { mutableStateOf(UserRole.STUDENT) }
    
    var userPhoneNumber by remember { mutableStateOf("") }
    var lastCreatedOrderId by remember { mutableStateOf<String?>(null) }
    
    // Student Data
    var studentName by remember { mutableStateOf(SessionManager.getInstance(context).getUserName().takeIf { it.isNotBlank() && !it.equals("Pengguna", true) } ?: "Pengguna") }
    var studentClass by remember { mutableStateOf(SessionManager.getInstance(context).getStudentClass() ?: "") }
    
    // Seller Data
    var sellerStandName by remember { mutableStateOf("Kebab Bang Ali") }
    var sellerCounterSlot by remember { mutableStateOf("Stand 04") }

    // Stand Selection Data
    var selectedStandId by remember { mutableStateOf<String?>(null) }
    var selectedStandName by remember { mutableStateOf<String?>(null) }

    fun performLogout() {
        val session = SessionManager.getInstance(context)
        session.clearSession()
        ApiConfig.setAuthToken(null)
        cartViewModel.clearCart()
        userPhoneNumber = ""
        studentName = ""
        studentClass = ""
        sellerStandName = "Kebab Bang Ali"
        sellerCounterSlot = "Stand 04"
        currentUserRole = UserRole.STUDENT
        currentScreen = ScreenState.LOGIN
        Toast.makeText(context, "Berhasil keluar dari akun", Toast.LENGTH_SHORT).show()
    }

    fun navigateToRoleDashboard(session: SessionManager, roleString: String) {
        val roleLower = roleString.lowercase()
        when {
            roleLower.contains("admin") -> {
                currentUserRole = UserRole.ADMIN
                currentScreen = ScreenState.ADMIN_DASHBOARD
            }
            roleLower.contains("penjual") || roleLower.contains("seller") -> {
                currentUserRole = UserRole.SELLER
                currentScreen = ScreenState.SELLER_DASHBOARD
            }
            else -> {
                currentUserRole = UserRole.STUDENT
                if (session.isProfileComplete()) {
                    currentScreen = ScreenState.HOME_LOGGED_IN
                } else {
                    currentScreen = ScreenState.STUDENT_REGISTER
                }
            }
        }
    }

    // Pengecekan JWT Token / Session otomatis setiap aplikasi dibuka
    LaunchedEffect(Unit) {
        val session = SessionManager.getInstance(context)
        val token = session.getAuthToken()

        if (token.isNullOrBlank()) {
            // Belum ada token / sesi kosong -> tampilkan layar login
            delay(700)
            currentScreen = ScreenState.LOGIN
        } else {
            // Token ditemukan! Sinkronkan ke ApiConfig
            ApiConfig.setAuthToken(token)
            splashStatusMessage = "Memvalidasi sesi akun..."

            // Muat data lokal terlebih dahulu agar transisi UI mulus
            val cachedPhone = session.getUserPhone()
            val cachedName = session.getUserName()
            val cachedRole = session.getUserRole()
            val cachedClass = session.getStudentClass() ?: ""
            val cachedStand = session.getStandName() ?: "Kebab Bang Ali"
            val cachedSlot = session.getCounterSlot() ?: "Stand 04"

            userPhoneNumber = cachedPhone
            studentName = cachedName
            studentClass = cachedClass
            sellerStandName = cachedStand
            sellerCounterSlot = cachedSlot

            // Validasi token langsung ke endpoint /users/me
            val repository = CanteenRepository()
            val profileResult = repository.getMyProfile()

            profileResult.onSuccess { res ->
                val profile = res.data
                if (profile != null) {
                    val rawName = (profile.fullName ?: profile.name ?: cachedName).trim()
                    val updatedName = if (rawName.equals("EMPTY", ignoreCase = true)) "Pengguna" else rawName
                    val updatedRole = profile.role ?: cachedRole
                    val updatedPhone = profile.phoneNumber ?: cachedPhone
                    val updatedPoints = profile.points ?: session.getPoints()
                    val updatedClass = (profile.resolvedClass ?: profile.studentClass ?: profile.className ?: cachedClass).trim()
                    val updatedStandId = profile.stand?.id ?: session.getStandId()
                    val updatedStandName = profile.stand?.name ?: cachedStand
                    val updatedSlot = profile.stand?.counterSlot ?: cachedSlot

                    // Update session storage dengan data terbaru dari server
                    session.saveUser(
                        id = profile.id,
                        name = updatedName,
                        role = updatedRole,
                        phone = updatedPhone,
                        standId = updatedStandId,
                        points = updatedPoints,
                        studentClass = updatedClass,
                        nis = profile.nis ?: session.getNis(),
                        standName = updatedStandName,
                        counterSlot = updatedSlot
                    )

                    studentName = updatedName
                    studentClass = updatedClass
                    sellerStandName = updatedStandName
                    sellerCounterSlot = updatedSlot
                    userPhoneNumber = updatedPhone

                    navigateToRoleDashboard(session, updatedRole)
                } else {
                    navigateToRoleDashboard(session, cachedRole)
                }
            }.onFailure { e ->
                if (e is ApiException && (e.statusCode == 401 || e.statusCode == 403)) {
                    // Token kedaluwarsa atau tidak valid di backend
                    session.clearSession()
                    ApiConfig.setAuthToken(null)
                    Toast.makeText(context, "Sesi login Anda telah berakhir. Silakan masuk kembali.", Toast.LENGTH_LONG).show()
                    currentScreen = ScreenState.LOGIN
                } else {
                    // Offline atau backend error sementara -> tetap berikan akses dengan sesi tersimpan
                    Toast.makeText(context, "Sesi offline aktif: Menggunakan data tersimpan", Toast.LENGTH_SHORT).show()
                    navigateToRoleDashboard(session, cachedRole)
                }
            }
        }
    }

    // Android System Back Navigation Handler
    val isRootScreen = currentScreen == ScreenState.LOGIN ||
            currentScreen == ScreenState.SPLASH ||
            currentScreen == ScreenState.HOME_LOGGED_IN ||
            currentScreen == ScreenState.SELLER_DASHBOARD ||
            currentScreen == ScreenState.ADMIN_DASHBOARD

    BackHandler(enabled = !isRootScreen) {
        when (currentScreen) {
            ScreenState.OTP_VERIFICATION -> currentScreen = ScreenState.LOGIN
            ScreenState.STUDENT_REGISTER, ScreenState.STAND_REGISTER -> currentScreen = ScreenState.LOGIN
            ScreenState.CHECKOUT -> currentScreen = ScreenState.HOME_LOGGED_IN
            ScreenState.STRUK_LUNAS -> {
                cartViewModel.clearCart()
                currentScreen = ScreenState.HOME_LOGGED_IN
            }
            ScreenState.POINTS_REWARD, ScreenState.PROFILE, ScreenState.NOTIFICATIONS, ScreenState.ALL_STANDS -> currentScreen = ScreenState.HOME_LOGGED_IN
            ScreenState.STAND_MENU -> currentScreen = ScreenState.ALL_STANDS
            ScreenState.SCAN_QR -> currentScreen = ScreenState.SELLER_DASHBOARD
            ScreenState.ADMIN_ADD_STAND -> currentScreen = ScreenState.ADMIN_DASHBOARD
            else -> {
                // Exit app / system back
            }
        }
    }

    when (currentScreen) {
        ScreenState.SPLASH -> {
            SplashScreen(statusMessage = splashStatusMessage)
        }

        ScreenState.LOGIN -> {
            LoginScreen(
                initialPhoneNumber = userPhoneNumber,
                onNavigateToAdmin = {
                    // Force Demo Admin
                    userPhoneNumber = "811-9999-0000"
                },
                onNavigateToRegister = {
                    currentScreen = ScreenState.STUDENT_REGISTER
                },
                onNavigateToStandRegister = {
                    currentScreen = ScreenState.STAND_REGISTER
                },
                onRequestOtpSuccess = { phone ->
                    userPhoneNumber = phone
                    currentScreen = ScreenState.OTP_VERIFICATION
                }
            )
        }

        ScreenState.OTP_VERIFICATION -> {
            OtpVerificationScreen(
                phoneNumber = userPhoneNumber,
                onBackClick = {
                    currentScreen = ScreenState.LOGIN
                },
                onEditPhoneClick = {
                    currentScreen = ScreenState.LOGIN
                },
                onVerificationSuccess = { otp ->
                    val cleanPhone = userPhoneNumber.replace("-", "")
                    val session = SessionManager.getInstance(context)
                    val savedRole = session.getUserRole().lowercase()
                    val savedName = session.getUserName()
                    val savedClass = session.getStudentClass() ?: studentClass
                    val savedStand = session.getStandName() ?: sellerStandName
                    val savedSlot = session.getCounterSlot() ?: sellerCounterSlot

                    studentName = savedName
                    studentClass = savedClass
                    sellerStandName = savedStand
                    sellerCounterSlot = savedSlot

                    when {
                        savedRole.contains("admin") || cleanPhone.startsWith("811") -> {
                            currentUserRole = UserRole.ADMIN
                            currentScreen = ScreenState.ADMIN_DASHBOARD
                            Toast.makeText(context, "Login sebagai Admin Koperasi", Toast.LENGTH_SHORT).show()
                        }
                        savedRole.contains("penjual") || savedRole.contains("seller") || cleanPhone.startsWith("822") -> {
                            currentUserRole = UserRole.SELLER
                            currentScreen = ScreenState.SELLER_DASHBOARD
                            Toast.makeText(context, "Login sebagai Penjual/Tenant", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            currentUserRole = UserRole.STUDENT
                            if (session.isProfileComplete()) {
                                currentScreen = ScreenState.HOME_LOGGED_IN
                            } else {
                                currentScreen = ScreenState.STUDENT_REGISTER
                            }
                            Toast.makeText(context, "Verifikasi Siswa Berhasil", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            )
        }

        ScreenState.STUDENT_REGISTER -> {
            StudentRegisterScreen(
                verifiedPhoneNumber = userPhoneNumber,
                onBackClick = {
                    currentScreen = ScreenState.OTP_VERIFICATION
                },
                onRegisterSuccess = { name, className ->
                    studentName = name
                    studentClass = className
                    val session = SessionManager.getInstance(context)
                    session.saveUser(
                        id = session.getUserId() ?: "",
                        name = name,
                        role = "Siswa",
                        phone = userPhoneNumber,
                        standId = null,
                        points = session.getPoints(),
                        studentClass = className
                    )
                    currentScreen = ScreenState.HOME_LOGGED_IN
                }
            )
        }

        ScreenState.STAND_REGISTER -> {
            StandRegisterScreen(
                onBackClick = {
                    currentScreen = ScreenState.LOGIN
                },
                onRegisterSuccess = { stand, owner ->
                    sellerStandName = stand
                    sellerCounterSlot = "Stand 04"
                    val session = SessionManager.getInstance(context)
                    session.saveUser(
                        id = session.getUserId() ?: "",
                        name = owner,
                        role = "Penjual",
                        phone = userPhoneNumber,
                        standId = session.getStandId(),
                        points = 0,
                        standName = stand,
                        counterSlot = "Stand 04"
                    )
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
                    Toast.makeText(context, "Akses Seller untuk $stand berhasil dibuat!", Toast.LENGTH_LONG).show()
                    currentScreen = ScreenState.ADMIN_DASHBOARD
                }
            )
        }

        ScreenState.HOME_LOGGED_IN -> {
            StudentDashboardScreen(
                cartViewModel = cartViewModel,
                studentName = studentName,
                studentClass = studentClass,
                loyaltyPoints = SessionManager.getInstance(context).getPoints(),
                onPointsClick = {
                    currentScreen = ScreenState.POINTS_REWARD
                },
                onLogoutClick = {
                    performLogout()
                },
                onCheckoutClick = {
                    currentScreen = ScreenState.CHECKOUT
                },
                onOrderClick = { orderId ->
                    lastCreatedOrderId = orderId
                    currentScreen = ScreenState.STRUK_LUNAS
                },
                onNavigateToNotifications = {
                    currentScreen = ScreenState.NOTIFICATIONS
                },
                onSeeAllStandsClick = {
                    currentScreen = ScreenState.ALL_STANDS
                },
                onStandClick = { standId, standName ->
                    selectedStandId = standId
                    selectedStandName = standName
                    currentScreen = ScreenState.STAND_MENU
                }
            )
        }

        ScreenState.ALL_STANDS -> {
            AllStandsScreen(
                onBackClick = {
                    currentScreen = ScreenState.HOME_LOGGED_IN
                },
                onStandClick = { standId, standName ->
                    selectedStandId = standId
                    selectedStandName = standName
                    currentScreen = ScreenState.STAND_MENU
                }
            )
        }

        ScreenState.STAND_MENU -> {
            StandMenuScreen(
                cartViewModel = cartViewModel,
                standId = selectedStandId ?: "",
                standName = selectedStandName ?: "Stand Kantin",
                onBackClick = {
                    currentScreen = ScreenState.ALL_STANDS
                },
                onCheckoutClick = {
                    currentScreen = ScreenState.CHECKOUT
                }
            )
        }

        ScreenState.ORDER_HISTORY -> {
            StudentOrderHistoryScreen(
                onBackClick = {
                    currentScreen = ScreenState.HOME_LOGGED_IN
                },
                onOrderClick = { orderId ->
                    lastCreatedOrderId = orderId
                    currentScreen = ScreenState.STRUK_LUNAS
                },
                onOrderNewFoodClick = {
                    currentScreen = ScreenState.HOME_LOGGED_IN
                }
            )
        }

        ScreenState.NOTIFICATIONS -> {
            NotificationScreen(
                onBackClick = {
                    currentScreen = ScreenState.HOME_LOGGED_IN
                }
            )
        }

        ScreenState.POINTS_REWARD -> {
            PointsRewardScreen(
                studentName = studentName,
                studentClass = studentClass.take(12),
                currentPoints = SessionManager.getInstance(context).getPoints(),
                onBackClick = {
                    currentScreen = ScreenState.HOME_LOGGED_IN
                },
                onNavigateToHome = {
                    currentScreen = ScreenState.HOME_LOGGED_IN
                },
                onNavigateToOrders = {
                    currentScreen = ScreenState.ORDER_HISTORY
                },
                onNavigateToProfile = {
                    currentScreen = ScreenState.PROFILE
                }
            )
        }

        ScreenState.PROFILE -> {
            ProfileScreen(
                currentName = studentName,
                currentClass = studentClass,
                currentPhone = userPhoneNumber,
                onBackClick = {
                    currentScreen = ScreenState.HOME_LOGGED_IN
                },
                onLogoutClick = {
                    performLogout()
                },
                onSaveSuccess = { newName, newClass ->
                    studentName = newName
                    studentClass = newClass
                    val session = SessionManager.getInstance(context)
                    session.updateProfile(newName, newClass)
                    currentScreen = ScreenState.HOME_LOGGED_IN
                }
            )
        }

        ScreenState.CHECKOUT -> {
            CheckoutPaymentScreen(
                cartViewModel = cartViewModel,
                onBackClick = {
                    currentScreen = ScreenState.HOME_LOGGED_IN
                },
                onPaymentSuccess = { method, orderId ->
                    lastCreatedOrderId = orderId
                    currentScreen = ScreenState.STRUK_LUNAS
                }
            )
        }

        ScreenState.STRUK_LUNAS -> {
            StrukLunasScreen(
                orderId = lastCreatedOrderId,
                viewModel = cartViewModel,
                onBackClick = {
                    cartViewModel.clearCart()
                    currentScreen = ScreenState.HOME_LOGGED_IN
                },
                onBackToHome = {
                    cartViewModel.clearCart()
                    currentScreen = ScreenState.HOME_LOGGED_IN
                }
            )
        }

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
                    currentScreen = ScreenState.SCAN_QR
                },
                onLogoutClick = {
                    performLogout()
                }
            )
        }

        ScreenState.SCAN_QR -> {
            QrScannerScreen(
                onBackClick = {
                    currentScreen = ScreenState.SELLER_DASHBOARD
                },
                onScanSuccess = { orderId ->
                    coroutineScope.launch {
                        val repo = CanteenRepository()
                        repo.confirmPickup(orderId).onSuccess { res ->
                            Toast.makeText(context, "Pesanan #${res.data?.orderNumber ?: orderId} berhasil diambil!", Toast.LENGTH_LONG).show()
                        }.onFailure {
                            Toast.makeText(context, "Konfirmasi Pickup $orderId Selesai (Simulasi)", Toast.LENGTH_LONG).show()
                        }
                    }
                    currentScreen = ScreenState.SELLER_DASHBOARD
                }
            )
        }

        ScreenState.ADMIN_DASHBOARD -> {
            AdminDashboardScreen(
                onLogoutClick = {
                    performLogout()
                },
                onNavigateToAddStand = {
                    currentScreen = ScreenState.ADMIN_ADD_STAND
                }
            )
        }
    }
}
