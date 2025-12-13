package com.example.alp_visprog.repositories

import com.example.alp_visprog.models.GeneralResponse
import com.example.alp_visprog.models.GetAllExchangesResponse
import com.example.alp_visprog.services.ExchangeAPIService
import retrofit2.Call

class ExchangeRepository(
    private val exchangeAPIService: ExchangeAPIService
) {

    // 1. Get the list of offers for a specific post
    fun getExchangeOffers(helpRequestId: Int): Call<GetAllExchangesResponse> {
        return exchangeAPIService.getExchangeOffers(helpRequestId)
    }

    // 2. Delete a specific offer
    fun deleteExchange(exchangeId: Int): Call<GeneralResponse> {
        return exchangeAPIService.deleteExchange(exchangeId)
    }
}