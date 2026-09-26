package com.januarzidanetinendeng.eightcanteen.data.remote

import com.google.gson.annotations.SerializedName
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
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

    @PUT("users/me")
    suspend fun updateProfile(
        @Body request: UpdateProfileRequest
    ): BaseResponse<UserProfile>

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

    @POST("orders/checkout")
    suspend fun createOrderCheckout(
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
    @SerializedName("className") val className: String? = null,
    @SerializedName("studentClass") val studentClass: String? = null,
    @SerializedName("class_name") val class_name: String? = null,
    @SerializedName("standName") val standName: String? = null
)

data class UpdateProfileRequest(
    @SerializedName("name") val name: String? = null,
    @SerializedName("fullName") val fullName: String? = null,
    @SerializedName("className") val className: String? = null,
    @SerializedName("studentClass") val studentClass: String? = null,
    @SerializedName("class_name") val class_name: String? = null,
    @SerializedName("nis") val nis: String? = null
)

data class LoginResponseData(
    @SerializedName("token") val token: String,
    @SerializedName("user") val user: UserProfile,
    @SerializedName("isNewUser") val isNewUser: Boolean? = null,
    @SerializedName("isProfileComplete") val isProfileComplete: Boolean? = null
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
    @SerializedName("className") val className: String? = null,
    @SerializedName("class_name") val class_name: String? = null,
    @SerializedName("stand") val stand: StandResponse? = null,
    @SerializedName("isNewUser") val isNewUser: Boolean? = null,
    @SerializedName("isProfileComplete") val isProfileComplete: Boolean? = null
) {
    val resolvedClass: String? get() = studentClass ?: className ?: class_name
}

// Stand & Menu Models
data class StandResponse(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("ownerName") val ownerName: String? = null,
    @SerializedName("counterNumber") private val _counterNumber: String? = null,
    @SerializedName("stand_number") private val _stand_number: String? = null,
    @SerializedName("counterSlot") private val _counterSlot: String? = null,
    @SerializedName("isOpen") private val _isOpen: Boolean? = null,
    @SerializedName("is_open") private val _is_open: Boolean? = null,
    @SerializedName("category") val category: String? = null,
    @SerializedName("rating") val rating: Double? = 4.8
) {
    val counterSlot: String? get() = _counterSlot ?: _stand_number ?: _counterNumber
    val counterNumber: String? get() = _counterNumber ?: _stand_number ?: _counterSlot
    val isOpen: Boolean get() = _isOpen ?: _is_open ?: true
}

data class MenuResponse(
    @SerializedName("id") val id: String,
    @SerializedName("stand_id") private val _stand_id: String? = null,
    @SerializedName("standId") private val _standId: String? = null,
    @SerializedName("name") val name: String,
    @SerializedName("price") val price: Int,
    @SerializedName("stock") val stock: Int,
    @SerializedName("image_url") val imageUrl: String? = null,
    @SerializedName("is_available") val isAvailable: Boolean = true,
    @SerializedName("prepareTime") val prepareTime: String? = "10 mnt",
    @SerializedName("stands") val stands: StandResponse? = null
) {
    val standId: String? get() = _stand_id ?: _standId ?: stands?.id
}

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
    @SerializedName("orderNumber") private val _orderNumber: String? = null,
    @SerializedName("order_number") private val _order_number: String? = null,
    @SerializedName("standId") private val _standId: String? = null,
    @SerializedName("stand_id") private val _stand_id: String? = null,
    @SerializedName("standName") val standName: String? = null,
    @SerializedName("userId") private val _userId: String? = null,
    @SerializedName("student_id") private val _student_id: String? = null,
    @SerializedName("studentName") val studentName: String? = null,
    @SerializedName("studentClass") val studentClass: String? = null,
    @SerializedName("items") val items: List<OrderItemDetail>? = null,
    @SerializedName("order_items") private val _order_items: List<OrderItemDetail>? = null,
    @SerializedName("orderItems") private val _orderItems: List<OrderItemDetail>? = null,
    @SerializedName("stands") val stands: StandResponse? = null,
    @SerializedName("totalAmount") private val _totalAmount: Int? = null,
    @SerializedName("total_amount") private val _total_amount: Int? = null,
    @SerializedName("paymentMethod") private val _paymentMethod: String? = null,
    @SerializedName("payment_method") private val _payment_method: String? = null,
    @SerializedName("paymentStatus") private val _paymentStatus: String? = null,
    @SerializedName("payment_status") private val _payment_status: String? = null,
    @SerializedName("status") val status: String? = null,
    @SerializedName("qrCode") private val _qrCode: String? = null,
    @SerializedName("barcode") private val _barcode: String? = null,
    @SerializedName("pickupTime") val pickupTime: String? = null,
    @SerializedName("createdAt") private val _createdAt: String? = null,
    @SerializedName("created_at") private val _created_at: String? = null
) {
    val orderNumber: String get() = _orderNumber ?: _order_number ?: id.take(8).uppercase()
    val standId: String? get() = _standId ?: _stand_id ?: stands?.id
    val userId: String? get() = _userId ?: _student_id
    val totalAmount: Int get() = _totalAmount ?: _total_amount ?: 0
    val paymentMethod: String? get() = _paymentMethod ?: _payment_method
    val paymentStatus: String get() = _paymentStatus ?: _payment_status ?: "PENDING"
    val qrCode: String? get() = _qrCode ?: _order_number ?: _orderNumber
    val barcode: String? get() = _barcode ?: _order_number ?: _orderNumber
    val createdAt: String? get() = _createdAt ?: _created_at
    val orderItems: List<OrderItemDetail> get() = items ?: _order_items ?: _orderItems ?: emptyList()
}

data class OrderItemDetail(
    @SerializedName("menuId") private val _menuId: String? = null,
    @SerializedName("menu_id") private val _menu_id: String? = null,
    @SerializedName("menuName") val menuName: String? = null,
    @SerializedName("menus") val menus: MenuResponse? = null,
    @SerializedName("price") private val _price: Int? = null,
    @SerializedName("price_at_time") private val _price_at_time: Int? = null,
    @SerializedName("quantity") val quantity: Int = 1
) {
    val menuId: String? get() = _menuId ?: _menu_id ?: menus?.id
    val name: String get() = menuName ?: menus?.name ?: "Menu Makanan"
    val price: Int get() = _price ?: _price_at_time ?: menus?.price ?: 0
}

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