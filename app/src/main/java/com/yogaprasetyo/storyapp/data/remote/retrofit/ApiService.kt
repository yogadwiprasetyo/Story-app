package com.yogaprasetyo.storyapp.data.remote.retrofit

import com.yogaprasetyo.storyapp.data.remote.response.ResponseLogin
import com.yogaprasetyo.storyapp.data.remote.response.ResponseRegister
import com.yogaprasetyo.storyapp.data.remote.response.ResponseStories
import com.yogaprasetyo.storyapp.data.remote.response.ResponseUploadStory
import com.yogaprasetyo.storyapp.model.UserLoginModel
import com.yogaprasetyo.storyapp.model.UserRegisterModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ApiService {
    @POST("register")
    suspend fun createUser(
        @Body user: UserRegisterModel
    ): ResponseRegister

    @POST("login")
    suspend fun authenticate(
        @Body user: UserLoginModel
    ): ResponseLogin

    @GET("stories")
    suspend fun allStories(
        @Header("Authorization") authorization: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): ResponseStories

    @GET("stories")
    suspend fun allStoriesWithLocation(
        @Header("Authorization") authorization: String,
        @Query("location") includeLocation: Int = 1
    ): ResponseStories

    @Multipart
    @POST("stories")
    suspend fun uploadStory(
        @Header("Authorization") authorization: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") latitude: RequestBody,
        @Part("lon") longitude: RequestBody,
    ): ResponseUploadStory
}