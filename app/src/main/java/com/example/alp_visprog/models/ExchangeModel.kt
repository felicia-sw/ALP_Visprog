package com.example.alp_visprog.models

import com.google.gson.annotations.SerializedName

// Represents a single offer from a neighbor
data class ExchangeModel(
    val id: Int,
    val name: String,
    val phone: String,
    val email: String?,
    val description: String?,
    val helpRequestId: Int
)

// The request model for a single exchange
data class CreateExchangeRequest(
    val name: String,
    val phone: String,
    val email: String,
    val description: String,
    val helpRequestId: Int
)

// NEW: The request model for the Batch Checkout
data class CheckoutRequest(
    @SerializedName("name") val name: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("email") val email: String,
    @SerializedName("description") val description: String
)

data class GetAllExchangesResponse(
    val data: List<ExchangeModel>
)

data class GeneralResponse(
    val message: String
)