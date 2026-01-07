package com.example.alp_visprog

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.alp_visprog.network.AuthInterceptor
import com.example.alp_visprog.repositories.AuthenticationRepository
import com.example.alp_visprog.repositories.AuthenticationRepositoryInterface
import com.example.alp_visprog.repositories.ExchangeRepository
import com.example.alp_visprog.repositories.HelpRequestRepository
import com.example.alp_visprog.repositories.ShoppingCartRepository
import com.example.alp_visprog.repositories.ProfileRepository
import com.example.alp_visprog.repositories.ProfileRepositoryInterface
import com.example.alp_visprog.repositories.UserRepository
import com.example.alp_visprog.repositories.UserRepositoryInterface
import com.example.alp_visprog.services.AuthenticationAPIService
import com.example.alp_visprog.services.ExchangeAPIService
import com.example.alp_visprog.services.HelpRequestAPIService
import com.example.alp_visprog.services.LocationIQAPIService
import com.example.alp_visprog.services.ShoppingCartAPIService
import com.example.alp_visprog.services.ProfileAPIService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

interface AppContainerInterface {
    val authenticationRepository: AuthenticationRepositoryInterface
    val userRepository: UserRepositoryInterface
    val profileRepository: ProfileRepositoryInterface
    val exchangeRepository: ExchangeRepository
    val helpRequestRepository: HelpRequestRepository
    val shoppingCartRepository: ShoppingCartRepository
}

class AppContainer(
    private val datastore: DataStore<Preferences>,
    private val backendURL: String
) : AppContainerInterface {


    private val authInterceptor: AuthInterceptor by lazy {
        AuthInterceptor(userRepository)
    }

    // Init Retrofit with logging AND auth interceptor
    private val retrofit: Retrofit by lazy {
        initRetrofit()
    }

    private fun initRetrofit(): Retrofit {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor(authInterceptor) // ADDED: Auto token injection
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .baseUrl(backendURL)
            .build()
    }

    // ========== SERVICES ==========

    private val locationIQRetrofit = Retrofit.Builder()
        .baseUrl("https://us1.locationiq.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val locationIQService: LocationIQAPIService by lazy {
        locationIQRetrofit.create(LocationIQAPIService::class.java)
    }

    private val authenticationRetrofitService: AuthenticationAPIService by lazy {
        retrofit.create(AuthenticationAPIService::class.java)
    }

    private val exchangeAPIService: ExchangeAPIService by lazy {
        retrofit.create(ExchangeAPIService::class.java)
    }

    private val helpRequestAPIService: HelpRequestAPIService by lazy {
        retrofit.create(HelpRequestAPIService::class.java)
    }

    private val shoppingCartAPIService: ShoppingCartAPIService by lazy {
        retrofit.create(ShoppingCartAPIService::class.java)
    }

    private val profileAPIService: ProfileAPIService by lazy {
        retrofit.create(ProfileAPIService::class.java)
    }

    // ========== REPOSITORIES ==========

    override val authenticationRepository: AuthenticationRepositoryInterface by lazy {
        AuthenticationRepository(authenticationRetrofitService)
    }

    override val userRepository: UserRepositoryInterface by lazy {
        UserRepository(datastore)
    }

    override val exchangeRepository: ExchangeRepository by lazy {
        ExchangeRepository(exchangeAPIService)
    }

    override val helpRequestRepository: HelpRequestRepository by lazy {
        HelpRequestRepository(helpRequestAPIService)
    }

    override val shoppingCartRepository: ShoppingCartRepository by lazy {
        ShoppingCartRepository(shoppingCartAPIService)
    }

    override val profileRepository: ProfileRepositoryInterface by lazy {
        ProfileRepository(profileAPIService)
    }
}