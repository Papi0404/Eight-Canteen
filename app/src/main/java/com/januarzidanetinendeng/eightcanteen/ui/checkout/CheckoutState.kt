package com.januarzidanetinendeng.eightcanteen.ui.checkout

enum class FoodImageType {
    KEBAB,
    ES_JERUK,
    KETOPRAK,
    AYAM_GEPREK,
    DIMSUM,
    ES_KOPI,
    GENERIC
}

enum class PaymentMethod {
    QRIS,
    CASH
}

typealias PaymentMethodType = PaymentMethod

data class CartItem(
    val id: String,
    val name: String,
    val price: Double,
    val quantity: Int = 1,
    val standName: String = "",
    val standId: String? = null,
    val imageUrl: String = "",
    val imageType: FoodImageType = FoodImageType.GENERIC,
    val foodEmoji: String = "🍱",
    val note: String = ""
)

data class CheckoutUiState(
    val timeSlotTitle: String = "Istirahat 1 (10:15 – 10:30 WIB)",
    val standInfo: String = "",
    val cartItems: List<CartItem> = emptyList(),
    val orderNote: String = "",
    val userPoints: Int = 0,
    val redeemPointsCost: Int = 20,
    val discountAmount: Double = 0.0,
    val isPointsDiscountEnabled: Boolean = false,
    val serviceFee: Double = 0.0,
    val selectedPaymentMethod: PaymentMethod = PaymentMethod.CASH,
    val isPaymentSuccess: Boolean = false
) {
    val totalMenuCount: Int
        get() = cartItems.sumOf { it.quantity }

    val subtotal: Double
        get() = cartItems.sumOf { it.price * it.quantity }

    val actualDiscount: Double
        get() = if (isPointsDiscountEnabled && cartItems.isNotEmpty()) discountAmount else 0.0

    val totalPayment: Double
        get() = (subtotal + serviceFee - actualDiscount).coerceAtLeast(0.0)

    val totalBill: Double
        get() = totalPayment

    val selectedPaymentMethodName: String
        get() = when (selectedPaymentMethod) {
            PaymentMethod.QRIS -> "QRIS Otomatis"
            PaymentMethod.CASH -> "Cash di Stand"
        }
}
