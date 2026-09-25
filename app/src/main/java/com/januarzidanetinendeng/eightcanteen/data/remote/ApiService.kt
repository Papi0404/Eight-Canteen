package com.januarzidanetinendeng.eightcanteen.data.remote

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Header

interface ApiService {
    @POST("api/v1/auth/request-otp")
    suspend fun requestOtp(@Body request: OtpRequest): BaseResponse<Any>

    @POST("api/v1/auth/verify-otp")
    suspend fun verifyOtp(@Body request: VerifyOtpRequest): BaseResponse<LoginResponseData>
    
    @GET("api/v1/menus")
    suspend fun getMenus(@Header("Authorization") token: String): BaseResponse<List<MenuResponse>>
}

data class OtpRequest(val phoneNumber: String)
data class VerifyOtpRequest(val phoneNumber: String, val otp: String)

data class BaseResponse<T>(
    val status: String,
    val message: String?,
    val data: T?
)

data class LoginResponseData(
    val token: String,
    val user: UserData
)

data class UserData(
    val id: String,
    val fullName: String,
    val role: String,
    val isNewUser: Boolean
)

data class MenuResponse(
    val id: String,
    val stand_id: String,
    val name: String,
    val price: Int,
    val stock: Int,
    val image_url: String?,
    val is_available: Boolean,
    val stands: StandData?
)

data class StandData(
    val name: String,
    val stand_number: String?
)