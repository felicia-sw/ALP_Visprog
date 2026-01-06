package com.example.alp_visprog.services

import com.example.alp_visprog.models.AddToCartRequest
import com.example.alp_visprog.models.ShoppingCartResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ShoppingCartAPIService {

    @GET("api/cart")
    fun getCart(@Query("userId") userId: Int): Call<ShoppingCartResponse>

    @POST("api/cart")
    fun addToCart(@Body request: AddToCartRequest): Call<ShoppingCartResponse>

    @DELETE("api/cart/{helpRequestId}")
    fun removeFromCart(
        @Path("helpRequestId") helpRequestId: Int,
        @Query("userId") userId: Int
    ): Call<ShoppingCartResponse>
}