package com.example.alp_visprog.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import com.auth0.android.jwt.JWT
import com.example.alp_visprog.App
import com.example.alp_visprog.uiStates.AuthenticationStatusUIState
import com.example.alp_visprog.uiStates.AuthenticationUIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
//import com.example.alp_visprog.Application
import com.example.alp_visprog.R
import com.example.alp_visprog.repositories.AuthenticationRepositoryInterface
import kotlinx.coroutines.launch
import com.example.alp_visprog.models.ErrorModel
import com.example.alp_visprog.models.UserResponse
import com.example.alp_visprog.repositories.UserRepositoryInterface
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class AuthenticationViewModel(

    private val authenticationRepository: AuthenticationRepositoryInterface,
    private val userRepository: UserRepositoryInterface
) : ViewModel() {
    var authenticationStatus: AuthenticationStatusUIState by mutableStateOf(
        AuthenticationStatusUIState.Start
    )
        private set


    private val _authenticationUIState = MutableStateFlow(AuthenticationUIState())

    val authenticationUIState: StateFlow<AuthenticationUIState>
        get() {
            return _authenticationUIState.asStateFlow()
        }

    var usernameInput by mutableStateOf("")
        private set

    var passwordInput by mutableStateOf("")
        private set

    var confirmPasswordInput by mutableStateOf("")
        private set

    var emailInput by mutableStateOf("")
        private set

    fun changeEmailInput(emailInput: String) {
        this.emailInput = emailInput
    }

    fun changeConfirmPasswordInput(confirmPasswordInput: String) {
        this.confirmPasswordInput = confirmPasswordInput
    }

    fun changeUsernameInput(usernameInput: String) {
        this.usernameInput = usernameInput
    }

    fun changePasswordInput(passwordInput: String) {
        this.passwordInput = passwordInput
    }

    fun changePasswordVisibility() {
        _authenticationUIState.update { currentState ->
            if (currentState.showPassword) {
                currentState.copy(
                    showPassword = false,
                    passwordVisibility = PasswordVisualTransformation(),
                    passwordVisibilityIcon = R.drawable.ic_password_visible
                )
            } else {
                currentState.copy(
                    showPassword = true,
                    passwordVisibility = VisualTransformation.None,
                    passwordVisibilityIcon = R.drawable.ic_password_invisible
                )
            }
        }
    }

    fun changeConfirmPasswordVisibility() {
        _authenticationUIState.update { currentState ->
            if (currentState.showConfirmPassword) {
                currentState.copy(
                    showConfirmPassword = false,
                    confirmPasswordVisibility = PasswordVisualTransformation(),
                    confirmPasswordVisibilityIcon = R.drawable.ic_password_visible
                )
            } else {
                currentState.copy(
                    showConfirmPassword = true,
                    confirmPasswordVisibility = VisualTransformation.None,
                    confirmPasswordVisibilityIcon = R.drawable.ic_password_invisible
                )
            }
        }
    }

    fun checkLoginForm() {
        if (emailInput.isNotEmpty() && passwordInput.isNotEmpty()) {
            _authenticationUIState.update { currentState ->
                currentState.copy(
                    buttonEnabled = true
                )
            }
        } else {
            _authenticationUIState.update { currentState ->
                currentState.copy(
                    buttonEnabled = false
                )
            }
        }
    }

    fun checkRegisterForm() {
        if (emailInput.isNotEmpty() && passwordInput.isNotEmpty() && usernameInput.isNotEmpty() && confirmPasswordInput.isNotEmpty() && passwordInput == confirmPasswordInput) {
            _authenticationUIState.update { currentState ->
                currentState.copy(
                    buttonEnabled = true
                )
            }
        } else {
            _authenticationUIState.update { currentState ->
                currentState.copy(
                    buttonEnabled = false
                )
            }
        }
    }

    fun checkButtonEnabled(isEnabled: Boolean): Color {
        if (isEnabled) {
            return Color.Blue
        }

        return Color.LightGray
    }

    fun resetViewModel() {
        changeEmailInput("")
        changePasswordInput("")
        changeUsernameInput("")
        changeConfirmPasswordInput("")
        _authenticationUIState.update { currentState ->
            currentState.copy(
                showConfirmPassword = false,
                showPassword = false,
                passwordVisibility = PasswordVisualTransformation(),
                confirmPasswordVisibility = PasswordVisualTransformation(),
                passwordVisibilityIcon = R.drawable.ic_password_visible,
                confirmPasswordVisibilityIcon = R.drawable.ic_password_visible,
                buttonEnabled = false
            )
        }
    }

    fun register(navController: NavHostController) {
        // Client-side validation first
        when {
            usernameInput.isBlank() -> {
                authenticationStatus = AuthenticationStatusUIState.Failed("Username tidak boleh kosong")
                return
            }
            emailInput.isBlank() -> {
                authenticationStatus = AuthenticationStatusUIState.Failed("Email tidak boleh kosong")
                return
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(emailInput).matches() -> {
                authenticationStatus = AuthenticationStatusUIState.Failed("Format email tidak valid")
                return
            }
            passwordInput.length < 8 -> {
                authenticationStatus = AuthenticationStatusUIState.Failed("Kata sandi minimal 8 karakter")
                return
            }
            passwordInput != confirmPasswordInput -> {
                authenticationStatus = AuthenticationStatusUIState.Failed("Kata sandi dan konfirmasi tidak cocok")
                return
            }
        }

        viewModelScope.launch {
            authenticationStatus = AuthenticationStatusUIState.Loading

            try {
                val call = authenticationRepository.register(usernameInput, emailInput, passwordInput)

                call.enqueue(object : Callback<UserResponse> {
                    override fun onResponse(call: Call<UserResponse>, res: Response<UserResponse>) {
                        if (res.isSuccessful && res.body() != null) {
                            // 1. Get token safely
                            val responseData = res.body()?.data
                            val token = responseData?.token

                            if (token != null) {
                                try {
                                    // 2. Decode JWT safely
                                    val jwt = JWT(token)
                                    val username = jwt.getClaim("username").asString()

                                    if (username != null) {
                                        // 3. Save only if we have all data
                                        savedUsernameToken(token, username, emailInput)
                                        authenticationStatus = AuthenticationStatusUIState.Success(responseData)

                                        resetViewModel()
                                        navController.navigate("Home") {
                                            popUpTo("login") { inclusive = true }
                                        }
                                    } else {
                                        authenticationStatus = AuthenticationStatusUIState.Failed("Token error: Username missing")
                                    }
                                } catch (e: Exception) {
                                    authenticationStatus = AuthenticationStatusUIState.Failed("Token invalid")
                                }
                            } else {
                                authenticationStatus = AuthenticationStatusUIState.Failed("Registrasi berhasil tetapi token kosong")
                            }
                        } else {
                            // Enhanced error handling for registration
                            val errorMessage = try {
                                val errorBody = res.errorBody()
                                if (errorBody != null) {
                                    val errorString = errorBody.string()

                                    // Try to parse as ErrorModel
                                    try {
                                        val errorModel = Gson().fromJson(errorString, ErrorModel::class.java)
                                        var msg = errorModel.errors

                                        // Make error messages more user-friendly
                                        when {
                                            msg.contains("username", ignoreCase = true) &&
                                                    msg.contains("Unique constraint", ignoreCase = true) -> {
                                                "Username sudah digunakan. Silakan pilih username lain."
                                            }
                                            msg.contains("email", ignoreCase = true) &&
                                                    msg.contains("Unique constraint", ignoreCase = true) -> {
                                                "Email sudah terdaftar. Silakan gunakan email lain atau login."
                                            }
                                            msg.contains("Unique constraint", ignoreCase = true) -> {
                                                "Username atau email sudah digunakan. Silakan coba yang lain."
                                            }
                                            else -> msg
                                        }
                                    } catch (e: Exception) {
                                        // If not ErrorModel format, return raw error
                                        when {
                                            errorString.contains("username", ignoreCase = true) ->
                                                "Username sudah digunakan. Silakan pilih username lain."
                                            errorString.contains("email", ignoreCase = true) ->
                                                "Email sudah terdaftar. Silakan gunakan email lain."
                                            else -> "Registrasi gagal: ${res.code()}"
                                        }
                                    }
                                } else {
                                    when (res.code()) {
                                        400 -> "Data tidak valid. Periksa kembali input Anda."
                                        409 -> "Username atau email sudah terdaftar."
                                        500 -> "Terjadi kesalahan server. Coba lagi nanti."
                                        else -> "Registrasi gagal (${res.code()})"
                                    }
                                }
                            } catch (e: Exception) {
                                "Gagal memproses error: ${e.message ?: "Unknown"}"
                            }

                            authenticationStatus = AuthenticationStatusUIState.Failed(errorMessage)
                        }
                    }

                    override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                        authenticationStatus = AuthenticationStatusUIState.Failed(
                            t.localizedMessage ?: "Koneksi gagal. Periksa internet Anda."
                        )
                    }
                })

            } catch (error: IOException) {
                authenticationStatus = AuthenticationStatusUIState.Failed(
                    error.localizedMessage ?: "Kesalahan jaringan"
                )
            }
        }
    }

    fun login(navController: NavHostController) {
        viewModelScope.launch {
            authenticationStatus = AuthenticationStatusUIState.Loading

            try {
                val call = authenticationRepository.login(emailInput, passwordInput)

                call.enqueue(object : Callback<UserResponse> {
                    override fun onResponse(call: Call<UserResponse>, res: Response<UserResponse>) {
                        viewModelScope.launch {
                            if (res.isSuccessful && res.body() != null) {
                                // 1. Get token safely
                                val responseData = res.body()?.data
                                val token = responseData?.token

                                if (token != null) {
                                    try {
                                        // 2. Decode JWT safely
                                        val jwt = JWT(token)
                                        val username = jwt.getClaim("username").asString()

                                        if (username != null) {
                                            // 3. Save only if we have all data
                                            savedUsernameToken(token, username, emailInput)
                                            authenticationStatus = AuthenticationStatusUIState.Success(responseData)

                                            resetViewModel()
                                            navController.navigate("Home") {
                                                popUpTo("login") { inclusive = true }
                                            }
                                        } else {
                                            authenticationStatus = AuthenticationStatusUIState.Failed("Token error: Username missing")
                                        }
                                    } catch (e: Exception) {
                                        authenticationStatus = AuthenticationStatusUIState.Failed("Token invalid")
                                    }
                                } else {
                                    authenticationStatus = AuthenticationStatusUIState.Failed("Login berhasil tetapi token kosong")
                                }
                            } else {
                                val errorMessage = try {
                                    val error = Gson().fromJson(
                                        res.errorBody()!!.charStream(),
                                        ErrorModel::class.java
                                    )
                                    error?.errors ?: "Login failed: ${res.code()}"
                                } catch (e: Exception) {
                                    "Login failed: ${res.code()}"
                                }

                                authenticationStatus = AuthenticationStatusUIState.Failed(errorMessage)
                            }
                        }
                    }

                    override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                        viewModelScope.launch {
                            authenticationStatus =
                                AuthenticationStatusUIState.Failed(t.localizedMessage ?: "Unknown error")
                        }
                    }
                })

            } catch (error: IOException) {
                authenticationStatus = AuthenticationStatusUIState.Failed(error.localizedMessage ?: "Network error")
            }
        }
    }

    // [Modified] Accept email as a parameter
    private fun savedUsernameToken(token: String, username: String, email: String) {
        viewModelScope.launch {
            userRepository.saveUserToken(token)
            userRepository.saveUsername(username)
            userRepository.saveUserEmail(email) // [New] Save the email
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as App)
                val authenticationRepository = application.container.authenticationRepository
                val userRepository = application.container.userRepository
                AuthenticationViewModel(authenticationRepository, userRepository)
            }
        }
    }
}