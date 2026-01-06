package com.example.alp_visprog.viewModel

import android.util.Log
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
import com.example.alp_visprog.models.GetAllHelpRequestsResponse
import com.example.alp_visprog.models.HelpRequestModel
import com.example.alp_visprog.models.ProfileResponse
import com.example.alp_visprog.models.UpdateProfileRequest
import com.example.alp_visprog.repositories.HelpRequestRepository
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

class ProfileViewModel(
    private val profileRepository: ProfileRepositoryInterface,
    private val userRepository: UserRepositoryInterface,
    private val helpRequestRepository: HelpRequestRepository
) : ViewModel() {

    companion object {
        private const val TAG = "ProfileViewModel"

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = (this[APPLICATION_KEY] as App)
                ProfileViewModel(
                    profileRepository = app.container.profileRepository,
                    userRepository = app.container.userRepository,
                    helpRequestRepository = app.container.helpRequestRepository
                )
            }
        }
    }

    var profileStatus: ProfileStatusUIState by mutableStateOf(ProfileStatusUIState.Start)
        private set

    var updateStatus: UpdateProfileStatusUIState by mutableStateOf(UpdateProfileStatusUIState.Start)
        private set

    var userHelpRequests by mutableStateOf<List<HelpRequestModel>>(emptyList())
        private set

    var isLoadingHelpRequests by mutableStateOf(false)
        private set

    var totalTawaran by mutableStateOf(0)
        private set

    var totalBertukar by mutableStateOf(0)
        private set

    var totalProses by mutableStateOf(0)
        private set

    fun fetchProfile() {
        viewModelScope.launch {
            try {
                profileStatus = ProfileStatusUIState.Loading
                Log.d(TAG, "🔄 Starting to fetch profile...")

                val token = userRepository.currentUserToken.first()
                Log.d(TAG, "🔑 Token retrieved: ${if (token == "Unknown") "Unknown" else "Valid (${token.take(20)}...)"}")

                if (token == "Unknown" || token.isBlank()) {
                    profileStatus = ProfileStatusUIState.Failed("Token belum ada. Silakan login dulu.")
                    Log.e(TAG, "❌ No valid token available")
                    return@launch
                }

                val bearer = "Bearer $token"

                profileRepository.viewProfile(bearer).enqueue(object : Callback<ProfileResponse> {
                    override fun onResponse(call: Call<ProfileResponse>, res: Response<ProfileResponse>) {
                        try {
                            Log.d(TAG, "📥 Profile response received - Code: ${res.code()}")

                            if (res.isSuccessful) {
                                val responseBody = res.body()
                                if (responseBody != null && responseBody.data != null) {
                                    Log.d(TAG, "✅ Profile loaded successfully: ${responseBody.data.fullName}")
                                    profileStatus = ProfileStatusUIState.Success(responseBody.data)
                                    fetchUserHelpRequests()
                                } else {
                                    Log.e(TAG, "❌ Response body or data is null")
                                    profileStatus = ProfileStatusUIState.Failed("Data profil tidak ditemukan")
                                }
                            } else {
                                val msg = try {
                                    val err = Gson().fromJson(res.errorBody()?.charStream(), ErrorModel::class.java)
                                    err?.errors ?: "Gagal memuat profile (${res.code()})"
                                } catch (e: Exception) {
                                    Log.e(TAG, "❌ Error parsing error response", e)
                                    "Gagal memuat profile (${res.code()})"
                                }
                                Log.e(TAG, "❌ API error: $msg")
                                profileStatus = ProfileStatusUIState.Failed(msg)
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "💥 Exception in onResponse", e)
                            profileStatus = ProfileStatusUIState.Failed("Error: ${e.message}")
                        }
                    }

                    override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                        Log.e(TAG, "💥 Network failure", t)
                        profileStatus = ProfileStatusUIState.Failed(t.localizedMessage ?: "Network error")
                    }
                })
            } catch (e: Exception) {
                Log.e(TAG, "💥 Exception in fetchProfile", e)
                profileStatus = ProfileStatusUIState.Failed("Error: ${e.message}")
            }
        }
    }

    fun fetchUserHelpRequests() {
        viewModelScope.launch {
            try {
                isLoadingHelpRequests = true
                Log.d(TAG, "🔄 Starting to fetch user help requests...")

                val token = userRepository.currentUserToken.first()
                if (token == "Unknown" || token.isBlank()) {
                    isLoadingHelpRequests = false
                    Log.e(TAG, "❌ No token available for help requests")
                    totalTawaran = 0
                    totalBertukar = 0
                    totalProses = 0
                    return@launch
                }

                val bearer = "Bearer $token"
                Log.d(TAG, "🔑 Token: ${token.take(20)}...")

                helpRequestRepository.getUserHelpRequests(bearer).enqueue(object : Callback<GetAllHelpRequestsResponse> {
                    override fun onResponse(
                        call: Call<GetAllHelpRequestsResponse>,
                        res: Response<GetAllHelpRequestsResponse>
                    ) {
                        try {
                            isLoadingHelpRequests = false
                            Log.d(TAG, "📥 Help requests response - Code: ${res.code()}")

                            if (res.isSuccessful) {
                                val responseBody = res.body()
                                if (responseBody != null && responseBody.data != null) {
                                    userHelpRequests = responseBody.data
                                    Log.d(TAG, "✅ Success! Found ${userHelpRequests.size} help requests")
                                    calculateStatistics()
                                    Log.d(TAG, "📊 Stats - Tawaran: $totalTawaran, Bertukar: $totalBertukar, Proses: $totalProses")
                                } else {
                                    Log.e(TAG, "❌ Response body or data is null")
                                    userHelpRequests = emptyList()
                                    resetStatistics()
                                }
                            } else {
                                Log.e(TAG, "❌ API call failed - Code: ${res.code()}")
                                try {
                                    val errorBody = res.errorBody()?.string()
                                    Log.e(TAG, "Error body: $errorBody")
                                } catch (e: Exception) {
                                    Log.e(TAG, "Could not read error body", e)
                                }
                                userHelpRequests = emptyList()
                                resetStatistics()
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "💥 Exception in help requests onResponse", e)
                            isLoadingHelpRequests = false
                            userHelpRequests = emptyList()
                            resetStatistics()
                        }
                    }

                    override fun onFailure(call: Call<GetAllHelpRequestsResponse>, t: Throwable) {
                        isLoadingHelpRequests = false
                        Log.e(TAG, "💥 Network error in help requests", t)
                        userHelpRequests = emptyList()
                        resetStatistics()
                    }
                })
            } catch (e: Exception) {
                Log.e(TAG, "💥 Exception in fetchUserHelpRequests", e)
                isLoadingHelpRequests = false
                userHelpRequests = emptyList()
                resetStatistics()
            }
        }
    }

    private fun calculateStatistics() {
        try {
            totalTawaran = userHelpRequests.size
            totalBertukar = userHelpRequests.count { it.isCheckout }
            totalProses = userHelpRequests.count { !it.isCheckout }
            Log.d(TAG, "✅ Statistics calculated successfully")
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error calculating statistics", e)
            resetStatistics()
        }
    }

    private fun resetStatistics() {
        totalTawaran = 0
        totalBertukar = 0
        totalProses = 0
    }

    fun updateProfile(fullName: String, location: String, bio: String?) {
        viewModelScope.launch {
            try {
                updateStatus = UpdateProfileStatusUIState.Loading
                Log.d(TAG, "🔄 Starting profile update...")

                val token = userRepository.currentUserToken.first()
                if (token == "Unknown" || token.isBlank()) {
                    updateStatus = UpdateProfileStatusUIState.Failed("Token belum ada. Silakan login dulu.")
                    Log.e(TAG, "❌ No token available for update")
                    return@launch
                }

                val bearer = "Bearer $token"
                val req = UpdateProfileRequest(fullName = fullName, location = location, bio = bio)

                profileRepository.updateProfile(bearer, req).enqueue(object : Callback<ProfileResponse> {
                    override fun onResponse(call: Call<ProfileResponse>, res: Response<ProfileResponse>) {
                        try {
                            Log.d(TAG, "📥 Update response - Code: ${res.code()}")

                            if (res.isSuccessful) {
                                val responseBody = res.body()
                                if (responseBody != null && responseBody.data != null) {
                                    val profile = responseBody.data
                                    Log.d(TAG, "✅ Profile updated successfully")
                                    updateStatus = UpdateProfileStatusUIState.Success(profile)
                                    profileStatus = ProfileStatusUIState.Success(profile)
                                } else {
                                    Log.e(TAG, "❌ Update response body is null")
                                    updateStatus = UpdateProfileStatusUIState.Failed("Gagal update profile")
                                }
                            } else {
                                val msg = try {
                                    val err = Gson().fromJson(res.errorBody()?.charStream(), ErrorModel::class.java)
                                    err?.errors ?: "Gagal update profile (${res.code()})"
                                } catch (e: Exception) {
                                    Log.e(TAG, "❌ Error parsing update error", e)
                                    "Gagal update profile (${res.code()})"
                                }
                                Log.e(TAG, "❌ Update error: $msg")
                                updateStatus = UpdateProfileStatusUIState.Failed(msg)
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "💥 Exception in update onResponse", e)
                            updateStatus = UpdateProfileStatusUIState.Failed("Error: ${e.message}")
                        }
                    }

                    override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                        Log.e(TAG, "💥 Network failure in update", t)
                        updateStatus = UpdateProfileStatusUIState.Failed(t.localizedMessage ?: "Network error")
                    }
                })
            } catch (e: Exception) {
                Log.e(TAG, "💥 Exception in updateProfile", e)
                updateStatus = UpdateProfileStatusUIState.Failed("Error: ${e.message}")
            }
        }
    }

    fun resetUpdateStatus() {
        updateStatus = UpdateProfileStatusUIState.Start
    }

    fun logout() {
        viewModelScope.launch {
            try {
                Log.d(TAG, "🚪 Logging out...")
                userRepository.saveUserToken("")
                userRepository.saveUsername("")
                userRepository.saveUserEmail("")
                Log.d(TAG, "✅ Logout successful")
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error during logout", e)
            }
        }
    }
}