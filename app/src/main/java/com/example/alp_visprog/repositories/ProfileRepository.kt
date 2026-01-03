package com.example.alp_visprog.repositories

import com.example.alp_visprog.models.ProfileResponse
import com.example.alp_visprog.models.UpdateProfileRequest
import com.example.alp_visprog.services.ProfileAPIService
import retrofit2.Call

interface ProfileRepositoryInterface {
    fun viewProfile(bearer: String): Call<ProfileResponse>
    fun updateProfile(bearer: String, req: UpdateProfileRequest): Call<ProfileResponse>
}

class ProfileRepository(
    private val api: ProfileAPIService
) : ProfileRepositoryInterface {
    override fun viewProfile(bearer: String) = api.viewProfile(bearer)
    override fun updateProfile(bearer: String, req: UpdateProfileRequest) = api.updateProfile(bearer, req)
}
