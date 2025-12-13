package com.example.alp_visprog

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.alp_visprog.repositories.AuthenticationRepository
import com.example.alp_visprog.repositories.AuthenticationRepositoryInterface
import com.example.alp_visprog.repositories.ExchangeRepository
import com.example.alp_visprog.repositories.UserRepository
import com.example.alp_visprog.repositories.UserRepositoryInterface
import com.example.alp_visprog.services.AuthenticationAPIService
import com.example.alp_visprog.services.ExchangeAPIService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface AppContainerInterface {
    val authenticationRepository: AuthenticationRepositoryInterface
    val userRepository: UserRepositoryInterface
    // exchange repository
    val exchangeRepository: ExchangeRepository
}

class AppContainer (
    private val datastore: DataStore<Preferences>
): AppContainerInterface {
    private val backendURL = "http://10.0.2.2:3000/" // masih placeholder;
    // Note: If you are using the Android Emulator, use "http://10.0.2.2:3000/"
    // If using a physical device, use your laptop's IP address (e.g., "http://192.168.x.x:3000/")

//    // RETROFIT SERVICE
//    private val authenticationRetrofitService: AuthenticationAPIService by lazy {
//        val retrofit = initRetrofit()
//        retrofit.create(AuthenticationAPIService::class.java)
//    }
//
//    override val authenticationRepository: AuthenticationRepositoryInterface by lazy {
//        AuthenticationRepository(authenticationRetrofitService)
//    }
//
//    //REPOSITORY INIT
//    override val userRepository: UserRepositoryInterface by lazy {
//        UserRepository(datastore)
//    }
//
//    private fun initRetrofit(): Retrofit {
//        val logging = HttpLoggingInterceptor()
//        logging.level = (HttpLoggingInterceptor.Level.BODY)
//
//        val client = OkHttpClient.Builder()
//        client.addInterceptor(logging)
//
//        return Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
//            .client(client.build()).baseUrl(backendURL).build()
//    }
//
//    private val exchangeAPIService: ExchangeAPIService by lazy {
//        retrofit.create(ExchangeAPIService::class.java)
//    }

//    yang kamu bikin aku ganti jadi gini ya biar lebih efisien, soalnya harus nambah retrofit buat fitur lain juga biar ga initialize retrofit terus
//    1. initialize retrofit ONCE here so it can be reused
    private val retrofit: Retrofit by lazy {
        initRetrofit()
}
    private fun initRetrofit(): Retrofit {
        val logging = HttpLoggingInterceptor()
        logging.level = (HttpLoggingInterceptor.Level.BODY)

        val client = OkHttpClient.Builder()
        client.addInterceptor(logging)

        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .client(client.build())
            .baseUrl(backendURL)
            .build()
    }

    // 2. Authentication Service
    private val authenticationRetrofitService: AuthenticationAPIService by lazy {
        retrofit.create(AuthenticationAPIService::class.java)
    }

    // 3. Exchange API Service (The new addition)
    val exchangeAPIService: ExchangeAPIService by lazy {
        retrofit.create(ExchangeAPIService::class.java)
    }

    // 4. Repositories
    override val authenticationRepository: AuthenticationRepositoryInterface by lazy {
        AuthenticationRepository(authenticationRetrofitService)
    }

    override val userRepository: UserRepositoryInterface by lazy {
        UserRepository(datastore)
    }

//    Now we need to tell AppContainer how to create this Repository so we can use it later.
//
//Open app/src/main/java/com/example/alp_visprog/AppContainer.kt again.
//
//Add the exchangeRepository property at the bottom of the class (inside the class).
    override val exchangeRepository: ExchangeRepository by lazy {
        ExchangeRepository(exchangeAPIService)
    }
}