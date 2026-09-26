package com.januarzidanetinendeng.eightcanteen.data.remote

import android.content.Context
import com.januarzidanetinendeng.eightcanteen.BuildConfig
import com.januarzidanetinendeng.eightcanteen.data.local.SessionManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiConfig {
    // Diambil secara aman dari BuildConfig (berasal dari .env / local.properties)
    val BASE_URL: String = BuildConfig.BASE_URL
    val API_KEY: String = BuildConfig.API_KEY

    // In-memory token cache jika SessionManager belum di-init
    private var authToken: String? = null
    private var applicationContext: Context? = null

    fun init(context: Context) {
        applicationContext = context.applicationContext
        val savedToken = SessionManager.getInstance(context).getAuthToken()
        if (!savedToken.isNullOrBlank()) {
            authToken = savedToken
        }
    }

    fun setAuthToken(token: String?) {
        authToken = token
    }

    fun getAuthToken(): String? {
        return authToken ?: applicationContext?.let { SessionManager.getInstance(it).getAuthToken() }
    }

    fun getApiService(context: Context? = null): ApiService {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val authInterceptor = Interceptor { chain ->
            val original = chain.request()
            val requestBuilder = original.newBuilder()

            // Header x-api-key wajib untuk semua request
            requestBuilder.addHeader("x-api-key", API_KEY)

            // Tambahkan Authorization: Bearer <token> jika ada
            val token = authToken
                ?: context?.let { SessionManager.getInstance(it).getAuthToken() }
                ?: applicationContext?.let { SessionManager.getInstance(it).getAuthToken() }

            if (!token.isNullOrBlank()) {
                requestBuilder.addHeader("Authorization", "Bearer $token")
            }

            chain.proceed(requestBuilder.build())
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
