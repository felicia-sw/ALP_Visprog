package com.example.alp_visprog.uiStates

import com.example.alp_visprog.models.ProfileModel

sealed interface ProfileStatusUIState {
    object Start : ProfileStatusUIState
    object Loading : ProfileStatusUIState
    data class Success(val profile: ProfileModel) : ProfileStatusUIState
    data class Failed(val message: String) : ProfileStatusUIState
}

sealed interface UpdateProfileStatusUIState {
    object Start : UpdateProfileStatusUIState
    object Loading : UpdateProfileStatusUIState
    data class Success(val profile: ProfileModel) : UpdateProfileStatusUIState
    data class Failed(val message: String) : UpdateProfileStatusUIState
}
