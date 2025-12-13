package com.example.alp_visprog.uiStates

import com.example.alp_visprog.models.UserModel

sealed interface AuthenticationStatusUIState { // tujuan nya hanya untuk status coding sukses/ error/ stuck, tidak perlu untuk di extend class lain
    data class Success(val userModelData: UserModel) : AuthenticationStatusUIState
    object Loading: AuthenticationStatusUIState
    object Start: AuthenticationStatusUIState
    data class Failed(val errorMessage: String) : AuthenticationStatusUIState
}