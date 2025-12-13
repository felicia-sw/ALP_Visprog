package com.example.alp_visprog.models

// Represents a single offer from a neighbor
data class ExchangeModel(
    val id: Int,
    val name: String,
    val phone: String,
    val email: String?,
    val description: String?,
    val helpRequestId: Int
)

// 2. The request model (what you send to create one) <-- need to add additional because you dont need id to create one
data class CreateExchangeRequest(
    val name: String,
    val phone: String,
    val email: String,
    val description: String,
    val helpRequestId: Int
)

// Represents the response from GET /api/exchanges
data class GetAllExchangesResponse(
    val data: List<ExchangeModel>
)

// Represents the response from DELETE (and other simple actions)
data class GeneralResponse(
    val message: String
)