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

data class CartItem(
    val id: String,
    val name: String,
    val price: Int,
    val quantity: Int = 1,
    val standName: String = "Stand 04",
    val imageType: FoodImageType = FoodImageType.GENERIC,
    val foodEmoji: String = "🍱"
)

enum class PaymentMethodType {
    QRIS_AUTOMATIC,
    CASH_AT_STAND
}

data class CheckoutUiState(
    val timeSlotTitle: String = "Istirahat 1 (10:15 – 10:30 WIB)",
    val standInfo: String = "Stand 04 • Kebab & Burger Bang Ali",
    val cartItems: List<CartItem> = emptyList(),
    val orderNote: String = "Mayones dipisah, jeruk es sedikit.",
    val userPoints: Int = 50,
    val redeemPointsCost: Int = 5,
    val discountAmount: Int = 2500,
    val isPointsDiscountEnabled: Boolean = true,
    val serviceFee: Int = 0,
    val selectedPaymentMethod: PaymentMethodType = PaymentMethodType.QRIS_AUTOMATIC,
    val isPaymentSuccess: Boolean = false
) {
    val totalMenuCount: Int
        get() = cartItems.sumOf { it.quantity }

    val subtotal: Int
        get() = cartItems.sumOf { it.price * it.quantity }

    val actualDiscount: Int
        get() = if (isPointsDiscountEnabled && cartItems.isNotEmpty()) discountAmount else 0

    val totalPayment: Int
        get() = (subtotal + serviceFee - actualDiscount).coerceAtLeast(0)

    // Property totalBill alias untuk compatibility
    val totalBill: Int
        get() = totalPayment

    val selectedPaymentMethodName: String
        get() = when (selectedPaymentMethod) {
            PaymentMethodType.QRIS_AUTOMATIC -> "QRIS Otomatis"
            PaymentMethodType.CASH_AT_STAND -> "Cash di Stand"
        }
}
