package com.example.alp_visprog.services

import com.example.alp_visprog.models.UserResponse
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Call

interface AuthenticationAPIService {

    @POST("api/register")
    fun register(@Body registerMap: HashMap<String, String>): Call<UserResponse>

    @POST("api/login")
    fun login(@Body loginMap: HashMap<String, String>): Call<UserResponse>

}