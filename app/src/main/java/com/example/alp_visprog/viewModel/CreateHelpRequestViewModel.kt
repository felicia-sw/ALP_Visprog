package com.example.alp_visprog.viewModel

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.alp_visprog.App
import com.example.alp_visprog.models.CreateHelpRequestResponse
import com.example.alp_visprog.repositories.HelpRequestRepository
import com.example.alp_visprog.repositories.UserRepositoryInterface
import com.example.alp_visprog.uiStates.CreateExchangeUIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CreateHelpRequestViewModel(
    private val helpRequestRepository: HelpRequestRepository,
    private val userRepository: UserRepositoryInterface // [1] Injected Dependency
) : ViewModel() {

    // --- Form Inputs ---
    var nameOfProduct by mutableStateOf("")
    var description by mutableStateOf("")
    var exchangeProductName by mutableStateOf("")
    var location by mutableStateOf("")

    // Contact Fields
    var contactPhone by mutableStateOf("")
    var contactEmail by mutableStateOf("")

    // Image Handling
    var selectedImageUri by mutableStateOf<Uri?>(null)
    var imageUrl by mutableStateOf("")

    // Category (Default to 1 = Barang)
    var categoryIdInput by mutableStateOf("1")

    // UI State
    private val _dataStatus = MutableStateFlow<CreateExchangeUIState>(CreateExchangeUIState.Idle)
    val dataStatus: StateFlow<CreateExchangeUIState> = _dataStatus.asStateFlow()

    // [2] Init Block: Load User Profile Data automatically when ViewModel is created
    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            // [3] Collect the email flow from Repository
            userRepository.currentUserEmail.collect { email ->
                // Only pre-fill if field is empty and we have a valid email
                if (contactEmail.isEmpty() && email.isNotEmpty() && email != "Unknown") {
                    contactEmail = email
                }
            }
        }
    }

    fun clearErrorMessage() {
        _dataStatus.value = CreateExchangeUIState.Idle
    }

    fun resetForm() {
        _dataStatus.value = CreateExchangeUIState.Idle
        nameOfProduct = ""
        description = ""
        exchangeProductName = ""
        location = ""
        contactPhone = ""
        // We clear email, but re-loading profile will pre-fill it again if saved
        contactEmail = ""
        selectedImageUri = null
        imageUrl = ""

        loadUserProfile() // Re-fetch default data
    }

    fun submitHelpRequest() {
        if (nameOfProduct.isBlank() || description.isBlank() || contactPhone.isBlank()) {
            _dataStatus.value = CreateExchangeUIState.Error("Please fill in required fields (Name, Desc, Phone).")
            return
        }

        _dataStatus.value = CreateExchangeUIState.Loading

        val finalImageUrl = selectedImageUri?.toString() ?: ""

        val requestCall = helpRequestRepository.createHelpRequest(
            nameOfProduct = nameOfProduct,
            description = description,
            exchangeProductName = exchangeProductName,
            location = location,
            imageUrl = finalImageUrl,
            categoryId = categoryIdInput.toIntOrNull() ?: 1,
            userId = 1, // TODO: Replace with actual User ID if available
            contactPhone = contactPhone,
            contactEmail = contactEmail
        )

        requestCall.enqueue(object : Callback<CreateHelpRequestResponse> {
            override fun onResponse(call: Call<CreateHelpRequestResponse>, response: Response<CreateHelpRequestResponse>) {
                if (response.isSuccessful) {
                    _dataStatus.value = CreateExchangeUIState.Success
                } else {
                    _dataStatus.value = CreateExchangeUIState.Error("Failed: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<CreateHelpRequestResponse>, t: Throwable) {
                _dataStatus.value = CreateExchangeUIState.Error("Error: ${t.localizedMessage}")
            }
        })
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as App)
                CreateHelpRequestViewModel(
                    application.container.helpRequestRepository,
                    application.container.userRepository // [4] Pass UserRepository here
                )
            }
        }
    }
}