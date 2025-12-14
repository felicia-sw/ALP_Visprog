package com.example.alp_visprog.uiStates

import com.example.alp_visprog.models.HelpRequestModel

/**
 * Sealed interface representing different states of the Home screen
 */
sealed interface HomeUIState {
    object Loading : HomeUIState
    data class Success(val data: List<HelpRequestModel>) : HomeUIState
    data class Error(val errorMessage: String) : HomeUIState
}
