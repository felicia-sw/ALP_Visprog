package com.example.alp_visprog.repositories

import com.example.alp_visprog.models.UserResponse
import com.example.alp_visprog.services.AuthenticationAPIService
import retrofit2.Call

interface AuthenticationRepositoryInterface {
    fun register(
        username: String,
        email: String,
        pass: String,
        phone: String,
        location: String,
        lat: Double,
        lon: Double
    ): Call<UserResponse>

    fun login(email: String, password: String): Call<UserResponse>
}

class AuthenticationRepository(
    private val authenticationAPIService: AuthenticationAPIService
) : AuthenticationRepositoryInterface {
    override fun register(
        username: String,
        email: String,
        pass: String,
        phone: String,
        location: String,
        lat: Double,
        lon: Double
    ): Call<UserResponse> {
        return authenticationAPIService.register(username, email, pass, phone, location, lat, lon)
    }

    override fun login(email: String, password: String): Call<UserResponse> {
        val loginMap = HashMap<String, String>()

        loginMap["email"] = email
        loginMap["password"] = password

        return authenticationAPIService.login(loginMap)
    }
}
