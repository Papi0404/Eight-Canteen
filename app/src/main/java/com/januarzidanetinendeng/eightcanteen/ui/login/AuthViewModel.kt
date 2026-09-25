package com.januarzidanetinendeng.eightcanteen.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.januarzidanetinendeng.eightcanteen.data.remote.ApiConfig
import com.januarzidanetinendeng.eightcanteen.data.remote.LoginResponseData
import com.januarzidanetinendeng.eightcanteen.data.remote.OtpRequest
import com.januarzidanetinendeng.eightcanteen.data.remote.VerifyOtpRequest
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

class AuthViewModel : ViewModel() {
    private val api = ApiConfig.getApiService()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    fun requestOtp(phoneNumber: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                // Hapus dash jika ada
                val cleanPhone = phoneNumber.replace("-", "").trim()
                val response = api.requestOtp(OtpRequest(cleanPhone))
                if (response.status == "success") {
                    _authState.value = AuthState.OtpSent
                } else {
                    _authState.value = AuthState.Error(response.message ?: "Gagal meminta OTP")
                }
            } catch (e: Exception) {
                // Di mode dev kalau API belum on, fallback local UI test bypass
                e.printStackTrace()
                _authState.value = AuthState.Error("Koneksi API Gagal: ${e.message}. Pastikan Backend (server.js) berjalan di port 3000.")
            }
        }
    }

    fun verifyOtp(phoneNumber: String, otp: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val cleanPhone = phoneNumber.replace("-", "").trim()
                val response = api.verifyOtp(VerifyOtpRequest(cleanPhone, otp))
                if (response.status == "success" && response.data != null) {
                    _authState.value = AuthState.Success(response.data)
                } else {
                    _authState.value = AuthState.Error(response.message ?: "OTP Salah")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _authState.value = AuthState.Error("Gagal verifikasi OTP: ${e.message}")
            }
        }
    }
    
    fun resetState() {
        _authState.value = AuthState.Idle
    }
}
