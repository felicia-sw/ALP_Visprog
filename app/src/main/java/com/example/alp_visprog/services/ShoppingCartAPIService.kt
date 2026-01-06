package com.example.alp_visprog.network

import com.example.alp_visprog.models.AddToCartRequest
import com.example.alp_visprog.models.ShoppingCartResponse
import retrofit2.Call
import retrofit2.http.*

/**
 * Retrofit Service Interface for Shopping Cart API
 *
 * Backend Base URL: http://10.0.2.2:3000
 * All endpoints return: { "data": { ... } }
 */
interface ShoppingCartService {

    /**
     * Get user's shopping cart
     * GET /api/cart?userId=1
     *
     * Response:
     * {
     *   "data": {
     *     "id": number,
     *     "userId": number,
     *     "items": [
     *       {
     *         "helpRequestId": number,
     *         "addedAt": string,
     *         "productName": string,
     *         "description": string,
     *         "price": string,
     *         "imageUrl": string
     *       }
     *     ]
     *   }
     * }
     */
    @GET("api/cart")
    fun getCart(
        @Query("userId") userId: Int
    ): Call<ShoppingCartResponse>

    /**
     * Add item to cart
     * POST /api/cart
     *
     * Request Body:
     * {
     *   "userId": number,
     *   "helpRequestId": number
     * }
     *
     * Response:
     * - 200: Successfully added (returns full cart)
     * - 400: Item already in cart
     *   { "errors": "Item is already in your cart" }
     * - 404: User or HelpRequest not found
     */
    @POST("api/cart")
    fun addToCart(
        @Body request: AddToCartRequest
    ): Call<ShoppingCartResponse>

    /**
     * Remove item from cart
     * DELETE /api/cart/:helpRequestId?userId=1
     *
     * Response:
     * - 200: Successfully removed (returns updated cart)
     * - 404: Cart or item not found
     *   { "errors": "Cart not found" } or
     *   { "errors": "Item not found in cart" }
     */
    @DELETE("api/cart/{helpRequestId}")
    fun removeFromCart(
        @Path("helpRequestId") helpRequestId: Int,
        @Query("userId") userId: Int
    ): Call<ShoppingCartResponse>
}