package com.example.alp_visprog.viewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.auth0.android.jwt.JWT
import com.example.alp_visprog.App
import com.example.alp_visprog.models.GetAllHelpRequestsResponse as AllHelpResp
import com.example.alp_visprog.models.ProfileResponse
import com.example.alp_visprog.models.ShoppingCartResponse
import com.example.alp_visprog.repositories.HelpRequestRepository
import com.example.alp_visprog.repositories.ProfileRepositoryInterface
import com.example.alp_visprog.repositories.ShoppingCartRepository
import com.example.alp_visprog.repositories.UserRepositoryInterface
import com.example.alp_visprog.uiStates.HomeUIState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel(
    private val helpRequestRepository: HelpRequestRepository,
    private val profileRepository: ProfileRepositoryInterface,
    private val userRepository: UserRepositoryInterface,
    private val shoppingCartRepository: ShoppingCartRepository
) : ViewModel() {

    companion object {
        private const val TAG = "HomeViewModel"
        private const val TIMEOUT_MS = 10000L

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as App)
                HomeViewModel(
                    helpRequestRepository = application.container.helpRequestRepository,
                    profileRepository = application.container.profileRepository,
                    userRepository = application.container.userRepository,
                    shoppingCartRepository = application.container.shoppingCartRepository
                )
            }
        }
    }

    private val _homeUIState = MutableStateFlow<HomeUIState>(HomeUIState.Loading)
    val homeUIState: StateFlow<HomeUIState> = _homeUIState.asStateFlow()

    var userLocation by mutableStateOf("Loading...")
        private set

    // NEW: Cart status message for user feedback
    private val _cartStatusMessage = MutableStateFlow<String?>(null)
    val cartStatusMessage: StateFlow<String?> = _cartStatusMessage.asStateFlow()

    private var isRequestInProgress = false
    private var currentUserId: Int = -1

    init {
        fetchUserLocation()
        fetchUserId()
    }

    // NEW: Fetch current user ID from token
    private fun fetchUserId() {
        viewModelScope.launch {
            try {
                val token = userRepository.currentUserToken.first()
                if (token != "Unknown" && token.isNotBlank()) {
                    val jwt = JWT(token)
                    // Backend JWT payload uses 'id', not 'userId'
                    val userId = jwt.getClaim("id").asInt()
                    if (userId != null) {
                        currentUserId = userId
                        Log.d(TAG, "✅ Current user ID: $currentUserId")
                    } else {
                        Log.e(TAG, "❌ User ID claim is null in JWT")
                    }
                } else {
                    Log.d(TAG, "❌ No valid token available")
                }
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error fetching user ID: ${e.message}", e)
            }
        }
    }

    private fun fetchUserLocation() {
        viewModelScope.launch {
            try {
                val token = userRepository.currentUserToken.first()
                if (token == "Unknown" || token.isBlank()) {
                    userLocation = "Unknown"
                    Log.d(TAG, "❌ No token available for location fetch")
                    return@launch
                }

                val bearer = "Bearer $token"
                profileRepository.viewProfile(bearer).enqueue(object : Callback<ProfileResponse> {
                    override fun onResponse(call: Call<ProfileResponse>, res: Response<ProfileResponse>) {
                        try {
                            val responseBody = res.body()
                            if (res.isSuccessful && responseBody != null && responseBody.data != null) {
                                userLocation = responseBody.data.location ?: "Unknown"
                                Log.d(TAG, "✅ User location fetched: $userLocation")
                            } else {
                                userLocation = "Unknown"
                                Log.d(TAG, "❌ Failed to fetch user location - Response unsuccessful or null")
                            }
                        } catch (e: Exception) {
                            userLocation = "Unknown"
                            Log.e(TAG, "❌ Exception while parsing location: ${e.message}", e)
                        }
                    }

                    override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                        userLocation = "Unknown"
                        Log.e(TAG, "💥 Error fetching user location: ${t.message}", t)
                    }
                })
            } catch (e: Exception) {
                userLocation = "Unknown"
                Log.e(TAG, "💥 Exception in fetchUserLocation: ${e.message}", e)
            }
        }
    }

    // NEW: Add item to shopping cart
    fun addToCart(helpRequestId: Int) {
        if (currentUserId == -1) {
            _cartStatusMessage.value = "Gagal menambahkan ke keranjang. Silakan login ulang."
            Log.e(TAG, "❌ Cannot add to cart: User ID not available")
            viewModelScope.launch {
                delay(3000)
                _cartStatusMessage.value = null
            }
            return
        }

        viewModelScope.launch {
            try {
                Log.d(TAG, "🛒 Adding helpRequestId=$helpRequestId to cart for userId=$currentUserId")

                shoppingCartRepository.addToCart(currentUserId, helpRequestId).enqueue(
                    object : Callback<ShoppingCartResponse> {
                        override fun onResponse(
                            call: Call<ShoppingCartResponse>,
                            response: Response<ShoppingCartResponse>
                        ) {
                            if (response.isSuccessful) {
                                _cartStatusMessage.value = "✅ Berhasil ditambahkan ke keranjang!"
                                Log.d(TAG, "✅ Successfully added to cart")
                            } else {
                                // Enhanced error handling matching backend responses
                                val errorMessage = when (response.code()) {
                                    400 -> {
                                        // Backend returns 400 if item already in cart
                                        try {
                                            val errorBody = response.errorBody()?.string()
                                            Log.d(TAG, "Error body: $errorBody")
                                            if (errorBody?.contains("already in your cart", ignoreCase = true) == true) {
                                                "⚠️ Item sudah ada di keranjang"
                                            } else {
                                                "❌ Gagal menambahkan ke keranjang"
                                            }
                                        } catch (e: Exception) {
                                            "❌ Gagal menambahkan ke keranjang"
                                        }
                                    }
                                    404 -> "❌ Item tidak ditemukan"
                                    401 -> "❌ Silakan login terlebih dahulu"
                                    500 -> "❌ Terjadi kesalahan server"
                                    else -> "❌ Gagal menambahkan (${response.code()})"
                                }
                                _cartStatusMessage.value = errorMessage
                                Log.e(TAG, "❌ Failed to add to cart: $errorMessage")
                            }

                            // Clear message after 3 seconds
                            viewModelScope.launch {
                                delay(3000)
                                _cartStatusMessage.value = null
                            }
                        }

                        override fun onFailure(call: Call<ShoppingCartResponse>, t: Throwable) {
                            val errorMessage = when {
                                t.message?.contains("timeout", ignoreCase = true) == true ->
                                    "❌ Koneksi timeout. Coba lagi."
                                t.message?.contains("Unable to resolve host", ignoreCase = true) == true ->
                                    "❌ Tidak dapat terhubung ke server"
                                else -> "❌ Error: ${t.message}"
                            }
                            _cartStatusMessage.value = errorMessage
                            Log.e(TAG, "💥 Error adding to cart: ${t.message}", t)

                            viewModelScope.launch {
                                delay(3000)
                                _cartStatusMessage.value = null
                            }
                        }
                    }
                )
            } catch (e: Exception) {
                _cartStatusMessage.value = "❌ Terjadi kesalahan"
                Log.e(TAG, "💥 Exception in addToCart: ${e.message}", e)

                viewModelScope.launch {
                    delay(3000)
                    _cartStatusMessage.value = null
                }
            }
        }
    }

    fun loadHelpRequests() {
        if (isRequestInProgress) {
            Log.d(TAG, "loadHelpRequests: Request already in progress, skipping")
            return
        }

        Log.d(TAG, "loadHelpRequests: Starting to load help requests")
        _homeUIState.value = HomeUIState.Loading
        isRequestInProgress = true

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

    fun filterHelpRequests(type: String? = null, status: String? = null) {
        Log.d(TAG, "filterHelpRequests: Filtering by type=$type, status=$status")
        _homeUIState.value = HomeUIState.Loading

        try {
            val call = helpRequestRepository.getAllHelpRequests()

            call.enqueue(object : Callback<AllHelpResp> {
                override fun onResponse(p0: Call<AllHelpResp>, p1: Response<AllHelpResp>) {
                    val response = p1
                    Log.d(TAG, "filterHelpRequests onResponse: code ${response.code()}")

                    if (response.isSuccessful) {
                        val all = response.body()?.data ?: emptyList()

                        val filtered = when {
                            type.isNullOrBlank() -> all
                            type == "BARANG" -> all.filter { it.categoryId == 1 }
                            type == "JASA" -> all.filter { it.categoryId == 2 }
                            else -> {
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

    fun searchHelpRequests(query: String) {
        Log.d(TAG, "searchHelpRequests: Searching for '$query'")

        if (query.isBlank()) {
            loadHelpRequests()
            return
        }

        _homeUIState.value = HomeUIState.Loading

        try {
            val call = helpRequestRepository.getAllHelpRequests()

            call.enqueue(object : Callback<AllHelpResp> {
                override fun onResponse(p0: Call<AllHelpResp>, p1: Response<AllHelpResp>) {
                    val response = p1
                    Log.d(TAG, "searchHelpRequests onResponse: code ${response.code()}")

                    if (response.isSuccessful) {
                        val all = response.body()?.data ?: emptyList()

                        val searchResults = all.filter {
                            it.nameOfProduct.contains(query, ignoreCase = true) ||
                                    it.exchangeProductName.contains(query, ignoreCase = true) ||
                                    it.description.contains(query, ignoreCase = true) ||
                                    it.location.contains(query, ignoreCase = true)
                        }

                        Log.d(TAG, "searchHelpRequests: Found ${searchResults.size} results for '$query'")
                        _homeUIState.value = HomeUIState.Success(searchResults)
                    } else {
                        val errorMsg = "Failed to search help requests (HTTP ${response.code()})"
                        Log.e(TAG, errorMsg)
                        _homeUIState.value = HomeUIState.Error(errorMsg)
                    }
                }

                override fun onFailure(p0: Call<AllHelpResp>, p1: Throwable) {
                    val errorMsg = p1.message ?: "Unknown Error"
                    Log.e(TAG, "searchHelpRequests onFailure: $errorMsg", p1)
                    _homeUIState.value = HomeUIState.Error("Network error: $errorMsg")
                }
            })
        } catch (e: Exception) {
            Log.e(TAG, "searchHelpRequests: Exception caught", e)
            _homeUIState.value = HomeUIState.Error("Error: ${e.message}")
        }
    }
}