package com.januarzidanetinendeng.eightcanteen.data.repository

import com.google.gson.Gson
import com.januarzidanetinendeng.eightcanteen.data.remote.*
import okhttp3.MultipartBody
import retrofit2.HttpException

class ApiException(val statusCode: Int, override val message: String) : Exception(message)

class CanteenRepository(
    private val api: ApiService = ApiConfig.getApiService()
) {
    private val gson = Gson()

    private fun parseHttpError(e: Throwable): Throwable {
        if (e is HttpException) {
            try {
                val errorBody = e.response()?.errorBody()?.string()
                if (!errorBody.isNullOrBlank()) {
                    val baseResponse = gson.fromJson(errorBody, BaseResponse::class.java)
                    val message = baseResponse.message
                    if (!message.isNullOrBlank()) {
                        return ApiException(e.code(), message)
                    }
                }
            } catch (_: Exception) {}

            val fallbackMsg = when (e.code()) {
                400 -> "Permintaan tidak valid atau data salah"
                401 -> "Sesi login telah berakhir atau token tidak valid"
                403 -> "Akses ditolak"
                404 -> "Data tidak ditemukan"
                else -> "Gagal memproses permintaan (HTTP ${e.code()})"
            }
            return ApiException(e.code(), fallbackMsg)
        }
        return e
    }

    private inline fun <T> safeApiCall(block: () -> T): Result<T> {
        return try {
            Result.success(block())
        } catch (e: Throwable) {
            Result.failure(parseHttpError(e))
        }
    }

    // 1. Auth
    suspend fun requestOtp(phoneNumber: String): Result<BaseResponse<Any>> = safeApiCall {
        api.requestOtp(OtpRequest(phoneNumber.replace("-", "").trim()))
    }

    suspend fun verifyOtp(phoneNumber: String, otp: String): Result<BaseResponse<LoginResponseData>> = safeApiCall {
        api.verifyOtp(VerifyOtpRequest(phoneNumber.replace("-", "").trim(), otp.trim()))
    }

    suspend fun register(
        name: String,
        role: String,
        nis: String? = null,
        studentClass: String? = null,
        standName: String? = null
    ): Result<BaseResponse<UserProfile>> = safeApiCall {
        api.register(
            RegisterRequest(
                name = name,
                role = role,
                nis = nis,
                className = studentClass,
                studentClass = studentClass,
                class_name = studentClass,
                standName = standName
            )
        )
    }

    // 2. User Profile
    suspend fun getMyProfile(): Result<BaseResponse<UserProfile>> = safeApiCall {
        api.getMyProfile()
    }

    suspend fun updateProfile(
        name: String,
        studentClass: String? = null,
        nis: String? = null
    ): Result<BaseResponse<UserProfile>> = safeApiCall {
        api.updateProfile(
            UpdateProfileRequest(
                name = name.trim(),
                fullName = name.trim(),
                className = studentClass?.trim(),
                studentClass = studentClass?.trim(),
                class_name = studentClass?.trim(),
                nis = nis?.trim()
            )
        )
    }

    // 3. Stands & Menus
    suspend fun getStands(): Result<BaseResponse<List<StandResponse>>> = safeApiCall {
        api.getStands()
    }

    suspend fun getMyStand(): Result<BaseResponse<StandResponse>> = safeApiCall {
        api.getMyStand()
    }

    suspend fun updateMyStand(
        name: String? = null,
        counterSlot: String? = null,
        category: String? = null,
        isOpen: Boolean? = null
    ): Result<BaseResponse<StandResponse>> = safeApiCall {
        api.updateMyStand(
            UpdateStandRequest(
                name = name,
                standName = name,
                counterSlot = counterSlot,
                standNumber = counterSlot,
                category = category,
                isOpen = isOpen
            )
        )
    }

    suspend fun updateStand(
        standId: String,
        name: String? = null,
        counterSlot: String? = null,
        category: String? = null,
        isOpen: Boolean? = null
    ): Result<BaseResponse<StandResponse>> = safeApiCall {
        api.updateStand(
            standId = standId,
            request = UpdateStandRequest(
                name = name,
                standName = name,
                counterSlot = counterSlot,
                standNumber = counterSlot,
                category = category,
                isOpen = isOpen
            )
        )
    }

    suspend fun getMenusByStand(standId: String): Result<BaseResponse<List<MenuResponse>>> = safeApiCall {
        api.getMenusByStand(standId)
    }

    suspend fun getAllMenus(): Result<BaseResponse<List<MenuResponse>>> = safeApiCall {
        api.getAllMenus()
    }

    suspend fun getMyMenus(): Result<BaseResponse<List<MenuResponse>>> = safeApiCall {
        api.getMyMenus()
    }

    suspend fun uploadMenuImage(imagePart: MultipartBody.Part): Result<BaseResponse<UploadImageResponse>> = safeApiCall {
        api.uploadMenuImage(imagePart)
    }

    suspend fun createMenu(standId: String?, name: String, price: Int, stock: Int, imageUrl: String? = null): Result<BaseResponse<MenuResponse>> = safeApiCall {
        api.createMenu(CreateMenuRequest(standId = standId, name = name, price = price, stock = stock, image = imageUrl, imageUrl = imageUrl))
    }

    suspend fun updateMenuImage(menuId: String, imagePart: MultipartBody.Part): Result<BaseResponse<MenuResponse>> = safeApiCall {
        api.updateMenuImage(menuId, imagePart)
    }

    suspend fun deleteMenuImage(menuId: String): Result<BaseResponse<MenuResponse>> = safeApiCall {
        api.deleteMenuImage(menuId)
    }

    suspend fun updateMenu(
        menuId: String,
        name: String? = null,
        price: Int? = null,
        stock: Int? = null,
        isAvailable: Boolean? = null,
        imageUrl: String? = null
    ): Result<BaseResponse<MenuResponse>> = safeApiCall {
        api.updateMenu(
            menuId,
            UpdateMenuRequest(
                name = name,
                price = price,
                stock = stock,
                isAvailable = isAvailable,
                is_available = isAvailable,
                imageUrl = imageUrl,
                image = imageUrl
            )
        )
    }

    suspend fun updateMenuStock(menuId: String, stock: Int): Result<BaseResponse<MenuResponse>> = safeApiCall {
        api.updateMenuStock(menuId, UpdateStockRequest(stock = stock))
    }

    suspend fun deleteMenu(menuId: String): Result<BaseResponse<Any>> = safeApiCall {
        api.deleteMenu(menuId)
    }

    suspend fun createOrder(standId: String, items: List<OrderItemRequest>, paymentMethod: String, usePoints: Boolean = false, note: String? = null): Result<BaseResponse<OrderResponse>> = safeApiCall {
        val req = CreateOrderRequest(standId = standId, items = items, paymentMethod = paymentMethod, usePoints = usePoints, note = note, notes = note)
        try {
            api.createOrder(req)
        } catch (e: HttpException) {
            if (e.code() == 404) {
                api.createOrderCheckout(req)
            } else {
                throw e
            }
        }
    }

    suspend fun getOrders(status: String? = null): Result<BaseResponse<List<OrderResponse>>> = safeApiCall {
        api.getOrders(status)
    }

    suspend fun getOrderDetail(orderId: String): Result<BaseResponse<OrderResponse>> = safeApiCall {
        api.getOrderDetail(orderId)
    }

    suspend fun confirmPickup(orderId: String): Result<BaseResponse<OrderResponse>> = safeApiCall {
        api.confirmPickup(orderId)
    }

    suspend fun updateOrderStatus(orderId: String, status: String): Result<BaseResponse<OrderResponse>> = safeApiCall {
        api.updateOrderStatus(orderId, UpdateOrderStatusRequest(status = status))
    }

    suspend fun cancelOrder(orderId: String): Result<BaseResponse<OrderResponse>> = safeApiCall {
        try {
            api.cancelOrder(orderId)
        } catch (e: Exception) {
            api.updateOrderStatus(orderId, UpdateOrderStatusRequest(status = "CANCELLED"))
        }
    }

    suspend fun getSellerRevenue(): Result<BaseResponse<SellerRevenueResponse>> = safeApiCall {
        api.getSellerRevenue()
    }

    // 5. Admin
    suspend fun adminCreateStand(ownerName: String, standName: String, phoneNumber: String, counterSlot: String? = null, category: String? = null): Result<BaseResponse<StandResponse>> = safeApiCall {
        api.adminCreateStand(AdminCreateStandRequest(ownerName = ownerName, standName = standName, phoneNumber = phoneNumber, counterSlot = counterSlot, category = category))
    }

    suspend fun getAdminRevenue(startDate: String? = null, endDate: String? = null): Result<BaseResponse<AdminRevenueResponse>> = safeApiCall {
        api.getAdminRevenue(startDate, endDate)
    }

    suspend fun getAdminStandRevenue(standId: String): Result<BaseResponse<StandRevenueDetailResponse>> = safeApiCall {
        api.getAdminStandRevenue(standId)
    }

    suspend fun getAdminViolations(): Result<BaseResponse<List<ViolationResponse>>> = safeApiCall {
        api.getAdminViolations()
    }

    suspend fun addViolation(userId: String, points: Int = 5, note: String? = null): Result<BaseResponse<Any>> = safeApiCall {
        api.addViolation(userId, AddViolationRequest(points = points, note = note))
    }
}
