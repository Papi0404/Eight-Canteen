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
    val standName: String = "Stand 04",
    val imageUrl: String = "",
    val imageType: FoodImageType = FoodImageType.GENERIC,
    val foodEmoji: String = "🍱"
)

data class CheckoutUiState(
    val timeSlotTitle: String = "Istirahat 1 (10:15 – 10:30 WIB)",
    val standInfo: String = "Stand 04 • Kebab Bang Ali",
    val cartItems: List<CartItem> = emptyList(),
    val orderNote: String = "Mayones dipisah, jeruk es sedikit.",
    val userPoints: Int = 50,
    val redeemPointsCost: Int = 5,
    val discountAmount: Double = 2500.0,
    val isPointsDiscountEnabled: Boolean = true,
    val serviceFee: Double = 0.0,
    val selectedPaymentMethod: PaymentMethod = PaymentMethod.QRIS,
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
