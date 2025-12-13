package com.example.alp_visprog.services

import com.example.alp_visprog.models.CreateHelpRequestRequest
import com.example.alp_visprog.models.GeneralResponse
import com.example.alp_visprog.models.GetAllHelpRequestsResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface HelpRequestAPIService {

    // 1. Create a new Help Request
    @POST("api/help-requests")
    fun createHelpRequest(
        @Body request: CreateHelpRequestRequest
    ): Call<GeneralResponse>

    // 2. Get all Help Requests (for the Home Feed later)
    @GET("api/help-requests")
    fun getAllHelpRequests(): Call<GetAllHelpRequestsResponse>
}