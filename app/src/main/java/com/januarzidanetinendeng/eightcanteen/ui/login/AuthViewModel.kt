package com.januarzidanetinendeng.eightcanteen.ui.login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.januarzidanetinendeng.eightcanteen.data.local.SessionManager
import com.januarzidanetinendeng.eightcanteen.data.remote.ApiConfig
import com.januarzidanetinendeng.eightcanteen.data.remote.LoginResponseData
import com.januarzidanetinendeng.eightcanteen.data.remote.UserProfile
import com.januarzidanetinendeng.eightcanteen.data.repository.CanteenRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object OtpSent : AuthState()
    data class Success(val data: LoginResponseData) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(
    private val repository: CanteenRepository = CanteenRepository()
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile

    fun requestOtp(phoneNumber: String, onResult: ((Boolean, String) -> Unit)? = null) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val cleanPhone = phoneNumber.replace("-", "").trim()
            val result = repository.requestOtp(cleanPhone)
            
            result.onSuccess { response ->
                _authState.value = AuthState.OtpSent
                val msg = response.message ?: "OTP berhasil dikirim via WhatsApp!"
                onResult?.invoke(true, msg)
            }.onFailure { e ->
                val errorMsg = e.message ?: "Koneksi ke backend gagal"
                _authState.value = AuthState.Error(errorMsg)
                onResult?.invoke(false, errorMsg)
            }
        }
    }

    fun verifyOtp(
        context: Context,
        phoneNumber: String,
        otp: String,
        onResult: ((Boolean, LoginResponseData?, String) -> Unit)? = null
    ) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val cleanPhone = phoneNumber.replace("-", "").trim()
            val result = repository.verifyOtp(cleanPhone, otp)

            result.onSuccess { response ->
                val data = response.data
                if (data != null) {
                    _authState.value = AuthState.Success(data)
                    // Simpan token ke SessionManager & ApiConfig
                    SessionManager.getInstance(context).apply {
                        saveAuthToken(data.token)
                        saveUser(
                            id = data.user.id,
                            name = data.user.fullName ?: data.user.name ?: "User",
                            role = data.user.role ?: "Siswa",
                            phone = cleanPhone,
                            standId = data.user.stand?.id,
                            points = data.user.points ?: 0,
                            studentClass = data.user.studentClass,
                            nis = data.user.nis,
                            standName = data.user.stand?.name,
                            counterSlot = data.user.stand?.counterSlot
                        )
                    }
                    ApiConfig.setAuthToken(data.token)
                    _userProfile.value = data.user
                    onResult?.invoke(true, data, "Verifikasi Berhasil!")
                } else {
                    val msg = response.message ?: "Verifikasi gagal"
                    _authState.value = AuthState.Error(msg)
                    onResult?.invoke(false, null, msg)
                }
            }.onFailure { e ->
                val errorMsg = e.message ?: "Gagal memverifikasi OTP"
                _authState.value = AuthState.Error(errorMsg)
                onResult?.invoke(false, null, errorMsg)
            }
        }
    }

    fun register(
        name: String,
        role: String,
        nis: String? = null,
        standName: String? = null,
        onResult: ((Boolean, UserProfile?, String) -> Unit)? = null
    ) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = repository.register(name, role, nis, standName)
            result.onSuccess { response ->
                _userProfile.value = response.data
                _authState.value = AuthState.Idle
                onResult?.invoke(true, response.data, response.message ?: "Pendaftaran Berhasil!")
            }.onFailure { e ->
                val msg = e.message ?: "Gagal mendaftar"
                _authState.value = AuthState.Error(msg)
                onResult?.invoke(false, null, msg)
            }
        }
    }

    fun fetchMyProfile(context: Context? = null, onResult: ((UserProfile?) -> Unit)? = null) {
        viewModelScope.launch {
            val result = repository.getMyProfile()
            result.onSuccess { response ->
                response.data?.let { profile ->
                    _userProfile.value = profile
                    context?.let { ctx ->
                        SessionManager.getInstance(ctx).updatePoints(profile.points ?: 0)
                    }
                    onResult?.invoke(profile)
                }
            }.onFailure {
                onResult?.invoke(null)
            }
        }
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }
}
