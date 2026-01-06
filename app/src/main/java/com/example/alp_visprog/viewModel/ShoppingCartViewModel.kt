package com.example.alp_visprog.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.auth0.android.jwt.JWT
import com.example.alp_visprog.App
import com.example.alp_visprog.models.ShoppingCartResponse
import com.example.alp_visprog.models.CartItem
import com.example.alp_visprog.repositories.ShoppingCartRepository
import com.example.alp_visprog.repositories.UserRepositoryInterface
import com.example.alp_visprog.uiStates.ShoppingCartUIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ShoppingCartViewModel(
    private val shoppingCartRepository: ShoppingCartRepository,
    private val userRepository: UserRepositoryInterface
) : ViewModel() {

    companion object {
        private const val TAG = "ShoppingCartViewModel"

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as App)
                ShoppingCartViewModel(
                    application.container.shoppingCartRepository,
                    application.container.userRepository
                )
            }
        }
    }

    private val _uiState = MutableStateFlow<ShoppingCartUIState>(ShoppingCartUIState.Loading)
    val uiState: StateFlow<ShoppingCartUIState> = _uiState.asStateFlow()

    private var currentUserId: Int = -1

    init {
        Log.d(TAG, "🔄 ShoppingCartViewModel initialized")
        loadCurrentUserAndCart()
    }

    private fun loadCurrentUserAndCart() {
        viewModelScope.launch {
            try {
                Log.d(TAG, "📡 Fetching user token...")
                val token = userRepository.currentUserToken.first()

                if (token == "Unknown" || token.isBlank()) {
                    Log.e(TAG, "❌ No valid token found")
                    _uiState.value = ShoppingCartUIState.Error("Silakan login terlebih dahulu")
                    return@launch
                }

                Log.d(TAG, "🔑 Token found: ${token.take(20)}...")

                // Decode JWT to get user ID
                val jwt = JWT(token)

                // CRITICAL: Backend uses 'id' field, not 'userId'
                val userId = jwt.getClaim("id").asInt()

                if (userId == null) {
                    Log.e(TAG, "❌ Failed to extract user ID from token")
                    _uiState.value = ShoppingCartUIState.Error("Token tidak valid. Silakan login ulang.")
                    return@launch
                }

                currentUserId = userId
                Log.d(TAG, "✅ User ID extracted: $currentUserId")

                // Load cart
                getCart()

            } catch (e: Exception) {
                Log.e(TAG, "💥 Error loading user: ${e.message}", e)
                _uiState.value = ShoppingCartUIState.Error("Gagal memuat data: ${e.message}")
            }
        }
    }

    fun getCart() {
        if (currentUserId == -1) {
            Log.e(TAG, "❌ Cannot load cart: User ID is -1")
            _uiState.value = ShoppingCartUIState.Error("User ID tidak valid")
            return
        }

        Log.d(TAG, "🛒 Loading cart for user ID: $currentUserId")
        _uiState.value = ShoppingCartUIState.Loading

        shoppingCartRepository.getCart(currentUserId).enqueue(object : Callback<ShoppingCartResponse> {
            override fun onResponse(call: Call<ShoppingCartResponse>, response: Response<ShoppingCartResponse>) {
                Log.d(TAG, "📥 Cart response received - Code: ${response.code()}")

                if (response.isSuccessful && response.body() != null) {
                    val cartData = response.body()!!.data
                    val items = cartData.items

                    Log.d(TAG, "✅ Cart loaded successfully: ${items.size} items")
                    _uiState.value = ShoppingCartUIState.Success(items)
                } else {
                    val errorMessage = when (response.code()) {
                        404 -> "Keranjang tidak ditemukan"
                        401 -> "Silakan login terlebih dahulu"
                        500 -> "Terjadi kesalahan server"
                        else -> "Gagal memuat keranjang (${response.code()})"
                    }
                    Log.e(TAG, "❌ Failed to load cart: $errorMessage")
                    _uiState.value = ShoppingCartUIState.Error(errorMessage)
                }
            }

            override fun onFailure(call: Call<ShoppingCartResponse>, t: Throwable) {
                val errorMessage = when {
                    t.message?.contains("timeout", ignoreCase = true) == true ->
                        "Koneksi timeout. Pastikan backend berjalan di http://10.0.2.2:3000"
                    t.message?.contains("Unable to resolve host", ignoreCase = true) == true ->
                        "Tidak dapat terhubung ke server. Pastikan backend berjalan."
                    else -> "Error: ${t.message}"
                }
                Log.e(TAG, "💥 Network error: $errorMessage", t)
                _uiState.value = ShoppingCartUIState.Error(errorMessage)
            }
        })
    }

    fun removeFromCart(helpRequestId: Int) {
        if (currentUserId == -1) {
            Log.e(TAG, "❌ Cannot remove from cart: User ID is -1")
            return
        }

        Log.d(TAG, "🗑️ Removing item $helpRequestId from cart")

        shoppingCartRepository.removeFromCart(currentUserId, helpRequestId).enqueue(
            object : Callback<ShoppingCartResponse> {
                override fun onResponse(call: Call<ShoppingCartResponse>, response: Response<ShoppingCartResponse>) {
                    if (response.isSuccessful && response.body() != null) {
                        val cartData = response.body()!!.data
                        val items = cartData.items

                        Log.d(TAG, "✅ Item removed successfully. Remaining items: ${items.size}")
                        _uiState.value = ShoppingCartUIState.Success(items)
                    } else {
                        Log.e(TAG, "❌ Failed to remove item: ${response.code()}")
                        _uiState.value = ShoppingCartUIState.Error("Gagal menghapus item")

                        // Reload cart to ensure consistency
                        getCart()
                    }
                }

                override fun onFailure(call: Call<ShoppingCartResponse>, t: Throwable) {
                    Log.e(TAG, "💥 Error removing item: ${t.message}", t)
                    _uiState.value = ShoppingCartUIState.Error("Error: ${t.message}")
                }
            }
        )
    }

    fun refreshCart() {
        Log.d(TAG, "🔄 Manually refreshing cart")
        getCart()
    }
}