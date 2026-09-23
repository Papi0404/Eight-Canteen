package com.januarzidanetinendeng.eightcanteen.ui.checkout

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CartViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(
        CheckoutUiState(
            cartItems = emptyList()
        )
    )
    val uiState: StateFlow<CheckoutUiState> = _uiState.asStateFlow()

    val cartItems: List<CartItem>
        get() = _uiState.value.cartItems

    val selectedPaymentMethod: PaymentMethod
        get() = _uiState.value.selectedPaymentMethod

    val subtotal: Double
        get() = _uiState.value.subtotal

    val discount: Double
        get() = _uiState.value.actualDiscount

    val totalPayment: Double
        get() = _uiState.value.totalPayment

    fun addToCart(item: CartItem) {
        _uiState.update { currentState ->
            val safeCart = currentState.cartItems
            val existingIndex = safeCart.indexOfFirst { it.id == item.id }
            val updatedList = if (existingIndex != -1) {
                safeCart.mapIndexed { index, existingItem ->
                    if (index == existingIndex) {
                        existingItem.copy(quantity = existingItem.quantity + maxOf(1, item.quantity))
                    } else {
                        existingItem
                    }
                }
            } else {
                safeCart + item.copy(quantity = maxOf(1, item.quantity))
            }
            currentState.copy(cartItems = updatedList)
        }
    }

    fun updateQuantity(itemId: String, newQuantity: Int) {
        _uiState.update { currentState ->
            val safeCart = currentState.cartItems
            val updatedList = if (newQuantity <= 0) {
                safeCart.filterNot { it.id == itemId }
            } else {
                safeCart.map { item ->
                    if (item.id == itemId) item.copy(quantity = newQuantity) else item
                }
            }
            currentState.copy(cartItems = updatedList)
        }
    }

    fun updateItemQuantity(itemId: String, delta: Int) {
        val safeCart = _uiState.value.cartItems
        val currentQty = safeCart.find { it.id == itemId }?.quantity ?: 0
        updateQuantity(itemId, currentQty + delta)
    }

    fun setPaymentMethod(method: PaymentMethod) {
        _uiState.update { currentState ->
            currentState.copy(selectedPaymentMethod = method)
        }
    }

    fun selectPaymentMethod(method: PaymentMethod) = setPaymentMethod(method)

    fun togglePointsDiscount(enabled: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(isPointsDiscountEnabled = enabled)
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

typealias CheckoutViewModel = CartViewModel
