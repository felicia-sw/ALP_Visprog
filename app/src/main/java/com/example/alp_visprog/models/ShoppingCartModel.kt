package com.example.alp_visprog.models

import com.google.gson.annotations.SerializedName

/**
 * Shopping Cart Models - Matching Backend API Structure
 *
 * Backend Endpoints:
 * - GET /api/cart?userId=1
 * - POST /api/cart (body: {userId, helpRequestId})
 * - DELETE /api/cart/:helpRequestId?userId=1
 */

// ========== RESPONSE MODELS ==========

/**
 * Main response wrapper for shopping cart operations
 * Matches backend structure: { "data": { ... } }
 */
data class ShoppingCartResponse(
    @SerializedName("data")
    val data: ShoppingCartData
)

/**
 * Shopping cart data
 * Matches backend: { id, userId, items: [...] }
 */
data class ShoppingCartData(
    @SerializedName("id")
    val id: Int,

    @SerializedName("userId")
    val userId: Int,

    @SerializedName("items")
    val items: List<CartItem>
)

/**
 * Individual cart item
 * Backend structure:
 * {
 *   "helpRequestId": number,
 *   "addedAt": Date,
 *   "productName": string,
 *   "description": string,
 *   "price": string, // This is actually exchangeProductName
 *   "imageUrl": string
 * }
 */
data class CartItem(
    @SerializedName("helpRequestId")
    val helpRequestId: Int,

    @SerializedName("addedAt")
    val addedAt: String,

    @SerializedName("productName")
    val productName: String,

    @SerializedName("description")
    val description: String,

    @SerializedName("price")
    val price: String, // Note: Backend calls it "price" but it's the exchange product name

    @SerializedName("imageUrl")
    val imageUrl: String
)

// ========== REQUEST MODELS ==========

/**
 * Request body for adding item to cart
 * POST /api/cart
 * Body: { "userId": number, "helpRequestId": number }
 */
data class AddToCartRequest(
    @SerializedName("userId")
    val userId: Int,

    @SerializedName("helpRequestId")
    val helpRequestId: Int
)

// ========== ERROR MODELS ==========

/**
 * Error response from backend
 * Example: { "errors": "Item is already in your cart" }
 */
data class CartErrorResponse(
    @SerializedName("errors")
    val errors: String
)

/**
 * Validation error from backend
 * Used for Zod validation failures
 */
data class CartValidationError(
    @SerializedName("errors")
    val errors: String
)