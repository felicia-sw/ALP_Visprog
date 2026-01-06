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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.util.Patterns
import android.util.Base64
import android.util.Log
import org.json.JSONObject

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

    // Store current user ID
    private var currentUserId: Int = -1

    // Load profile data on init
    init {
        loadProfileData()
        loadCurrentUserId()
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

    private fun loadCurrentUserId() {
        viewModelScope.launch {
            try {
                val token = userRepository.currentUserToken.first()
                Log.d("CreateHelpRequest", "🔑 Token: ${if (token == "Unknown") "Unknown" else token.take(30)}...")

                if (token != "Unknown" && token.isNotBlank()) {
                    // Decode JWT token to get user ID
                    val parts = token.split(".")
                    Log.d("CreateHelpRequest", "📦 Token parts: ${parts.size}")

                    if (parts.size == 3) {
                        try {
                            val payload = String(Base64.decode(parts[1], Base64.URL_SAFE))
                            Log.d("CreateHelpRequest", "📄 Payload: $payload")

                            val json = JSONObject(payload)
                            currentUserId = json.getInt("userId")
                            Log.d("CreateHelpRequest", "✅ Current user ID: $currentUserId")
                        } catch (e: Exception) {
                            Log.e("CreateHelpRequest", "❌ Error decoding payload", e)
                            // Try alternative key names
                            try {
                                val payload = String(Base64.decode(parts[1], Base64.URL_SAFE))
                                val json = JSONObject(payload)
                                // Try different possible keys
                                currentUserId = when {
                                    json.has("userId") -> json.getInt("userId")
                                    json.has("id") -> json.getInt("id")
                                    json.has("sub") -> json.getString("sub").toIntOrNull() ?: -1
                                    else -> -1
                                }
                                Log.d("CreateHelpRequest", "✅ User ID from alternative key: $currentUserId")
                            } catch (e2: Exception) {
                                Log.e("CreateHelpRequest", "❌ Failed with alternative keys", e2)
                                currentUserId = -1
                            }
                        }
                    } else {
                        Log.e("CreateHelpRequest", "❌ Invalid token format - expected 3 parts, got ${parts.size}")
                        currentUserId = -1
                    }
                } else {
                    Log.e("CreateHelpRequest", "❌ Token is Unknown or blank")
                    currentUserId = -1
                }
            } catch (e: Exception) {
                Log.e("CreateHelpRequest", "❌ Error getting user ID", e)
                currentUserId = -1
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
            _dataStatus.value = CreateExchangeUIState.Error("Mohon isi semua field yang diperlukan.")
            return
        }

        // 2. Check Email Validity
        if (contactEmail.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(contactEmail).matches()) {
            _dataStatus.value = CreateExchangeUIState.Error("Email tidak valid")
            return
        }

        _dataStatus.value = CreateExchangeUIState.Loading

        // Get token and create request
        viewModelScope.launch {
            try {
                val token = userRepository.currentUserToken.first()

                if (token == "Unknown" || token.isBlank()) {
                    _dataStatus.value = CreateExchangeUIState.Error("Sesi tidak ditemukan. Silakan login kembali.")
                    return@launch
                }

                val bearerToken = "Bearer $token"
                val finalImageUrl = selectedImageUri?.toString() ?: ""

                Log.d("CreateHelpRequest", "🚀 Membuat help request")
                Log.d("CreateHelpRequest", "📝 Name: $nameOfProduct")
                Log.d("CreateHelpRequest", "📝 Category: $categoryIdInput")
                Log.d("CreateHelpRequest", "📝 Location: $location")
                Log.d("CreateHelpRequest", "📝 User ID: $currentUserId")

                val requestCall = helpRequestRepository.createHelpRequest(
                    bearerToken = bearerToken,
                    nameOfProduct = nameOfProduct,
                    description = description,
                    exchangeProductName = exchangeProductName,
                    location = location,
                    imageUrl = finalImageUrl,
                    categoryId = categoryIdInput.toIntOrNull() ?: 1,
                    userId = if (currentUserId != -1) currentUserId else 1,
                    contactPhone = contactPhone,
                    contactEmail = contactEmail
                )

                requestCall.enqueue(object : Callback<CreateHelpRequestResponse> {
                    override fun onResponse(call: Call<CreateHelpRequestResponse>, response: Response<CreateHelpRequestResponse>) {
                        if (response.isSuccessful) {
                            Log.d("CreateHelpRequest", "✅ Help request created successfully!")
                            _dataStatus.value = CreateExchangeUIState.Success
                        } else {
                            val errorBody = response.errorBody()?.string()
                            val errorMessage = when (response.code()) {
                                404 -> "Endpoint tidak ditemukan. Silakan periksa server Anda."
                                401 -> "Tidak terautentikasi. Silakan login kembali."
                                400 -> "Data permintaan tidak valid: $errorBody"
                                500 -> "Kesalahan server: $errorBody"
                                else -> "${response.code()}: ${response.message()}"
                            }
                            Log.e("CreateHelpRequest", "❌ Failed: ${response.code()} - $errorBody")
                            Log.e("CreateHelpRequest", "❌ Request URL: ${call.request().url}")
                            Log.e("CreateHelpRequest", "❌ Request Body: ${call.request().body}")
                            _dataStatus.value = CreateExchangeUIState.Error(errorMessage)
                        }
                    }

                    override fun onFailure(call: Call<CreateHelpRequestResponse>, t: Throwable) {
                        Log.e("CreateHelpRequest", "❌ Network error: ${t.message}")
                        Log.e("CreateHelpRequest", "❌ Stack trace: ", t)
                        _dataStatus.value = CreateExchangeUIState.Error("Network error: ${t.localizedMessage}")
                    }
                })
            } catch (e: Exception) {
                Log.e("CreateHelpRequest", "❌ Exception in submit", e)
                _dataStatus.value = CreateExchangeUIState.Error("Error: ${e.message}")
            }
        }
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