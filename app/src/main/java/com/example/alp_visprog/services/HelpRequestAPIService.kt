package com.example.alp_visprog.services

import com.example.alp_visprog.models.CreateHelpRequest
import com.example.alp_visprog.models.CreateHelpRequestResponse
import com.example.alp_visprog.models.GetAllHelpRequestsResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Header

interface HelpRequestAPIService {
    @GET("api/help-requests")
    fun getAllHelpRequests(): Call<GetAllHelpRequestsResponse>

    @POST("api/help-requests")
    fun createHelpRequest(
        @Body request: CreateHelpRequest
    ): Call<CreateHelpRequestResponse>

    @GET("api/help-requests/user")
    fun getUserHelpRequests(
        @Header("Authorization") bearerToken: String
    ): Call<GetAllHelpRequestsResponse>
}