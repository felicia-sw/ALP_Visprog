package com.example.alp_visprog.services

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

//    branch create exchange offer
    // 3. Create Exchange Offer (to respond to someone's post offer)
    @POST("api/exchanges")
    fun createExchange(
        @Body exchangeMap: Map<String, String> // We send data as a JSON map
    ): Call<GeneralResponse>
}