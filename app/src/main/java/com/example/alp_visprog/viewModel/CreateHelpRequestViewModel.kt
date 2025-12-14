package com.example.alp_visprog.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.alp_visprog.App
import com.example.alp_visprog.models.GeneralResponse
import com.example.alp_visprog.repositories.HelpRequestRepository
import com.example.alp_visprog.uiStates.CreateHelpRequestUIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CreateHelpRequestViewModel(
    private val helpRequestRepository: HelpRequestRepository
) : ViewModel() {

    // 1. Form Data
    var title by mutableStateOf("")
    var description by mutableStateOf("")
    var category by mutableStateOf("")

    // 2. UI State (Using Best Practice: StateFlow)
    private val _dataStatus = MutableStateFlow<CreateHelpRequestUIState>(CreateHelpRequestUIState.Idle)
    val dataStatus: StateFlow<CreateHelpRequestUIState> = _dataStatus.asStateFlow()

    // 3. Reset Function
    fun clearErrorMessage() {
        _dataStatus.value = CreateHelpRequestUIState.Idle
    }

    fun resetForm() {
        _dataStatus.value = CreateHelpRequestUIState.Idle
        title = ""
        description = ""
        category = ""
    }

    // 4. Submit Function
    fun submitHelpRequest() {
        // Validation
        if (title.isBlank() || description.isBlank() || category.isBlank()) {
            _dataStatus.value = CreateHelpRequestUIState.Error("Please fill in all fields.")
            return
        }

        _dataStatus.value = CreateHelpRequestUIState.Loading

        val call = helpRequestRepository.createHelpRequest(
            title = title,
            description = description,
            category = category
        )

        call.enqueue(object : Callback<GeneralResponse> {
            override fun onResponse(call: Call<GeneralResponse>, response: Response<GeneralResponse>) {
                if (response.isSuccessful) {
                    _dataStatus.value = CreateHelpRequestUIState.Success
                } else {
                    _dataStatus.value = CreateHelpRequestUIState.Error("Failed to create post.")
                }
            }

            override fun onFailure(call: Call<GeneralResponse>, t: Throwable) {
                _dataStatus.value = CreateHelpRequestUIState.Error(t.message ?: "Unknown Error")
            }
        })
    }

    // 5. Factory
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as App)
                val helpRequestRepository = application.container.helpRequestRepository
                CreateHelpRequestViewModel(helpRequestRepository)
            }
        }
    }
}