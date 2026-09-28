package com.januarzidanetinendeng.eightcanteen.auth

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthViewModel : ViewModel() {

    private val _currentUserRole = MutableStateFlow(UserRole.STUDENT)
    val currentUserRole: StateFlow<UserRole> = _currentUserRole.asStateFlow()

    private val _userName = MutableStateFlow("")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _userClass = MutableStateFlow("")
    val userClass: StateFlow<String> = _userClass.asStateFlow()

    private val _userPhone = MutableStateFlow("")
    val userPhone: StateFlow<String> = _userPhone.asStateFlow()

    // Daftar Nomor WhatsApp Admin Terdaftar (Simulasi Database Koperasi SMKN 8)
    private val registeredAdminPhones = setOf(
        "08123456789",
        "08111111111",
        "081288888888",
        "081299998888",
        "081234567890",
        "0812-3456-789",
        "admin"
    )

    fun checkIsAdminPhone(phone: String): Boolean {
        if (phone.equals("admin", ignoreCase = true)) return true
        val cleanPhone = phone.replace(Regex("[^0-9]"), "")
        if (cleanPhone.isBlank()) return false
        return registeredAdminPhones.any { adminPhone ->
            val cleanAdmin = adminPhone.replace(Regex("[^0-9]"), "")
            cleanAdmin.isNotBlank() && (cleanPhone == cleanAdmin || cleanPhone.contains(cleanAdmin) || cleanAdmin.contains(cleanPhone))
        }
    }

    fun loginAsStudent(name: String, className: String, phone: String) {
        _userName.value = name.ifBlank { "Siswa" }
        _userClass.value = className
        _userPhone.value = phone
        _currentUserRole.value = UserRole.STUDENT
    }

    fun verifyAdminOtpAndLogin(phone: String, otpCode: String): Boolean {
        if (otpCode.length == 4) {
            _currentUserRole.value = UserRole.ADMIN
            _userName.value = "Pengelola Koperasi"
            _userClass.value = "Admin Koperasi"
            _userPhone.value = phone
            return true
        }
        return false
    }

    fun logout() {
        _currentUserRole.value = UserRole.STUDENT
        _userName.value = ""
        _userClass.value = ""
        _userPhone.value = ""
    }

    fun getRole(): UserRole {
        return _currentUserRole.value
    }
}
