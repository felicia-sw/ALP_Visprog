package com.example.alp_visprog.models

import com.google.gson.annotations.SerializedName

data class LocationIQResponse(
    @SerializedName("place_id") val placeId: String,
    @SerializedName("lat") val lat: String,
    @SerializedName("lon") val lon: String,
    @SerializedName("display_name") val displayName: String
)