package com.example.alp_visprog.repositories

import com.example.alp_visprog.models.AddToCartRequest
import com.example.alp_visprog.models.ShoppingCartResponse
import com.example.alp_visprog.services.ShoppingCartAPIService
import retrofit2.Call

class ShoppingCartRepository(
    private val shoppingCartAPIService: ShoppingCartAPIService
) {

    fun getCart(userId: Int): Call<ShoppingCartResponse> {
        return shoppingCartAPIService.getCart(userId)
    }

    fun addToCart(userId: Int, helpRequestId: Int): Call<ShoppingCartResponse> {
        val request = AddToCartRequest(userId, helpRequestId)
        return shoppingCartAPIService.addToCart(request)
    }

    fun removeFromCart(userId: Int, helpRequestId: Int): Call<ShoppingCartResponse> {
        return shoppingCartAPIService.removeFromCart(helpRequestId, userId)
    }
}