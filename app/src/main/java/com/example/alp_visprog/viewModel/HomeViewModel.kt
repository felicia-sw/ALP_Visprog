package com.example.alp_visprog.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.alp_visprog.App
import com.example.alp_visprog.models.GetAllHelpRequestsResponse as AllHelpResp
import com.example.alp_visprog.repositories.HelpRequestRepository
import com.example.alp_visprog.uiStates.HomeUIState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel(
    private val helpRequestRepository: HelpRequestRepository
) : ViewModel() {

    companion object {
        private const val TAG = "HomeViewModel"
        private const val TIMEOUT_MS = 10000L // 10 seconds timeout

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as App)
                val helpRequestRepository = application.container.helpRequestRepository
                HomeViewModel(helpRequestRepository)
            }
        }
    }

    // StateFlow to manage UI state
    private val _homeUIState = MutableStateFlow<HomeUIState>(HomeUIState.Loading)
    val homeUIState: StateFlow<HomeUIState> = _homeUIState.asStateFlow()

    private var isRequestInProgress = false

    // Fetch all help requests from backend
    fun loadHelpRequests() {
        if (isRequestInProgress) {
            Log.d(TAG, "loadHelpRequests: Request already in progress, skipping")
            return
        }

        Log.d(TAG, "loadHelpRequests: Starting to load help requests")
        _homeUIState.value = HomeUIState.Loading
        isRequestInProgress = true

        // Add a timeout to prevent infinite loading
        viewModelScope.launch {
            delay(TIMEOUT_MS)
            if (_homeUIState.value is HomeUIState.Loading && isRequestInProgress) {
                Log.e(TAG, "loadHelpRequests: Request timed out after ${TIMEOUT_MS}ms")
                _homeUIState.value = HomeUIState.Error(
                    "Request timed out. Please check if the backend server is running on http://10.0.2.2:3000/"
                )
                isRequestInProgress = false
            }
        }

        try {
            val call = helpRequestRepository.getAllHelpRequests()
            Log.d(TAG, "loadHelpRequests: API call created, enqueueing...")

            call.enqueue(object : Callback<AllHelpResp> {
                override fun onResponse(p0: Call<AllHelpResp>, p1: Response<AllHelpResp>) {
                    isRequestInProgress = false
                    val response = p1
                    Log.d(TAG, "onResponse: Received response with code ${response.code()}")

                    if (response.isSuccessful) {
                        val helpRequests = response.body()?.data ?: emptyList()
                        Log.d(TAG, "onResponse: Success! Loaded ${helpRequests.size} help requests")
                        _homeUIState.value = HomeUIState.Success(helpRequests)
                    } else {
                        val errorMsg = "Failed to load help requests (HTTP ${response.code()})"
                        Log.e(TAG, "onResponse: $errorMsg")
                        try {
                            Log.e(TAG, "Error body: ${response.errorBody()?.string()}")
                        } catch (e: Exception) {
                            Log.e(TAG, "Could not read error body", e)
                        }
                        _homeUIState.value = HomeUIState.Error(errorMsg)
                    }
                }

                override fun onFailure(p0: Call<AllHelpResp>, p1: Throwable) {
                    isRequestInProgress = false
                    val errorMsg = p1.message ?: "Unknown Error"
                    Log.e(TAG, "onFailure: Network call failed - $errorMsg", p1)
                    _homeUIState.value = HomeUIState.Error(
                        "Cannot connect to server. Make sure the backend is running at http://10.0.2.2:3000/\n\nError: $errorMsg"
                    )
                }
            })
        } catch (e: Exception) {
            isRequestInProgress = false
            Log.e(TAG, "loadHelpRequests: Exception caught", e)
            _homeUIState.value = HomeUIState.Error("Error: ${e.message}")
        }
    }

    @Suppress("UNUSED_PARAMETER")
    // Filter help requests by categoryId - type parameter should be a category ID number
    fun filterHelpRequests(type: String? = null, status: String? = null) {
        Log.d(TAG, "filterHelpRequests: Filtering by type=$type, status=$status")
        _homeUIState.value = HomeUIState.Loading

        try {
            // Repository doesn't have a dedicated filter endpoint; fetch all and filter locally by categoryId
            val call = helpRequestRepository.getAllHelpRequests()

            call.enqueue(object : Callback<AllHelpResp> {
                override fun onResponse(p0: Call<AllHelpResp>, p1: Response<AllHelpResp>) {
                    val response = p1
                    Log.d(TAG, "filterHelpRequests onResponse: code ${response.code()}")

                    if (response.isSuccessful) {
                        val all = response.body()?.data ?: emptyList()
                        val filtered = if (type.isNullOrBlank()) {
                            all
                        } else {
                            // Try to parse type as categoryId integer, otherwise filter by product name
                            val categoryId = type.toIntOrNull()
                            if (categoryId != null) {
                                all.filter { it.categoryId == categoryId }
                            } else {
                                // Fallback: search in product name
                                all.filter {
                                    it.nameOfProduct.contains(type, ignoreCase = true) ||
                                    it.exchangeProductName.contains(type, ignoreCase = true)
                                }
                            }
                        }
                        Log.d(TAG, "filterHelpRequests: Filtered ${filtered.size} from ${all.size} total")
                        _homeUIState.value = HomeUIState.Success(filtered)
                    } else {
                        val errorMsg = "Failed to filter help requests (HTTP ${response.code()})"
                        Log.e(TAG, errorMsg)
                        _homeUIState.value = HomeUIState.Error(errorMsg)
                    }
                }

                override fun onFailure(p0: Call<AllHelpResp>, p1: Throwable) {
                    val errorMsg = p1.message ?: "Unknown Error"
                    Log.e(TAG, "filterHelpRequests onFailure: $errorMsg", p1)
                    _homeUIState.value = HomeUIState.Error("Network error: $errorMsg")
                }
            })
        } catch (e: Exception) {
            Log.e(TAG, "filterHelpRequests: Exception caught", e)
            _homeUIState.value = HomeUIState.Error("Error: ${e.message}")
        }
    }
}
