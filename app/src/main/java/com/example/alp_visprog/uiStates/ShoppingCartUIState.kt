package com.example.alp_visprog.uiStates

import com.example.alp_visprog.models.CartItem

sealed interface ShoppingCartUIState {
    object Loading : ShoppingCartUIState
    data class Success(val items: List<CartItem>) : ShoppingCartUIState
    data class Error(val errorMessage: String) : ShoppingCartUIState
}