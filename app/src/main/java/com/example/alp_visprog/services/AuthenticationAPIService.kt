package com.example.alp_visprog.services

import com.example.alp_visprog.models.UserResponse
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded

interface AuthenticationAPIService {

    @POST("api/register") // /api itu di ambil dari backend di file main.ts
    fun register(@Body registerMap: HashMap<String, String>): Call<UserResponse>

    @POST("api/login")
    fun login(@Body loginMap: HashMap<String, String>): Call<UserResponse>


    @POST("create-user")
    @FormUrlEncoded
    fun register(
        @Field("username") username: String,
        @Field("email") email: String,
        @Field("password") pass: String,
        @Field("phone") phone: String,
        // Add these new fields:
        @Field("location") location: String,
        @Field("latitude") lat: Double,
        @Field("longitude") lon: Double
    ): Call<UserResponse>
}

