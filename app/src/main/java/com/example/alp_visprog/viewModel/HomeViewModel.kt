package com.example.alp_visprog.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.alp_visprog.App
import com.example.alp_visprog.models.HelpRequestsResponse
import com.example.alp_visprog.repositories.HelpRequestRepository
import com.example.alp_visprog.uiStates.HomeUIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel(
    private val helpRequestRepository: HelpRequestRepository
) : ViewModel() {

    // StateFlow to manage UI state
    private val _homeUIState = MutableStateFlow<HomeUIState>(HomeUIState.Loading)
    val homeUIState: StateFlow<HomeUIState> = _homeUIState.asStateFlow()

    // Fetch all help requests from backend
    fun loadHelpRequests() {
        _homeUIState.value = HomeUIState.Loading

        val call = helpRequestRepository.getAllHelpRequests()

        call.enqueue(object : Callback<HelpRequestsResponse> {
            override fun onResponse(
                call: Call<HelpRequestsResponse>,
                response: Response<HelpRequestsResponse>
            ) {
                if (response.isSuccessful) {
                    val helpRequests = response.body()?.data ?: emptyList()
                    _homeUIState.value = HomeUIState.Success(helpRequests)
                } else {
                    _homeUIState.value = HomeUIState.Error("Failed to load help requests")
                }
            }

            override fun onFailure(call: Call<HelpRequestsResponse>, t: Throwable) {
                _homeUIState.value = HomeUIState.Error(t.message ?: "Unknown Error")
            }
        })
    }

    // Filter help requests by type and status
    fun filterHelpRequests(type: String? = null, status: String? = null) {
        _homeUIState.value = HomeUIState.Loading

        val call = helpRequestRepository.filterHelpRequests(type, status)

        call.enqueue(object : Callback<HelpRequestsResponse> {
            override fun onResponse(
                call: Call<HelpRequestsResponse>,
                response: Response<HelpRequestsResponse>
            ) {
                if (response.isSuccessful) {
                    val helpRequests = response.body()?.data ?: emptyList()
                    _homeUIState.value = HomeUIState.Success(helpRequests)
                } else {
                    _homeUIState.value = HomeUIState.Error("Failed to filter help requests")
                }
            }

            override fun onFailure(call: Call<HelpRequestsResponse>, t: Throwable) {
                _homeUIState.value = HomeUIState.Error(t.message ?: "Unknown Error")
            }
        })
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as App)
                val helpRequestRepository = application.container.helpRequestRepository
                HomeViewModel(helpRequestRepository)
            }
        }
    }
}
