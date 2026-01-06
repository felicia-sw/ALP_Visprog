package com.example.alp_visprog.services

import com.example.alp_visprog.models.CheckoutRequest // Import the model
import com.example.alp_visprog.models.CreateExchangeRequest
import com.example.alp_visprog.models.GeneralResponse
import com.example.alp_visprog.models.GetAllExchangesResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ExchangeAPIService {
    // 1. View Offers
    @GET("api/exchanges")
    fun getExchangeOffers(
        @Query("helpRequestId") helpRequestId: Int
    ): Call<GetAllExchangesResponse>

    // 2. Cancel Offer
    @DELETE("api/exchanges/{exchangeId}")
    fun deleteExchange(
        @Path("exchangeId") exchangeId: Int
    ): Call<GeneralResponse>

    // 3. Create Single Exchange
    @POST("api/exchanges")
    fun createExchange(
        @Body request: CreateExchangeRequest
    ): Call<GeneralResponse>

    // 4. Batch Checkout (Uses the model from ExchangeModel.kt)
    @POST("api/checkout")
    fun checkout(
        @Body request: CheckoutRequest
    ): Call<GeneralResponse>
}