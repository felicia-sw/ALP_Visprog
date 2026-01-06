package com.example.alp_visprog.repositories

import com.example.alp_visprog.models.AddToCartRequest
import com.example.alp_visprog.models.ShoppingCartResponse
import com.example.alp_visprog.services.ShoppingCartAPIService
import retrofit2.Call

/**
 * Repository interface for Shopping Cart operations
 */
interface ShoppingCartRepositoryInterface {
    fun getCart(userId: Int): Call<ShoppingCartResponse>
    fun addToCart(userId: Int, helpRequestId: Int): Call<ShoppingCartResponse>
    fun removeFromCart(userId: Int, helpRequestId: Int): Call<ShoppingCartResponse>
}

/**
 * Implementation of Shopping Cart Repository
 * Communicates with backend API endpoints
 */
class ShoppingCartRepository(
    private val shoppingCartService: ShoppingCartAPIService
) : ShoppingCartRepositoryInterface {

    /**
     * Get user's shopping cart
     * GET /api/cart?userId=1
     */
    override fun getCart(userId: Int): Call<ShoppingCartResponse> {
        return shoppingCartService.getCart(userId)
    }

    /**
     * Add item to shopping cart
     * POST /api/cart
     * Body: { "userId": number, "helpRequestId": number }
     *
     * Backend will return:
     * - 200: Successfully added
     * - 400: Item already in cart or validation error
     * - 404: User or HelpRequest not found
     */
    override fun addToCart(userId: Int, helpRequestId: Int): Call<ShoppingCartResponse> {
        val request = AddToCartRequest(
            userId = userId,
            helpRequestId = helpRequestId
        )
        return shoppingCartService.addToCart(request)
    }

    /**
     * Remove item from shopping cart
     * DELETE /api/cart/:helpRequestId?userId=1
     *
     * Backend will return:
     * - 200: Successfully removed
     * - 404: Cart or item not found
     */
    override fun removeFromCart(userId: Int, helpRequestId: Int): Call<ShoppingCartResponse> {
        return shoppingCartService.removeFromCart(helpRequestId, userId)
    }
}