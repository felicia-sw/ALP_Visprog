package com.example.alp_visprog.repositories

import com.example.alp_visprog.models.CreateHelpRequestRequest
import com.example.alp_visprog.models.GeneralResponse
import com.example.alp_visprog.models.GetAllHelpRequestsResponse
import com.example.alp_visprog.services.HelpRequestAPIService
import retrofit2.Call

class HelpRequestRepository(
    private val helpRequestAPIService: HelpRequestAPIService
) {
    fun getAllHelpRequests(): Call<GetAllHelpRequestsResponse> {
        return helpRequestAPIService.getAllHelpRequests()
    }

    fun createHelpRequest(
        nameOfProduct: String,
        description: String,
        exchangeProductName: String,
        location: String,
        imageUrl: String,
        categoryId: Int
    ): Call<GeneralResponse> {

        val request = CreateHelpRequestRequest(
            nameOfProduct = nameOfProduct,
            description = description,
            exchangeProductName = exchangeProductName,
            location = location,
            imageUrl = imageUrl,
            categoryId = categoryId
        )

        return helpRequestAPIService.createHelpRequest(request)
    }
}