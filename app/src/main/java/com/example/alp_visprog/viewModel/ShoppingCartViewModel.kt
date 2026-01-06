package com.example.alp_visprog.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.alp_visprog.App
import com.example.alp_visprog.models.ShoppingCartResponse
import com.example.alp_visprog.repositories.ShoppingCartRepository
import com.example.alp_visprog.repositories.UserRepositoryInterface
import com.example.alp_visprog.uiStates.ShoppingCartUIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ShoppingCartViewModel(
    private val shoppingCartRepository: ShoppingCartRepository,
    private val userRepository: UserRepositoryInterface
) : ViewModel() {

    private val _uiState = MutableStateFlow<ShoppingCartUIState>(ShoppingCartUIState.Loading)
    val uiState: StateFlow<ShoppingCartUIState> = _uiState.asStateFlow()

    private var currentUserId: Int = -1

    init {
        loadCurrentUserAndCart()
    }

    private fun loadCurrentUserAndCart() {
        viewModelScope.launch {
            // Note: Assuming you have a way to get ID.
            // If UserRepository only has token/username, you might need to fetch profile first.
            // For now, we'll hardcode or fetch if your Repo supports it.
            // TEMPORARY: Hardcoding ID 1 for testing (Replace with real user ID logic later)
            currentUserId = 1
            getCart()
        }
    }

    fun getCart() {
        if (currentUserId == -1) return
        _uiState.value = ShoppingCartUIState.Loading

        shoppingCartRepository.getCart(currentUserId).enqueue(object : Callback<ShoppingCartResponse> {
            override fun onResponse(call: Call<ShoppingCartResponse>, response: Response<ShoppingCartResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    _uiState.value = ShoppingCartUIState.Success(response.body()!!.data.items)
                } else {
                    _uiState.value = ShoppingCartUIState.Error("Failed to load cart")
                }
            }

            override fun onFailure(call: Call<ShoppingCartResponse>, t: Throwable) {
                _uiState.value = ShoppingCartUIState.Error(t.localizedMessage ?: "Unknown error")
            }
        })
    }

    fun removeFromCart(helpRequestId: Int) {
        if (currentUserId == -1) return

        // Optimistic update or reloading state could go here
        shoppingCartRepository.removeFromCart(currentUserId, helpRequestId).enqueue(object : Callback<ShoppingCartResponse> {
            override fun onResponse(call: Call<ShoppingCartResponse>, response: Response<ShoppingCartResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    // Update UI with new list
                    _uiState.value = ShoppingCartUIState.Success(response.body()!!.data.items)
                } else {
                    _uiState.value = ShoppingCartUIState.Error("Failed to remove item")
                }
            }
            override fun onFailure(call: Call<ShoppingCartResponse>, t: Throwable) {
                _uiState.value = ShoppingCartUIState.Error("Error: ${t.message}")
            }
        })
    }

    companion object {
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
}