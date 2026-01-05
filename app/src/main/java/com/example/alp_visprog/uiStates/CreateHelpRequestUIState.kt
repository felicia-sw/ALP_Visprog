package com.example.alp_visprog.uiStates

sealed interface CreateHelpRequestUIState {
    object Idle : CreateHelpRequestUIState
    object Loading : CreateHelpRequestUIState
    object Success : CreateHelpRequestUIState
    data class Error(val errorMessage: String) : CreateHelpRequestUIState
}