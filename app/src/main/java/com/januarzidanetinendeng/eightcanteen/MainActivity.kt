package com.januarzidanetinendeng.eightcanteen

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.januarzidanetinendeng.eightcanteen.data.local.SessionManager
import com.januarzidanetinendeng.eightcanteen.data.repository.CanteenRepository
import com.januarzidanetinendeng.eightcanteen.ui.admin.AdminAddStandScreen
import com.januarzidanetinendeng.eightcanteen.ui.admin.AdminDashboardScreen
import com.januarzidanetinendeng.eightcanteen.ui.checkout.CartViewModel
import com.januarzidanetinendeng.eightcanteen.ui.checkout.CheckoutPaymentScreen
import com.januarzidanetinendeng.eightcanteen.ui.components.NotificationScreen
import com.januarzidanetinendeng.eightcanteen.ui.components.QrScannerScreen
import com.januarzidanetinendeng.eightcanteen.ui.dashboard.StudentDashboardScreen
import com.januarzidanetinendeng.eightcanteen.ui.login.LoginScreen
import com.januarzidanetinendeng.eightcanteen.ui.otp.OtpVerificationScreen
import com.januarzidanetinendeng.eightcanteen.ui.payment.StrukLunasScreen
import com.januarzidanetinendeng.eightcanteen.ui.points.PointsRewardScreen
import com.januarzidanetinendeng.eightcanteen.ui.profile.ProfileScreen
import com.januarzidanetinendeng.eightcanteen.ui.register.StudentRegisterScreen
import com.januarzidanetinendeng.eightcanteen.ui.seller.SellerDashboardScreen
import com.januarzidanetinendeng.eightcanteen.ui.stand.StandRegisterScreen
import com.januarzidanetinendeng.eightcanteen.ui.theme.EightCanteenTheme
import kotlinx.coroutines.launch

enum class ScreenState {
    LOGIN,
    OTP_VERIFICATION,
    STUDENT_REGISTER,
    STAND_REGISTER,
    ADMIN_ADD_STAND,
    HOME_LOGGED_IN,
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
    var currentScreen by remember { mutableStateOf(ScreenState.LOGIN) }
    var currentUserRole by remember { mutableStateOf(UserRole.STUDENT) }
    
    var userPhoneNumber by remember { mutableStateOf("") }
    var lastCreatedOrderId by remember { mutableStateOf<String?>(null) }
    
    // Student Data
    var studentName by remember { mutableStateOf("Dimas Pratama") }
    var studentClass by remember { mutableStateOf("XII RPL 2 • SMKN 8") }
    
    // Seller Data
    var sellerStandName by remember { mutableStateOf("Kebab Bang Ali") }
    var sellerCounterSlot by remember { mutableStateOf("Stand 04") }

    // Android System Back Navigation Handler
    BackHandler(enabled = currentScreen != ScreenState.LOGIN) {
        when (currentScreen) {
            ScreenState.OTP_VERIFICATION -> currentScreen = ScreenState.LOGIN
            ScreenState.STUDENT_REGISTER, ScreenState.STAND_REGISTER -> currentScreen = ScreenState.LOGIN
            ScreenState.CHECKOUT -> currentScreen = ScreenState.HOME_LOGGED_IN
            ScreenState.STRUK_LUNAS -> {
                cartViewModel.clearCart()
                currentScreen = ScreenState.HOME_LOGGED_IN
            }
            ScreenState.POINTS_REWARD, ScreenState.PROFILE, ScreenState.NOTIFICATIONS -> currentScreen = ScreenState.HOME_LOGGED_IN
            ScreenState.SCAN_QR -> currentScreen = ScreenState.SELLER_DASHBOARD
            ScreenState.ADMIN_ADD_STAND -> currentScreen = ScreenState.ADMIN_DASHBOARD
            ScreenState.HOME_LOGGED_IN, ScreenState.SELLER_DASHBOARD, ScreenState.ADMIN_DASHBOARD -> {
                currentScreen = ScreenState.LOGIN
            }
            ScreenState.LOGIN -> {
                // Exit app handled by system
            }
        }
    }

    when (currentScreen) {
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

                    when {
                        savedRole.contains("admin") || cleanPhone.startsWith("811") -> {
                            currentUserRole = UserRole.ADMIN
                            currentScreen = ScreenState.ADMIN_DASHBOARD
                            Toast.makeText(context, "Login sebagai Admin Koperasi", Toast.LENGTH_SHORT).show()
                        }
                        savedRole.contains("penjual") || savedRole.contains("seller") || cleanPhone.startsWith("822") -> {
                            currentUserRole = UserRole.SELLER
                            if (savedName.isNotBlank() && savedName != "Pengguna") {
                                sellerStandName = savedName
                            }
                            currentScreen = ScreenState.SELLER_DASHBOARD
                            Toast.makeText(context, "Login sebagai Penjual/Tenant", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            currentUserRole = UserRole.STUDENT
                            if (savedName.isNotBlank() && savedName != "Pengguna" && savedName != "User") {
                                studentName = savedName
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
                loyaltyPoints = 25,
                onPointsClick = {
                    currentScreen = ScreenState.POINTS_REWARD
                },
                onLogoutClick = {
                    userPhoneNumber = ""
                    currentScreen = ScreenState.LOGIN
                },
                onCheckoutClick = {
                    currentScreen = ScreenState.CHECKOUT
                },
                onNavigateToNotifications = {
                    currentScreen = ScreenState.NOTIFICATIONS
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
                currentPoints = 25,
                onBackClick = {
                    currentScreen = ScreenState.HOME_LOGGED_IN
                },
                onNavigateToHome = {
                    currentScreen = ScreenState.HOME_LOGGED_IN
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
                    userPhoneNumber = ""
                    currentScreen = ScreenState.LOGIN
                },
                onSaveSuccess = { newName, newClass ->
                    studentName = newName
                    studentClass = newClass
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
                    userPhoneNumber = ""
                    currentScreen = ScreenState.LOGIN
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
                    userPhoneNumber = ""
                    currentScreen = ScreenState.LOGIN
                },
                onNavigateToAddStand = {
                    currentScreen = ScreenState.ADMIN_ADD_STAND
                }
            )
        }
    }
}
