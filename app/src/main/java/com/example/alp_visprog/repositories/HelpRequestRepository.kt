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
        bearerToken: String,
        nameOfProduct: String,
        description: String,
        exchangeProductName: String,
        location: String,
        latitude: Double,
        longitude: Double,
        imageUrl: String,
        categoryId: Int,
        userId: Int,
        contactPhone: String,
        contactEmail: String
    ): Call<GeneralResponse> {

        val request = CreateHelpRequestRequest(
            nameOfProduct = nameOfProduct,
            description = description,
            exchangeProductName = exchangeProductName,
            location = location,
            latitude = latitude,
            longitude = longitude,
            imageUrl = imageUrl,
            categoryId = categoryId,
            contactPhone = contactPhone,
            contactEmail = contactEmail
        )

        return helpRequestAPIService.createHelpRequest(bearerToken, request)
    }
}