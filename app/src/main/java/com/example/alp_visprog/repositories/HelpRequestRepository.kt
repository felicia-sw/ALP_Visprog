package com.example.alp_visprog.repositories

import com.example.alp_visprog.models.CreateHelpRequest
import com.example.alp_visprog.models.CreateHelpRequestResponse
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
        categoryId: Int,
        userId: Int,
        // --- NEW PARAMETERS ---
        contactPhone: String,
        contactEmail: String
    ): Call<CreateHelpRequestResponse> {

        val request = CreateHelpRequest(
            nameOfProduct = nameOfProduct,
            description = description,
            exchangeProductName = exchangeProductName,
            location = location,
            imageUrl = imageUrl,
            categoryId = categoryId,
            userId = userId,
            // --- PASS THEM HERE ---
            contactPhone = contactPhone,
            contactEmail = contactEmail
        )

        return helpRequestAPIService.createHelpRequest(request)
    }
}