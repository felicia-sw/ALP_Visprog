package com.example.alp_visprog.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.alp_visprog.App
import com.example.alp_visprog.models.ErrorModel
import com.example.alp_visprog.models.ProfileResponse
import com.example.alp_visprog.models.UpdateProfileRequest
import com.example.alp_visprog.repositories.ProfileRepositoryInterface
import com.example.alp_visprog.repositories.UserRepositoryInterface
import com.example.alp_visprog.uiStates.ProfileStatusUIState
import com.example.alp_visprog.uiStates.UpdateProfileStatusUIState
import com.google.gson.Gson
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.jvm.java
import kotlin.text.isBlank

class ProfileViewModel(
    private val profileRepository: ProfileRepositoryInterface,
    private val userRepository: UserRepositoryInterface
) : ViewModel() {

    // ✅ HARUS mutableStateOf supaya Compose bisa recompose (biar tidak stuck di Loading)
    var profileStatus: ProfileStatusUIState by mutableStateOf(ProfileStatusUIState.Start)
        private set

    var updateStatus: UpdateProfileStatusUIState by mutableStateOf(UpdateProfileStatusUIState.Start)
        private set

    fun fetchProfile() {
        viewModelScope.launch {
            profileStatus = ProfileStatusUIState.Loading

            val token = userRepository.currentUserToken.first()
            if (token == "Unknown" || token.isBlank()) {
                profileStatus = ProfileStatusUIState.Failed("Token belum ada. Silakan login dulu.")
                return@launch
            }

            val bearer = "Bearer $token"

            profileRepository.viewProfile(bearer).enqueue(object : Callback<ProfileResponse> {
                override fun onResponse(call: Call<ProfileResponse>, res: Response<ProfileResponse>) {
                    if (res.isSuccessful && res.body() != null) {
                        profileStatus = ProfileStatusUIState.Success(res.body()!!.data)
                    } else {
                        val msg = try {
                            val err = Gson().fromJson(res.errorBody()!!.charStream(), ErrorModel::class.java)
                            err.errors
                        } catch (_: Exception) {
                            "Gagal memuat profile (${res.code()})"
                        }
                        profileStatus = ProfileStatusUIState.Failed(msg)
                    }
                }

                override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                    profileStatus = ProfileStatusUIState.Failed(t.localizedMessage ?: "Network error")
                }
            })
        }
    }

    fun updateProfile(fullName: String, location: String, bio: String?) {
        viewModelScope.launch {
            updateStatus = UpdateProfileStatusUIState.Loading

            val token = userRepository.currentUserToken.first()
            if (token == "Unknown" || token.isBlank()) {
                updateStatus = UpdateProfileStatusUIState.Failed("Token belum ada. Silakan login dulu.")
                return@launch
            }

            val bearer = "Bearer $token"
            val req = UpdateProfileRequest(fullName = fullName, location = location, bio = bio)

            profileRepository.updateProfile(bearer, req).enqueue(object : Callback<ProfileResponse> {
                override fun onResponse(call: Call<ProfileResponse>, res: Response<ProfileResponse>) {
                    if (res.isSuccessful && res.body() != null) {
                        val profile = res.body()!!.data
                        updateStatus = UpdateProfileStatusUIState.Success(profile)
                        profileStatus = ProfileStatusUIState.Success(profile) // refresh UI
                    } else {
                        val msg = try {
                            val err = Gson().fromJson(res.errorBody()!!.charStream(), ErrorModel::class.java)
                            err.errors
                        } catch (_: Exception) {
                            "Gagal update profile (${res.code()})"
                        }
                        updateStatus = UpdateProfileStatusUIState.Failed(msg)
                    }
                }

                override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                    updateStatus = UpdateProfileStatusUIState.Failed(t.localizedMessage ?: "Network error")
                }
            })
        }
    }

    fun resetUpdateStatus() {
        updateStatus = UpdateProfileStatusUIState.Start
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = (this[APPLICATION_KEY] as App)
                ProfileViewModel(
                    profileRepository = app.container.profileRepository,
                    userRepository = app.container.userRepository
                )
            }
        }
    }
}
