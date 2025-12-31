package com.example.alp_visprog.repositories

import com.example.alp_visprog.models.CreateHelpRequest
import com.example.alp_visprog.models.CreateHelpRequestResponse
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
        categoryId: Int,
        userId: Int
    ): Call<CreateHelpRequestResponse> {
        val request = CreateHelpRequest(
            nameOfProduct = nameOfProduct,
            description = description,
            exchangeProductName = exchangeProductName,
            location = location,
            imageUrl = imageUrl,
            categoryId = categoryId,
            userId = userId
        )
        return helpRequestAPIService.createHelpRequest(request)
    }
}