package com.example.alp_visprog.uiStates

import com.example.alp_visprog.models.ExchangeModel

sealed interface ExchangeUIState {
    // Screen is loading data
    object Loading : ExchangeUIState

    // We successfully got the list of offers
    data class Success(val data: List<ExchangeModel>) : ExchangeUIState

    // Something went wrong (e.g., no internet)
    data class Error(val errorMessage: String) : ExchangeUIState
}