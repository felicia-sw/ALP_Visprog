package com.example.alp_visprog.services

import com.example.alp_visprog.models.LocationIQResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface LocationIQAPIService {
    @GET("v1/search.php")
    fun searchLocation(
        @Query("key") apiKey: String,
        @Query("q") query: String,
        @Query("format") format: String = "json"
    ): Call<List<LocationIQResponse>>
}