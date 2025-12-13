package com.example.alp_visprog.uiStates

sealed interface CreateExchangeUIState {
    // 1. Idle: The form is ready, user is typing
    object Idle : CreateExchangeUIState

    // 2. Loading: The "Submit" button was clicked, waiting for backend
    object Loading : CreateExchangeUIState

    // 3. Success: Offer was created successfully!
    object Success : CreateExchangeUIState

    // 4. Error: Something went wrong (e.g. empty fields)
    data class Error(val errorMessage: String) : CreateExchangeUIState
}