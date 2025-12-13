package com.example.alp_visprog.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.alp_visprog.Application
import com.example.alp_visprog.models.GeneralResponse
import com.example.alp_visprog.models.GetAllExchangesResponse
import com.example.alp_visprog.repositories.ExchangeRepository
import com.example.alp_visprog.uiStates.ExchangeUIState
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ExchangeViewModel(
    private val exchangeRepository: ExchangeRepository
) : ViewModel() {

    // The current state of the screen (Loading, Success, or Error)
    var exchangeUIState: ExchangeUIState by mutableStateOf(ExchangeUIState.Loading)
        private set

    // FUNCTION 1: Load the offers
    fun getExchangeOffers(helpRequestId: Int) {
        exchangeUIState = ExchangeUIState.Loading

        val call = exchangeRepository.getExchangeOffers(helpRequestId)

        call.enqueue(object : Callback<GetAllExchangesResponse> {
            override fun onResponse(
                call: Call<GetAllExchangesResponse>,
                response: Response<GetAllExchangesResponse>
            ) {
                if (response.isSuccessful) {
                    // Success! Update UI with the data
                    val exchanges = response.body()?.data ?: emptyList()
                    exchangeUIState = ExchangeUIState.Success(exchanges)
                } else {
                    // Server returned an error (e.g. 404)
                    exchangeUIState = ExchangeUIState.Error("Failed to load offers")
                }
            }

            override fun onFailure(call: Call<GetAllExchangesResponse>, t: Throwable) {
                // Network error (e.g. no internet)
                exchangeUIState = ExchangeUIState.Error(t.message ?: "Unknown Error")
            }
        })
    }

    // FUNCTION 2: Delete an offer
    fun deleteExchange(exchangeId: Int, currentHelpRequestId: Int) {
        val call = exchangeRepository.deleteExchange(exchangeId)

        call.enqueue(object : Callback<GeneralResponse> {
            override fun onResponse(call: Call<GeneralResponse>, response: Response<GeneralResponse>) {
                if (response.isSuccessful) {
                    // If delete worked, RELOAD the list to show it's gone
                    getExchangeOffers(currentHelpRequestId)
                }
            }

            override fun onFailure(call: Call<GeneralResponse>, t: Throwable) {
                // Handle error if needed
            }
        })
    }

    // This factory helper allows the App to create this ViewModel easily
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application)
                val exchangeRepository = application.container.exchangeRepository
                ExchangeViewModel(exchangeRepository)
            }
        }
    }
}