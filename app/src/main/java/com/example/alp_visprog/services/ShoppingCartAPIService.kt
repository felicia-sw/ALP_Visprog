package com.example.alp_visprog.services

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
interface ShoppingCartAPIService {

    /**
     * Get user's shopping cart
     * GET /api/cart?userId=1
     */
    @GET("api/cart")
    fun getCart(
        @Query("userId") userId: Int
    ): Call<ShoppingCartResponse>

    /**
     * Add item to cart
     * POST /api/cart
     */
    @POST("api/cart")
    fun addToCart(
        @Body request: AddToCartRequest
    ): Call<ShoppingCartResponse>

    /**
     * Remove item from cart
     * DELETE /api/cart/:helpRequestId?userId=1
     */
    @DELETE("api/cart/{helpRequestId}")
    fun removeFromCart(
        @Path("helpRequestId") helpRequestId: Int,
        @Query("userId") userId: Int
    ): Call<ShoppingCartResponse>
}