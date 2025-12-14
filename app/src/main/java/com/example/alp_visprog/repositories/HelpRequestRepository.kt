package com.example.alp_visprog.repositories

import com.example.alp_visprog.models.CreateHelpRequestRequest
import com.example.alp_visprog.models.GeneralResponse
import com.example.alp_visprog.models.GetAllHelpRequestsResponse
import com.example.alp_visprog.services.HelpRequestAPIService
import retrofit2.Call

class HelpRequestRepository(
    private val helpRequestAPIService: HelpRequestAPIService
) {

    fun createHelpRequest(
        title: String,
        description: String,
        category: String
    ): Call<GeneralResponse> {
        val request = CreateHelpRequestRequest(
            title = title,
            description = description,
            category = category
        )
        return helpRequestAPIService.createHelpRequest(request)
    }

    fun getAllHelpRequests(): Call<GetAllHelpRequestsResponse> {
        return helpRequestAPIService.getAllHelpRequests()
    }
}