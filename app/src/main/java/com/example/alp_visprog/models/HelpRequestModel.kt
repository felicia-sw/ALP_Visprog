package com.example.alp_visprog.models

// Request Body: Matches your backend's "CreateHelpRequest" interface
data class CreateHelpRequest(
    val nameOfProduct: String,
    val description: String,
    val exchangeProductName: String,
    val location: String,
    val imageUrl: String,
    val categoryId: Int,
    val userId: Int,
    // --- NEW FIELDS ---
    val contactPhone: String,
    val contactEmail: String
)

// Response Body: Matches your backend's "HelpRequestResponse"
data class HelpRequestModel(
    val id: Int,
    val nameOfProduct: String,
    val description: String,
    val exchangeProductName: String,
    val location: String,
    val imageUrl: String,
    val isCheckout: Boolean,
    val userId: Int,
    val categoryId: Int,
    // --- NEW FIELDS ---
    val contactPhone: String,
    val contactEmail: String?
)

// API Response Wrappers
data class CreateHelpRequestResponse(
    val data: HelpRequestModel
)

data class GetAllHelpRequestsResponse(
    val data: List<HelpRequestModel>
)