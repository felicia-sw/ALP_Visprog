package com.example.alp_visprog.models

// 1. The main model (What we get from the DB)
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
    val createdAt: String
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

// 3. Response wrapper (if getting a list of posts)
data class GetAllHelpRequestsResponse(
    val data: List<HelpRequestModel>
)