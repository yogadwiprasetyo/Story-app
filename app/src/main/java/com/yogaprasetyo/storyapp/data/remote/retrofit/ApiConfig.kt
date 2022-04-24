package com.yogaprasetyo.storyapp.data.remote.retrofit

import com.yogaprasetyo.storyapp.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Configuration for Okhttp and Retrofit
 * setup base url and converter JSON
 * */
object ApiConfig {
    // Set to public and var for testing
    var baseUrl = "https://story-api.dicoding.dev/v1/"

    fun getApiService(): ApiService {
        val client = OkHttpClient.Builder()
            .addInterceptor(onlyLoggingOnDebug())
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
        return retrofit.create(ApiService::class.java)
    }

    private fun onlyLoggingOnDebug(): HttpLoggingInterceptor {
        return if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        } else {
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE)
        }
    }
}
