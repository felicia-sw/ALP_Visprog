package com.example.alp_visprog.models

data class ProfileResponse(val data: ProfileModel?)

data class ProfileModel(
    val username: String,
    // [FIX] Make these nullable because the backend might not send them
    val fullName: String?,
    val location: String?,
    val latitude: Double?,
    val longitude: Double?,
    val bio: String?,
    val photoUrl: String?
)

data class UpdateProfileRequest(
    val fullName: String,
    val location: String,
    val latitude: Double,
    val longitude: Double,
    val bio: String?
)