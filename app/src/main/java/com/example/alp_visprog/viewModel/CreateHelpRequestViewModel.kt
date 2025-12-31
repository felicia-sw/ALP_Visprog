package com.example.alp_visprog.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.alp_visprog.App
import com.example.alp_visprog.models.CreateHelpRequestResponse
import com.example.alp_visprog.repositories.HelpRequestRepository
import com.example.alp_visprog.uiStates.CreateExchangeUIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CreateHelpRequestViewModel(
    private val helpRequestRepository: HelpRequestRepository
) : ViewModel() {

    // Form Data
    var nameOfProduct by mutableStateOf("")
    var description by mutableStateOf("")
    var exchangeProductName by mutableStateOf("")
    var location by mutableStateOf("")
    var imageUrl by mutableStateOf("")
    var categoryIdInput by mutableStateOf("") // Keeping as String for input handling

    // UI State
    private val _dataStatus = MutableStateFlow<CreateExchangeUIState>(CreateExchangeUIState.Idle)
    val dataStatus: StateFlow<CreateExchangeUIState> = _dataStatus.asStateFlow()

    fun clearErrorMessage() {
        _dataStatus.value = CreateExchangeUIState.Idle
    }

    fun resetForm() {
        _dataStatus.value = CreateExchangeUIState.Idle
        nameOfProduct = ""
        description = ""
        exchangeProductName = ""
        location = ""
        imageUrl = ""
        categoryIdInput = ""
    }

    fun submitHelpRequest() {
        // 1. Validate Basic Fields
        if (nameOfProduct.isBlank() || description.isBlank() || location.isBlank() || exchangeProductName.isBlank()) {
            _dataStatus.value = CreateExchangeUIState.Error("Please fill in all required fields.")
            return
        }

        // 2. Validate Category ID
        val catId = categoryIdInput.toIntOrNull()
        if (catId == null) {
            _dataStatus.value = CreateExchangeUIState.Error("Category ID must be a valid number.")
            return
        }

        _dataStatus.value = CreateExchangeUIState.Loading

        // TODO: Replace '1' with actual logged-in User ID from DataStore/UserContext
        val currentUserId = 1

        val call = helpRequestRepository.createHelpRequest(
            nameOfProduct = nameOfProduct,
            description = description,
            exchangeProductName = exchangeProductName,
            location = location,
            imageUrl = imageUrl,
            categoryId = catId,
            userId = currentUserId
        )

        call.enqueue(object : Callback<CreateHelpRequestResponse> {
            override fun onResponse(call: Call<CreateHelpRequestResponse>, response: Response<CreateHelpRequestResponse>) {
                if (response.isSuccessful) {
                    _dataStatus.value = CreateExchangeUIState.Success
                } else {
                    _dataStatus.value = CreateExchangeUIState.Error("Failed: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<CreateHelpRequestResponse>, t: Throwable) {
                _dataStatus.value = CreateExchangeUIState.Error(t.message ?: "Unknown Error")
            }
        })
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as App)
                CreateHelpRequestViewModel(
                    application.container.helpRequestRepository
                )
            }
        }
    }
}