package com.yogaprasetyo.storyapp.util

import com.yogaprasetyo.storyapp.data.UserRepository
import com.yogaprasetyo.storyapp.data.remote.retrofit.ApiConfig

class Injection {
    companion object {
        fun provideUserRepository(): UserRepository {
            val apiService = ApiConfig.getApiService()
            return UserRepository.getInstance(apiService)
        }
    }
}