package com.januarzidanetinendeng.eightcanteen.data.remote

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiConfig {
    // Alamat backend dari https://github.com/alviangalen/backend-kantin-mobile
    // Ganti ini dengan server yang di-deploy (misal: "https://backend-kantin-mobile.onrender.com/")
    // Untuk localhost emulator android, gunakan: "http://10.0.2.2:3000/"
    var BASE_URL = "http://10.0.2.2:3000/" 
    
    // Sesuaikan APP_SECRET_KEY pada env backend
    var API_KEY = "my_super_secret_key_123" 

    fun getApiService(): ApiService {
        val loggingInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        
        val authInterceptor = Interceptor { chain ->
            val req = chain.request()
            val requestHeaders = req.newBuilder()
                .addHeader("x-api-key", API_KEY) // Diperlukan oleh backend untuk semua request
                .build()
            chain.proceed(requestHeaders)
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        return retrofit.create(ApiService::class.java)
    }
}
