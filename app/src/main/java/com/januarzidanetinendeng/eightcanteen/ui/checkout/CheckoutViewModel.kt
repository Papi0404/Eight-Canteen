package com.januarzidanetinendeng.eightcanteen.ui.checkout

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CartViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(
        CheckoutUiState(
            // Pre-populated default items so cart is ready or can be updated from Beranda
            cartItems = listOf(
                CartItem(
                    id = "m1",
                    name = "Kebab Beef Jumbo",
                    price = 15000,
                    quantity = 1,
                    standName = "Stand 04",
                    imageType = FoodImageType.KEBAB,
                    foodEmoji = "🥙"
                ),
                CartItem(
                    id = "m2",
                    name = "Ketoprak Telur Spesial",
                    price = 14000,
                    quantity = 1,
                    standName = "Ketoprak Bu Joko",
                    imageType = FoodImageType.KETOPRAK,
                    foodEmoji = "🍲"
                ),
                CartItem(
                    id = "m3",
                    name = "Ayam Sambal Matah",
                    price = 16000,
                    quantity = 1,
                    standName = "Ayam Geprek 8",
                    imageType = FoodImageType.AYAM_GEPREK,
                    foodEmoji = "🍗"
                ),
                CartItem(
                    id = "m5",
                    name = "Es Kopi Susu Aren 8",
                    price = 10000,
                    quantity = 1,
                    standName = "Official Barista 8",
                    imageType = FoodImageType.ES_KOPI,
                    foodEmoji = "🧋"
                )
            )
        )
    )
    val uiState: StateFlow<CheckoutUiState> = _uiState.asStateFlow()

    fun addToCart(item: CartItem) {
        _uiState.update { currentState ->
            val existingItemIndex = currentState.cartItems.indexOfFirst { it.id == item.id }
            val updatedList = if (existingItemIndex != -1) {
                currentState.cartItems.mapIndexed { index, existingItem ->
                    if (index == existingItemIndex) {
                        existingItem.copy(quantity = existingItem.quantity + maxOf(1, item.quantity))
                    } else {
                        existingItem
                    }
                }
            } else {
                currentState.cartItems + item.copy(quantity = maxOf(1, item.quantity))
            }
            currentState.copy(cartItems = updatedList)
        }
    }

    fun updateQuantity(itemId: String, delta: Int) {
        _uiState.update { currentState ->
            val updatedList = currentState.cartItems.mapNotNull { item ->
                if (item.id == itemId) {
                    val newQty = item.quantity + delta
                    if (newQty > 0) item.copy(quantity = newQty) else null
                } else {
                    item
                }
            }
            currentState.copy(cartItems = updatedList)
        }
    }

    fun updateItemQuantity(itemId: String, delta: Int) {
        updateQuantity(itemId, delta)
    }

    fun removeFromCart(itemId: String) {
        _uiState.update { currentState ->
            currentState.copy(cartItems = currentState.cartItems.filterNot { it.id == itemId })
        }
    }

    fun togglePointsDiscount(enabled: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(isPointsDiscountEnabled = enabled)
        }
    }

    fun selectPaymentMethod(method: PaymentMethodType) {
        _uiState.update { currentState ->
            currentState.copy(selectedPaymentMethod = method)
        }
    }

    fun updateOrderNote(newNote: String) {
        _uiState.update { currentState ->
            currentState.copy(orderNote = newNote)
        }
    }

    fun processPayment() {
        _uiState.update { currentState ->
            currentState.copy(isPaymentSuccess = true)
        }
    }

    fun clearCart() {
        _uiState.update { currentState ->
            currentState.copy(cartItems = emptyList(), isPaymentSuccess = false)
        }
    }
}

// Alias for backwards compatibility
typealias CheckoutViewModel = CartViewModel
