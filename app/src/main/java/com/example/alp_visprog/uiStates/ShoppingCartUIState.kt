package com.example.alp_visprog.uiStates

import com.example.alp_visprog.models.CartItemResponse

sealed interface ShoppingCartUIState {
    object Loading : ShoppingCartUIState
    data class Success(val items: List<CartItemResponse>) : ShoppingCartUIState
    data class Error(val errorMessage: String) : ShoppingCartUIState
}