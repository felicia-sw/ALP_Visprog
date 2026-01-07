package com.example.alp_visprog.services

import com.example.alp_visprog.models.CreateHelpRequestRequest
import com.example.alp_visprog.models.GetAllHelpRequestsResponse
import com.example.alp_visprog.models.GeneralResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface HelpRequestAPIService {

    // [FIX] Added 's' to match backend '/api/help-requests'
    @GET("api/help-requests")
    fun getAllHelpRequests(): Call<GetAllHelpRequestsResponse>

    // [FIX] Added 's' to match backend '/api/help-requests'
    @POST("api/help-requests")
    fun createHelpRequest(
        @Header("Authorization") bearerToken: String,
        @Body request: CreateHelpRequestRequest
    ): Call<GeneralResponse>
}