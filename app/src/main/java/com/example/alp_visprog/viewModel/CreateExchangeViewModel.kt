package com.example.alp_visprog.viewModel

import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.alp_visprog.App
import com.example.alp_visprog.models.GeneralResponse
import com.example.alp_visprog.repositories.ExchangeRepository
import com.example.alp_visprog.uiStates.CreateExchangeUIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CreateExchangeViewModel(
    private val exchangeRepository: ExchangeRepository
) : ViewModel() {

    // 1. Form Data (We keep these as mutableStateOf for simple text input binding)
    var name by mutableStateOf("")
    var phone by mutableStateOf("")
    var email by mutableStateOf("")
    var description by mutableStateOf("")

    // 2. The Form State (Refactored to StateFlow)
    private val _dataStatus = MutableStateFlow<CreateExchangeUIState>(CreateExchangeUIState.Idle)
    val dataStatus: StateFlow<CreateExchangeUIState> = _dataStatus.asStateFlow()

    // 3. Reset State & Error Clearing
    fun clearErrorMessage() {
        _dataStatus.value = CreateExchangeUIState.Idle
    }

    fun resetForm() {
        _dataStatus.value = CreateExchangeUIState.Idle
        name = ""
        phone = ""
        email = ""
        description = ""
    }

    // 4. The Main Submit Function
    fun submitOffer(helpRequestId: Int) {
        // 1. Basic Empty Check
        if (name.isBlank() || phone.isBlank() || description.isBlank()) {
            _dataStatus.value = CreateExchangeUIState.Error("Please fill in all required fields.")
            return
        }

        // 2. Email Format Check (NEW)
        // We only check if the user actually typed something (isNotBlank)
        if (email.isNotBlank() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _dataStatus.value = CreateExchangeUIState.Error("Invalid email format. Please check again.")
            return
        }

        // If we pass checks, set loading and call API
        _dataStatus.value = CreateExchangeUIState.Loading

        val call = exchangeRepository.createExchange(
            name = name,
            phone = phone,
            email = email,
            description = description,
            helpRequestId = helpRequestId
        )

        // ... (rest of the network call code remains the same) ...
        call.enqueue(object : Callback<GeneralResponse> {
            override fun onResponse(call: Call<GeneralResponse>, response: Response<GeneralResponse>) {
                if (response.isSuccessful) {
                    _dataStatus.value = CreateExchangeUIState.Success
                } else {
                    _dataStatus.value = CreateExchangeUIState.Error("Failed to submit offer.")
                }
            }

            override fun onFailure(call: Call<GeneralResponse>, t: Throwable) {
                _dataStatus.value = CreateExchangeUIState.Error(t.message ?: "Unknown Error")
            }
        })
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as App)
                val exchangeRepository = application.container.exchangeRepository
                CreateExchangeViewModel(exchangeRepository)
            }
        }
    }
}