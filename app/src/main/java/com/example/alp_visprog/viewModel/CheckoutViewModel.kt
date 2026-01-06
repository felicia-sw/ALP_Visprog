package com.example.alp_visprog.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.alp_visprog.App
import com.example.alp_visprog.models.CheckoutRequest
import com.example.alp_visprog.models.GeneralResponse
import com.example.alp_visprog.repositories.ExchangeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// 1. UI State Definition
// WHY: We use a "sealed class" to represent the screen's status.
// This forces the UI to handle every possible scenario (Loading, Success, Error).
sealed class CheckoutUIState {
    object Idle : CheckoutUIState()      // Nothing happened yet
    object Loading : CheckoutUIState()   // Spinner is spinning
    object Success : CheckoutUIState()   // Done! Navigate away
    data class Error(val message: String) : CheckoutUIState() // Something broke
}

class CheckoutViewModel(
    private val exchangeRepository: ExchangeRepository
) : ViewModel() {

    // 2. Form State (User Input)
    // WHY: We use 'mutableStateOf' for text fields instead of StateFlow.
    // This allows immediate, two-way binding in Compose TextFields without complex collection logic.
    var name by mutableStateOf("")
    var phone by mutableStateOf("")
    var email by mutableStateOf("")
    var message by mutableStateOf("")

    // 3. Screen State (Status)
    // WHY: We use 'StateFlow' for the overall screen status.
    // Unlike text inputs, the status (Loading/Success) is reactive and should be observed safely.
    private val _uiState = MutableStateFlow<CheckoutUIState>(CheckoutUIState.Idle)
    val uiState: StateFlow<CheckoutUIState> = _uiState.asStateFlow()

    // 4. Validation
    // WHY: Never send bad data to the backend. Check it first to save network resources.
    private fun validateForm(): Boolean {
        return if (name.isBlank() || phone.isBlank() || message.isBlank()) {
            _uiState.value = CheckoutUIState.Error("Please fill in all required fields.")
            false
        } else {
            true
        }
    }

    // 5. Submit Logic
    fun submitCheckout() {
        if (!validateForm()) return

        _uiState.value = CheckoutUIState.Loading

        val request = CheckoutRequest(
            name = name,
            phone = phone,
            email = email,
            description = message
        )

        // WHY: We use .enqueue() to run the network call asynchronously.
        // If we ran this on the main thread, the app would freeze.
        exchangeRepository.checkout(request).enqueue(object : Callback<GeneralResponse> {
            override fun onResponse(call: Call<GeneralResponse>, response: Response<GeneralResponse>) {
                if (response.isSuccessful) {
                    _uiState.value = CheckoutUIState.Success
                } else {
                    _uiState.value = CheckoutUIState.Error("Failed: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<GeneralResponse>, t: Throwable) {
                _uiState.value = CheckoutUIState.Error("Network Error: ${t.localizedMessage}")
            }
        })
    }

    // 6. Factory Boilerplate
    // WHY: ViewModels cannot have arguments in their constructor by default.
    // This Factory tells Android how to inject the 'ExchangeRepository' when creating the ViewModel.
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as App)
                CheckoutViewModel(
                    exchangeRepository = app.container.exchangeRepository
                )
            }
        }
    }
}