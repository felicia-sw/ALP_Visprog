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
    val user: UserModel? = null,
    val contactPhone: String? = null,
    val contactEmail: String? = null,
    val latitude: Double?,
    val longitude: Double?,
)

data class CreateHelpRequestRequest(
    val nameOfProduct: String,
    val description: String,
    val exchangeProductName: String,
    val location: String,
    val latitude: Double,
    val longitude: Double,
    val imageUrl: String,
    val categoryId: Int,
    val contactPhone: String,
    val contactEmail: String,

    // [FIX] Add this field so it gets sent to the backend
    val userId: Int
)

// 3. The Create Request with contact info (used by repository)
data class CreateHelpRequest(
    val nameOfProduct: String,
    val description: String,
    val exchangeProductName: String,
    val location: String,
    val imageUrl: String,
    val categoryId: Int,
    val userId: Int,
    val contactPhone: String,
    val contactEmail: String
)

// 4. The Create Response
data class CreateHelpRequestResponse(
    val message: String,
    val data: HelpRequestModel
)

data class GetAllHelpRequestsResponse(
    val data: List<HelpRequestModel>
)