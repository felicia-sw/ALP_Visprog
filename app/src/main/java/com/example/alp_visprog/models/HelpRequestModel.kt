package com.example.alp_visprog.models

// 1. Request Body (Matches your backend CreateHelpRequest)
data class CreateHelpRequest(
    val nameOfProduct: String,
    val description: String,
    val exchangeProductName: String,
    val location: String,
    val imageUrl: String,
    val categoryId: Int,
    val userId: Int
)

// 2. Response Body (Matches your backend HelpRequestResponse)
data class HelpRequestModel(
    val id: Int,
    val nameOfProduct: String,
    val description: String,
    val exchangeProductName: String,
    val location: String,
    val imageUrl: String,
    val isCheckout: Boolean,
    val userId: Int,
    val categoryId: Int
)

// 3. API Response Wrapper
data class CreateHelpRequestResponse(
    val data: HelpRequestModel
)

data class GetAllHelpRequestsResponse(
    val data: List<HelpRequestModel>
)