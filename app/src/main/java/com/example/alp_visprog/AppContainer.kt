package com.example.alp_visprog

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.alp_visprog.repositories.AuthenticationRepository
import com.example.alp_visprog.repositories.AuthenticationRepositoryInterface
import com.example.alp_visprog.repositories.ExchangeRepository
import com.example.alp_visprog.repositories.HelpRequestRepository // Import this
import com.example.alp_visprog.repositories.ShoppingCartRepository
import com.example.alp_visprog.repositories.ProfileRepository
import com.example.alp_visprog.repositories.ProfileRepositoryInterface
import com.example.alp_visprog.repositories.UserRepository
import com.example.alp_visprog.repositories.UserRepositoryInterface
import com.example.alp_visprog.services.AuthenticationAPIService
import com.example.alp_visprog.services.ExchangeAPIService
import com.example.alp_visprog.services.HelpRequestAPIService // Import this
import com.example.alp_visprog.services.ShoppingCartAPIService
import com.example.alp_visprog.services.HelpRequestAPIService
import com.example.alp_visprog.services.ProfileAPIService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface AppContainerInterface {
    val authenticationRepository: AuthenticationRepositoryInterface
    val userRepository: UserRepositoryInterface
    val profileRepository: ProfileRepositoryInterface
    // exchange repository
    val exchangeRepository: ExchangeRepository
//    help request repository
    val helpRequestRepository: HelpRequestRepository
}

class AppContainer(
    private val datastore: DataStore<Preferences>
) : AppContainerInterface {

    // 1. Base URL
    // Use "http://10.0.2.2:3000/" for Android Emulator
    private val backendURL = "http://10.0.2.2:3000/"

    // 2. Init Retrofit (Lazy initialization)
    private val retrofit: Retrofit by lazy {
        initRetrofit()
    }

    private fun initRetrofit(): Retrofit {
        val logging = HttpLoggingInterceptor()
        logging.level = (HttpLoggingInterceptor.Level.BODY)

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .baseUrl(backendURL)
            .build()
    }

    // --- SERVICES ---

    private val authenticationRetrofitService: AuthenticationAPIService by lazy {
        retrofit.create(AuthenticationAPIService::class.java)
    }

    // Made 'private' unless you need to access it directly outside
    private val exchangeAPIService: ExchangeAPIService by lazy {
        retrofit.create(ExchangeAPIService::class.java)
    }

    // NEW: HelpRequest Service
    private val helpRequestAPIService: HelpRequestAPIService by lazy {
        retrofit.create(HelpRequestAPIService::class.java)
    }
    // 1. Initialize API Service
    private val shoppingCartAPIService: ShoppingCartAPIService by lazy {
        retrofit.create(ShoppingCartAPIService::class.java)
    }

    // New: Profile Service (required by ProfileRepository)
    private val profileAPIService: ProfileAPIService by lazy {
        retrofit.create(ProfileAPIService::class.java)
    }

    // -------------------------------------------------------------
    // 3. REPOSITORIES (Inject the services into them)
    // -------------------------------------------------------------

    override val authenticationRepository: AuthenticationRepositoryInterface by lazy {
        AuthenticationRepository(authenticationRetrofitService)
    }

    override val userRepository: UserRepositoryInterface by lazy {
        UserRepository(datastore)
    }

    override val exchangeRepository: ExchangeRepository by lazy {
        ExchangeRepository(exchangeAPIService)
    }

    // NEW: HelpRequest Repository
    override val helpRequestRepository: HelpRequestRepository by lazy {
        HelpRequestRepository(helpRequestAPIService)
    }

    // 2. Initialize Repository (Add this property)
    val shoppingCartRepository: ShoppingCartRepository by lazy {
        ShoppingCartRepository(shoppingCartAPIService)
    }
    // Provide profileRepository as required by the interface
    override val profileRepository: ProfileRepositoryInterface by lazy {
        ProfileRepository(profileAPIService)
    }

    }
}