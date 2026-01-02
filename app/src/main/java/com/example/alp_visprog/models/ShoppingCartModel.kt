package com.example.alp_visprog.models

// Body for POST /api/cart
data class AddToCartRequest(
    val userId: Int,
    val helpRequestId: Int
)

// Item details inside the cart
data class CartItemResponse(
    val helpRequestId: Int,
    val addedAt: String,
    val productName: String,
    val description: String,
    val price: String, // "Exchange Product Name" acts as price
    val imageUrl: String
)

// The full cart object
data class ShoppingCartData(
    val id: Int,
    val userId: Int,
    val items: List<CartItemResponse>
)

// Wrapper for API Response
data class ShoppingCartResponse(
    val data: ShoppingCartData
)