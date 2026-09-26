package com.januarzidanetinendeng.eightcanteen.data.remote

import com.google.gson.annotations.SerializedName
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    // ==========================================
    // 1. AUTHENTICATION ENDPOINTS
    // ==========================================
    @POST("auth/request-otp")
    suspend fun requestOtp(
        @Body request: OtpRequest
    ): BaseResponse<Any>

    @POST("auth/verify-otp")
    suspend fun verifyOtp(
        @Body request: VerifyOtpRequest
    ): BaseResponse<LoginResponseData>

    @POST("auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): BaseResponse<UserProfile>

    // ==========================================
    // 2. USER PROFILE ENDPOINTS
    // ==========================================
    @GET("users/me")
    suspend fun getMyProfile(): BaseResponse<UserProfile>

    // ==========================================
    // 3. STAND & MENU ENDPOINTS
    // ==========================================
    @GET("stands")
    suspend fun getStands(): BaseResponse<List<StandResponse>>

    @GET("stands/{standId}/menus")
    suspend fun getMenusByStand(
        @Path("standId") standId: String
    ): BaseResponse<List<MenuResponse>>

    @GET("menus")
    suspend fun getAllMenus(): BaseResponse<List<MenuResponse>>

    @POST("menus")
    suspend fun createMenu(
        @Body request: CreateMenuRequest
    ): BaseResponse<MenuResponse>

    @PATCH("menus/{menuId}")
    suspend fun updateMenu(
        @Path("menuId") menuId: String,
        @Body request: UpdateMenuRequest
    ): BaseResponse<MenuResponse>

    @PATCH("menus/{menuId}/stock")
    suspend fun updateMenuStock(
        @Path("menuId") menuId: String,
        @Body request: UpdateStockRequest
    ): BaseResponse<MenuResponse>

    @DELETE("menus/{menuId}")
    suspend fun deleteMenu(
        @Path("menuId") menuId: String
    ): BaseResponse<Any>

    // ==========================================
    // 4. ORDER & PAYMENT ENDPOINTS
    // ==========================================
    @POST("orders")
    suspend fun createOrder(
        @Body request: CreateOrderRequest
    ): BaseResponse<OrderResponse>

    @GET("orders")
    suspend fun getOrders(
        @Query("status") status: String? = null
    ): BaseResponse<List<OrderResponse>>

    @GET("orders/{orderId}")
    suspend fun getOrderDetail(
        @Path("orderId") orderId: String
    ): BaseResponse<OrderResponse>

    @PATCH("orders/{orderId}/pickup")
    suspend fun confirmPickup(
        @Path("orderId") orderId: String
    ): BaseResponse<OrderResponse>

    // ==========================================
    // 5. ADMIN ENDPOINTS
    // ==========================================
    @POST("admin/stands")
    suspend fun adminCreateStand(
        @Body request: AdminCreateStandRequest
    ): BaseResponse<StandResponse>

    @GET("admin/revenue")
    suspend fun getAdminRevenue(
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String? = null
    ): BaseResponse<AdminRevenueResponse>

    @GET("admin/revenue/{standId}")
    suspend fun getAdminStandRevenue(
        @Path("standId") standId: String
    ): BaseResponse<StandRevenueDetailResponse>

    @GET("admin/violations")
    suspend fun getAdminViolations(): BaseResponse<List<ViolationResponse>>

    @POST("admin/violations/{userId}")
    suspend fun addViolation(
        @Path("userId") userId: String,
        @Body request: AddViolationRequest
    ): BaseResponse<Any>
}

// =============================================================================
// DATA TRANSFER OBJECTS (REQUEST & RESPONSE MODELS)
// =============================================================================

data class BaseResponse<T>(
    @SerializedName("status") val status: String? = "success",
    @SerializedName("message") val message: String? = null,
    @SerializedName("data") val data: T? = null
)

// Auth Models
data class OtpRequest(
    @SerializedName("phoneNumber") val phoneNumber: String
)

data class VerifyOtpRequest(
    @SerializedName("phoneNumber") val phoneNumber: String,
    @SerializedName("otp") val otp: String
)

data class RegisterRequest(
    @SerializedName("name") val name: String,
    @SerializedName("role") val role: String, // "Siswa", "Penjual", "Admin"
    @SerializedName("nis") val nis: String? = null,
    @SerializedName("standName") val standName: String? = null
)

data class LoginResponseData(
    @SerializedName("token") val token: String,
    @SerializedName("user") val user: UserProfile
)

data class UserProfile(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String? = null,
    @SerializedName("fullName") val fullName: String? = null,
    @SerializedName("phoneNumber") val phoneNumber: String? = null,
    @SerializedName("role") val role: String? = null,
    @SerializedName("points") val points: Int? = 0,
    @SerializedName("nis") val nis: String? = null,
    @SerializedName("studentClass") val studentClass: String? = null,
    @SerializedName("stand") val stand: StandResponse? = null
)

// Stand & Menu Models
data class StandResponse(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("ownerName") val ownerName: String? = null,
    @SerializedName("counterNumber") val counterNumber: String? = null,
    @SerializedName("counterSlot") val counterSlot: String? = null,
    @SerializedName("isOpen") val isOpen: Boolean? = true,
    @SerializedName("category") val category: String? = null,
    @SerializedName("rating") val rating: Double? = 4.8
)

data class MenuResponse(
    @SerializedName("id") val id: String,
    @SerializedName("stand_id") val standId: String? = null,
    @SerializedName("name") val name: String,
    @SerializedName("price") val price: Int,
    @SerializedName("stock") val stock: Int,
    @SerializedName("image_url") val imageUrl: String? = null,
    @SerializedName("is_available") val isAvailable: Boolean = true,
    @SerializedName("prepareTime") val prepareTime: String? = "10 mnt",
    @SerializedName("stands") val stands: StandResponse? = null
)

data class CreateMenuRequest(
    @SerializedName("standId") val standId: String? = null,
    @SerializedName("name") val name: String,
    @SerializedName("price") val price: Int,
    @SerializedName("stock") val stock: Int,
    @SerializedName("image") val image: String? = null
)

data class UpdateMenuRequest(
    @SerializedName("name") val name: String? = null,
    @SerializedName("price") val price: Int? = null,
    @SerializedName("isAvailable") val isAvailable: Boolean? = null
)

data class UpdateStockRequest(
    @SerializedName("stock") val stock: Int
)

// Order Models
data class CreateOrderRequest(
    @SerializedName("standId") val standId: String,
    @SerializedName("items") val items: List<OrderItemRequest>,
    @SerializedName("paymentMethod") val paymentMethod: String, // "QRIS" or "TUNAI"
    @SerializedName("usePoints") val usePoints: Boolean = false,
    @SerializedName("note") val note: String? = null
)

data class OrderItemRequest(
    @SerializedName("menuId") val menuId: String,
    @SerializedName("quantity") val quantity: Int
)

data class OrderResponse(
    @SerializedName("id") val id: String,
    @SerializedName("orderNumber") val orderNumber: String? = null,
    @SerializedName("standId") val standId: String? = null,
    @SerializedName("standName") val standName: String? = null,
    @SerializedName("userId") val userId: String? = null,
    @SerializedName("studentName") val studentName: String? = null,
    @SerializedName("studentClass") val studentClass: String? = null,
    @SerializedName("items") val items: List<OrderItemDetail>? = null,
    @SerializedName("totalAmount") val totalAmount: Int? = 0,
    @SerializedName("paymentMethod") val paymentMethod: String? = null,
    @SerializedName("status") val status: String? = null, // "PENDING", "COOKING", "READY", "COMPLETED", "CANCELLED"
    @SerializedName("qrCode") val qrCode: String? = null,
    @SerializedName("barcode") val barcode: String? = null,
    @SerializedName("pickupTime") val pickupTime: String? = null,
    @SerializedName("createdAt") val createdAt: String? = null
)

data class OrderItemDetail(
    @SerializedName("menuId") val menuId: String? = null,
    @SerializedName("menuName") val menuName: String? = null,
    @SerializedName("price") val price: Int? = 0,
    @SerializedName("quantity") val quantity: Int = 1
)

// Admin Models
data class AdminCreateStandRequest(
    @SerializedName("ownerName") val ownerName: String,
    @SerializedName("standName") val standName: String,
    @SerializedName("phoneNumber") val phoneNumber: String,
    @SerializedName("counterSlot") val counterSlot: String? = null,
    @SerializedName("category") val category: String? = null
)

data class AdminRevenueResponse(
    @SerializedName("grossIncome") val grossIncome: Long? = 0,
    @SerializedName("netIncome") val netIncome: Long? = 0,
    @SerializedName("koperasiFee") val koperasiFee: Long? = 0,
    @SerializedName("totalOrders") val totalOrders: Int? = 0,
    @SerializedName("standsRevenue") val standsRevenue: List<StandRevenueItem>? = null
)

data class StandRevenueItem(
    @SerializedName("standId") val standId: String,
    @SerializedName("standName") val standName: String,
    @SerializedName("grossIncome") val grossIncome: Long,
    @SerializedName("progress") val progress: Float? = 0.5f
)

data class StandRevenueDetailResponse(
    @SerializedName("standId") val standId: String,
    @SerializedName("standName") val standName: String? = null,
    @SerializedName("qrisBalance") val qrisBalance: Long? = 0,
    @SerializedName("readyToPayout") val readyToPayout: Long? = 0,
    @SerializedName("accountNumber") val accountNumber: String? = null
)

data class ViolationResponse(
    @SerializedName("id") val id: String,
    @SerializedName("userId") val userId: String? = null,
    @SerializedName("studentName") val studentName: String? = null,
    @SerializedName("studentClass") val studentClass: String? = null,
    @SerializedName("orderId") val orderId: String? = null,
    @SerializedName("amount") val amount: Int? = 0,
    @SerializedName("standName") val standName: String? = null,
    @SerializedName("note") val note: String? = null,
    @SerializedName("createdAt") val createdAt: String? = null
)

data class AddViolationRequest(
    @SerializedName("points") val points: Int = 5,
    @SerializedName("note") val note: String? = null
)