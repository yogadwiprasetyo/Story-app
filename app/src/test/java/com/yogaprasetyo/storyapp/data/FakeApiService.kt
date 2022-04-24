package com.yogaprasetyo.storyapp.data

import com.yogaprasetyo.storyapp.data.remote.response.ResponseLogin
import com.yogaprasetyo.storyapp.data.remote.response.ResponseRegister
import com.yogaprasetyo.storyapp.data.remote.response.ResponseStories
import com.yogaprasetyo.storyapp.data.remote.response.ResponseUploadStory
import com.yogaprasetyo.storyapp.data.remote.retrofit.ApiService
import com.yogaprasetyo.storyapp.model.UserLoginModel
import com.yogaprasetyo.storyapp.model.UserRegisterModel
import com.yogaprasetyo.storyapp.util.DataDummy.dummySuccessAllStoryLocation
import com.yogaprasetyo.storyapp.util.DataDummy.dummySuccessLogin
import com.yogaprasetyo.storyapp.util.DataDummy.dummySuccessRegister
import com.yogaprasetyo.storyapp.util.DataDummy.dummySuccessUpload
import okhttp3.MultipartBody
import okhttp3.RequestBody

class FakeApiService : ApiService {
    override suspend fun createUser(user: UserRegisterModel): ResponseRegister {
        return dummySuccessRegister
    }

    override suspend fun authenticate(user: UserLoginModel): ResponseLogin {
        return dummySuccessLogin
    }

    override suspend fun allStories(authorization: String, page: Int, size: Int): ResponseStories {
        return dummySuccessAllStoryLocation
    }

    override suspend fun allStoriesWithLocation(
        authorization: String,
        includeLocation: Int
    ): ResponseStories {
        return dummySuccessAllStoryLocation
    }

    override suspend fun uploadStory(
        authorization: String,
        file: MultipartBody.Part,
        description: RequestBody,
        latitude: RequestBody,
        longitude: RequestBody
    ): ResponseUploadStory {
        return dummySuccessUpload
    }
}