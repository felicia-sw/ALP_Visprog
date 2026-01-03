package com.example.alp_visprog.services

import com.example.alp_visprog.models.ProfileResponse
import com.example.alp_visprog.models.UpdateProfileRequest
import retrofit2.Call
import retrofit2.http.*

interface ProfileAPIService {
    @GET("api/profile/me")
    fun viewProfile(@Header("Authorization") bearer: String): Call<ProfileResponse>

    @PUT("api/profile/me")
    fun updateProfile(
        @Header("Authorization") bearer: String,
        @Body req: UpdateProfileRequest
    ): Call<ProfileResponse>
}
