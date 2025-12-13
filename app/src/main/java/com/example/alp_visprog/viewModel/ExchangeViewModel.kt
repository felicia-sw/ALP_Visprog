package com.example.alp_visprog.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.alp_visprog.App
import com.example.alp_visprog.models.GeneralResponse
import com.example.alp_visprog.models.GetAllExchangesResponse
import com.example.alp_visprog.repositories.ExchangeRepository
import com.example.alp_visprog.uiStates.ExchangeUIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ExchangeViewModel(
    private val exchangeRepository: ExchangeRepository
) : ViewModel() {

    // CHANGE 1: Use StateFlow instead of mutableStateOf
    private val _exchangeUIState = MutableStateFlow<ExchangeUIState>(ExchangeUIState.Loading)
    val exchangeUIState: StateFlow<ExchangeUIState> = _exchangeUIState.asStateFlow()

    // FUNCTION 1: Load the offers
    fun getExchangeOffers(helpRequestId: Int) {
        _exchangeUIState.value = ExchangeUIState.Loading

        val call = exchangeRepository.getExchangeOffers(helpRequestId)

        call.enqueue(object : Callback<GetAllExchangesResponse> {
            override fun onResponse(
                call: Call<GetAllExchangesResponse>,
                response: Response<GetAllExchangesResponse>
            ) {
                if (response.isSuccessful) {
                    val exchanges = response.body()?.data ?: emptyList()
                    _exchangeUIState.value = ExchangeUIState.Success(exchanges)
                } else {
                    _exchangeUIState.value = ExchangeUIState.Error("Failed to load offers")
                }
            }

            override fun onFailure(call: Call<GetAllExchangesResponse>, t: Throwable) {
                _exchangeUIState.value = ExchangeUIState.Error(t.message ?: "Unknown Error")
            }
        })
    }

    // FUNCTION 2: Delete an offer
    fun deleteExchange(exchangeId: Int, currentHelpRequestId: Int) {
        // Optional: We could set a "Deleting..." state here if we wanted

        val call = exchangeRepository.deleteExchange(exchangeId)

        call.enqueue(object : Callback<GeneralResponse> {
            override fun onResponse(call: Call<GeneralResponse>, response: Response<GeneralResponse>) {
                if (response.isSuccessful) {
                    // Reload the list to refresh the UI
                    getExchangeOffers(currentHelpRequestId)
                }
            }

            override fun onFailure(call: Call<GeneralResponse>, t: Throwable) {
                // Ideally, show a toast error here (via a separate EventFlow)
            }
        })
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as App)
                val exchangeRepository = application.container.exchangeRepository
                ExchangeViewModel(exchangeRepository)
            }
        }
    }
}