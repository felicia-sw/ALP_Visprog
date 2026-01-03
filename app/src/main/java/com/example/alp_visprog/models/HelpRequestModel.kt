package com.example.alp_visprog.models

// 1. The main model (What we get from the DB)

data class UserInHelpRequest(
    val id: Int,
    val name: String,
    val email: String
)

data class HelpRequestModel(
    val id: Int,
    val nameOfProduct: String,
    val description: String,
    val exchangeProductName: String,
    val location: String,
    val imageUrl: String,
    val isCheckout: Boolean,
    val categoryId: Int,
    val userId: Int,
    val createdAt: String,
    val user: UserInHelpRequest?
)

// 2. The Create Request (What we send to the API)
data class CreateHelpRequestRequest(
    val nameOfProduct: String,
    val description: String,
    val exchangeProductName: String,
    val location: String,
    val imageUrl: String,
    val categoryId: Int
)

data class GetAllHelpRequestsResponse(
    val data: List<HelpRequestModel>
)