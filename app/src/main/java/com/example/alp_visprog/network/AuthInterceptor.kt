package com.example.alp_visprog.network

import com.example.alp_visprog.repositories.UserRepositoryInterface
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Interceptor that automatically adds authentication token to all API requests
 * This prevents HTTP 401 Unauthorized errors
 */
class AuthInterceptor(
    private val userRepository: UserRepositoryInterface
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Get the current auth token from DataStore via UserRepository
        val token = runBlocking {
            try {
                userRepository.getToken().first()
            } catch (e: Exception) {
                null
            }
        }

        // Add Authorization header if token exists and is valid
        val newRequest = if (!token.isNullOrBlank() && token != "Unknown") {
            originalRequest.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            originalRequest
        }

        return chain.proceed(newRequest)
    }
}