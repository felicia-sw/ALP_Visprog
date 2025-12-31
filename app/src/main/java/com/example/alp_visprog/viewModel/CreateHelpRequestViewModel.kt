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
import android.util.Patterns

class CreateHelpRequestViewModel(
    private val helpRequestRepository: HelpRequestRepository,
    private val userRepository: UserRepositoryInterface
) : ViewModel() {

    // --- Form Inputs (Strictly matching DB) ---
    var categoryIdInput by mutableStateOf("1") // 1 = Barang, 2 = Jasa
    var nameOfProduct by mutableStateOf("")
    var description by mutableStateOf("")
    var exchangeProductName by mutableStateOf("") // "Mau ditukar dengan apa?"
    var location by mutableStateOf("")

    // --- Contact Fields ---
    var contactName by mutableStateOf("") // For UI display only (derived from User)
    var contactPhone by mutableStateOf("")
    var contactEmail by mutableStateOf("")

    // --- Image Handling ---
    var selectedImageUri by mutableStateOf<Uri?>(null)
    var imageUrl by mutableStateOf("") // For the single image URL string

    // UI State
    private val _dataStatus = MutableStateFlow<CreateExchangeUIState>(CreateExchangeUIState.Idle)
    val dataStatus: StateFlow<CreateExchangeUIState> = _dataStatus.asStateFlow()

    // Load profile data on init
    init {
        loadProfileData()
    }

    fun loadProfileData() {
        viewModelScope.launch {
            userRepository.currentUsername.collect { username ->
                if (username != "Unknown" && contactName.isEmpty()) contactName = username
            }
        }
        viewModelScope.launch {
            userRepository.currentUserEmail.collect { email ->
                if (email.isNotEmpty() && contactEmail.isEmpty()) contactEmail = email
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
        contactEmail = ""
        contactName = ""
        loadProfileData()
        selectedImageUri = null
    }

    fun submitHelpRequest() {
        // 1. Check Required Fields
        if (nameOfProduct.isBlank() || description.isBlank() || contactPhone.isBlank()) {
            _dataStatus.value = CreateExchangeUIState.Error("Please fill in required fields.")
            return
        }

        // 2. Check Email Validity (The Fix)
        // We only check if it is NOT empty. If it's empty, we allow it (optional).
        // If it has text, it MUST match the email pattern.
        if (contactEmail.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(contactEmail).matches()) {
            _dataStatus.value = CreateExchangeUIState.Error("Invalid email")
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
            userId = 1,
            contactPhone = contactPhone,
            contactEmail = contactEmail
        )

        requestCall.enqueue(object : Callback<CreateHelpRequestResponse> {
            override fun onResponse(call: Call<CreateHelpRequestResponse>, response: Response<CreateHelpRequestResponse>) {
                if (response.isSuccessful) {
                    _dataStatus.value = CreateExchangeUIState.Success
                } else {
                    // Fallback: If backend sends a specific error, show it
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
                    application.container.userRepository
                )
            }
        }
    }
}