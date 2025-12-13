package com.example.alp_visprog.services

import com.example.alp_visprog.models.GeneralResponse
import com.example.alp_visprog.models.GetAllExchangesResponse
import retrofit2.Call
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ExchangeAPIService {
    // 1. View Offers: Get list of exchanges for a specific post
    @GET("api/exchanges")
    fun getExchangeOffers(
        @Query("helpRequestId") helpRequestId: Int
    ): Call<GetAllExchangesResponse>

    // 2. Cancel Offer: Delete a specific exchange
    @DELETE("api/exchanges/{exchangeId}")
    fun deleteExchange(
        @Path("exchangeId") exchangeId: Int
    ): Call<GeneralResponse>
}