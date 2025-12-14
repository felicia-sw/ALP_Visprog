package com.example.alp_visprog.models

// 1. The main model (What we get from the DB)
data class HelpRequestModel(
    val id: Int,
    val title: String,
    val description: String,
    val category: String, // e.g., "Physical", "Academic", "Borrowing"
    val postedDate: String
)

// 2. The Create Request (What we send to the API)
data class CreateHelpRequestRequest(
    val title: String,
    val description: String,
    val category: String
)

// 3. Response wrapper (if getting a list of posts)
data class GetAllHelpRequestsResponse(
    val data: List<HelpRequestModel>
)