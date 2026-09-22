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
import com.januarzidanetinendeng.eightcanteen.ui.dashboard.StudentDashboardScreen
import com.januarzidanetinendeng.eightcanteen.ui.login.LoginScreen
import com.januarzidanetinendeng.eightcanteen.ui.otp.OtpVerificationScreen
import com.januarzidanetinendeng.eightcanteen.ui.points.PointsRewardScreen
import com.januarzidanetinendeng.eightcanteen.ui.register.StudentRegisterScreen
import com.januarzidanetinendeng.eightcanteen.ui.seller.SellerDashboardScreen
import com.januarzidanetinendeng.eightcanteen.ui.stand.StandRegisterScreen
import com.januarzidanetinendeng.eightcanteen.ui.theme.EightCanteenTheme

enum class ScreenState {
    LOGIN,
    OTP_VERIFICATION,
    STUDENT_REGISTER,
    STAND_REGISTER,
    ADMIN_ADD_STAND,
    HOME_LOGGED_IN,
    POINTS_REWARD,
    SELLER_DASHBOARD,
    ADMIN_DASHBOARD
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
    var currentScreen by remember { mutableStateOf(ScreenState.LOGIN) }
    var userPhoneNumber by remember { mutableStateOf("812-3456-7890") }
    var accountName by remember { mutableStateOf("Dimas Pratama") }
    var accountRoleOrClass by remember { mutableStateOf("XII RPL 2 • SMKN 8") }
    var sellerStandName by remember { mutableStateOf("Kebab Bang Ali") }
    var sellerCounterSlot by remember { mutableStateOf("Stand 04") }

    when (currentScreen) {
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
                    currentScreen = ScreenState.STUDENT_REGISTER
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
                    accountName = name
                    accountRoleOrClass = className
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
                    sellerStandName = stand
                    sellerCounterSlot = "Stand 01"
                    currentScreen = ScreenState.SELLER_DASHBOARD
                }
            )
        }

        ScreenState.HOME_LOGGED_IN -> {
            // Full Student Dashboard Screen View
            StudentDashboardScreen(
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
                    Toast.makeText(context, "Membuka Halaman Checkout Pre-Order E-Kantin...", Toast.LENGTH_SHORT).show()
                }
            )
        }

        ScreenState.POINTS_REWARD -> {
            // Points & Rewards Screen
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

        ScreenState.SELLER_DASHBOARD -> {
            // Full Seller Dashboard Screen View
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

        ScreenState.ADMIN_DASHBOARD -> {
            // Full Admin Dashboard Screen View
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
}
