package com.example.alp_visprog.models

data class UserResponse (
    val data: UserModel
)
data class UserModel (
    val token: String?
)