package com.example.alp_visprog.models

data class ProfileResponse(val data: ProfileModel?)

data class ProfileModel(
    val username: String,
    val fullName: String,
    val location: String,
    val bio: String?,
    val photoUrl: String?
)

data class UpdateProfileRequest(
    val fullName: String,
    val location: String,
    val bio: String?
)