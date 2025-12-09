package com.example.alp_visprog.uiState

sealed interface AuthenticationStatusUIState {
    data class Success(val userModelData: UserModel) : AuthenticationStatusUIState
    object Loading: AuthenticationStatusUIState
    object Start: AuthenticationStatusUIState
    data class Failed(val errorMessage: String) : AuthenticationStatusUIState
}