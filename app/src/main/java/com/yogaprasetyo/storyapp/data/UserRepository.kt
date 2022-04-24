package com.yogaprasetyo.storyapp.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.google.android.gms.maps.model.LatLng
import com.yogaprasetyo.storyapp.data.local.UserPreferences
import com.yogaprasetyo.storyapp.data.remote.response.*
import com.yogaprasetyo.storyapp.data.remote.retrofit.ApiService
import com.yogaprasetyo.storyapp.model.UserDataStoreModel
import com.yogaprasetyo.storyapp.model.UserLoginModel
import com.yogaprasetyo.storyapp.model.UserRegisterModel
import com.yogaprasetyo.storyapp.util.wrapEspressoIdlingResource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class UserRepository(private val apiService: ApiService) {

    /**
     * NETWORK SOURCE
     *
     * POST /register
     *
     * Create a new user to server
     * */
    fun createUser(user: UserRegisterModel): Flow<ResponseRegister> = flow {
        emit(ResponseRegister(false, ""))
        wrapEspressoIdlingResource {
            try {
                val response = apiService.createUser(user)
                emit(response)
            } catch (e: Exception) {
                emit(ResponseRegister(true, e.message.toString()))
            }
        }
    }

    /**
     * NETWORK SOURCE
     *
     * POST /login
     *
     * Authenticate user to server then
     * save token and state login to dataStore
     * */
    fun authenticate(user: UserLoginModel, pref: UserPreferences): Flow<ResponseLogin> = flow {
        emit(ResponseLogin(null, false, ""))
        wrapEspressoIdlingResource {
            try {
                val response = apiService.authenticate(user)
                val userPreference = UserDataStoreModel(
                    token = response.loginResult?.token as String,
                    isLogin = true
                )
                pref.saveUserPreference(userPreference)
                emit(response)
            } catch (e: Exception) {
                emit(ResponseLogin(null, true, e.message.toString()))
            }
        }
    }

    /**
     * NETWORK SOURCE
     *
     * GET /stories?page={n}&size={10}
     * n = number
     *
     * Retrieve all stories from first page until last
     * with per page have 10 data using Paging library
     * */
    fun getAllStories(token: String): LiveData<PagingData<ListStoryItem>> {
        return wrapEspressoIdlingResource {
            Pager(
                config = PagingConfig(pageSize = 10),
                pagingSourceFactory = { StoryPagingSource(apiService, setupToken(token)) }
            ).liveData
        }
    }

    /**
     * NETWORK SOURCE
     *
     * GET /stories?location={0|1}
     * 0 = not include | 1 = include
     *
     * Retrieve all stories with latitude & longitude from server
     * */
    fun getAllStoriesWithLocation(token: String): LiveData<ResponseStories> = liveData {
        emit(ResponseStories(emptyList(), false, ""))
        wrapEspressoIdlingResource {
            try {
                val bearerToken = setupToken(token)
                val response = apiService.allStoriesWithLocation(bearerToken)
                emit(response)
            } catch (e: Exception) {
                emit(ResponseStories(emptyList(), true, e.message.toString()))
            }
        }
    }

    /**
     * NETWORK SOURCE
     *
     * POST /stories
     *
     * Add new story to the server
     * file: File, description: String, location: LatLng
     * */
    fun uploadStory(
        token: String,
        file: File,
        description: String,
        location: LatLng
    ): Flow<ResponseUploadStory> =
        flow {
            emit(ResponseUploadStory(false, ""))
            wrapEspressoIdlingResource {
                try {
                    // Prepare token, request body image file and description
                    val bearerToken = setupToken(token)
                    val requestBodyDesc = setupRequestBody(description)
                    val requestBodyLat = setupRequestBody(location.latitude.toString())
                    val requestBodyLon = setupRequestBody(location.longitude.toString())
                    val imageMultipart = fileToMultipart(file)

                    val response = apiService.uploadStory(
                        bearerToken,
                        imageMultipart,
                        requestBodyDesc,
                        requestBodyLat,
                        requestBodyLon
                    )
                    emit(response)
                } catch (e: Exception) {
                    emit(ResponseUploadStory(true, e.message.toString()))
                }
            }
        }

    /**
     * Add prefix "Bearer" to token
     * */
    fun setupToken(token: String): String = "Bearer $token"

    /**
     * Convert String to Request body
     * */
    fun setupRequestBody(request: String) = request.toRequestBody("text/plain".toMediaType())

    /**
     * Creating Multipart Body for image file
     * */
    fun fileToMultipart(file: File): MultipartBody.Part {
        val requestImageFile = file.asRequestBody("image/jpeg".toMediaType())
        return MultipartBody.Part.createFormData(
            "photo",
            file.name,
            requestImageFile
        )
    }

    /**
     * Singleton pattern, only once created
     * */
    companion object {
        @Volatile
        private var instance: UserRepository? = null

        fun getInstance(apiService: ApiService): UserRepository {
            return instance ?: synchronized(this) {
                val userRepo = UserRepository(apiService)
                instance = userRepo
                userRepo
            }
        }
    }
}