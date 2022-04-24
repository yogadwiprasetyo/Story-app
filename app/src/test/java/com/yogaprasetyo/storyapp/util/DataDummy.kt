package com.yogaprasetyo.storyapp.util

import com.google.android.gms.maps.model.LatLng
import com.yogaprasetyo.storyapp.data.remote.response.*
import com.yogaprasetyo.storyapp.model.UserDataStoreModel
import com.yogaprasetyo.storyapp.model.UserLoginModel
import com.yogaprasetyo.storyapp.model.UserRegisterModel
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

object DataDummy {

    /**
     * Dummy for request and parameter
     * */
    private const val dummyLat = 18.0
    private const val dummyLong = 130.0
    const val dummyToken = "token"
    const val dummyDescription = "description"
    const val dummyPage = 1
    const val dummySize = 10
    val dummyLocation = LatLng(dummyLat, dummyLong)
    val dummyFile = File("Hello")
    val dummyRequestRegister = UserRegisterModel("yoga", "y@gmail.com", "123456")
    val dummyRequestLogin = UserLoginModel("y@gmail.com", "123456")
    val dummyRequestImageMultipart = MultipartBody.Part.createFormData(
        name = "photo",
        filename = dummyFile.name,
        dummyFile.asRequestBody("image/jpeg".toMediaType())
    )
    val dummyRequestDescriptionBody = dummyDescription.toRequestBody("text/plain".toMediaType())
    val dummyRequestLatBody = dummyLat.toString().toRequestBody("text/plain".toMediaType())
    val dummyRequestLongBody = dummyLong.toString().toRequestBody("text/plain".toMediaType())


    /**
     * Dummy for response success and failed
     * */
    val dummySuccessAllStoryLocation = generateResponseStories(error = false)
    val dummyFailedAllStoryLocation = generateResponseStories(error = true)
    val dummySuccessRegister = generateResponseRegister(error = false)
    val dummyFailedRegister = generateResponseRegister(error = true)
    val dummySuccessLogin = generateResponseLogin(error = false)
    val dummyFailedLogin = generateResponseLogin(error = true)
    val dummySuccessUpload = generateResponseUploadStory(error = false)
    val dummyFailedUpload = generateResponseUploadStory(error = true)
    val dummyAvailableDatastore = generateResponseDataStore(isLogin = true)
    val dummyEmptyDatastore = generateResponseDataStore(isLogin = false)
    val dummyListStory = generateDummyStoriesWithLocation()
    const val dummyBearerToken = "Bearer $dummyToken"

    const val shouldFalse = "Should Return False"
    const val shouldTrue = "Should Return True"
    const val shouldEquals = "Should Return Equals"
    const val shouldNotNull = "Should Return Not Null"
    const val shouldNull = "Should Return Null"
    const val shouldNotEmpty = "Should Is Not Empty True"
    const val shouldEmpty = "Should Is Empty True"

    private fun generateResponseDataStore(isLogin: Boolean): UserDataStoreModel {
        return if (isLogin) {
            UserDataStoreModel(
                token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9",
                isLogin = true
            )
        } else {
            UserDataStoreModel(
                token = "",
                isLogin = false
            )
        }
    }

    private fun generateResponseUploadStory(error: Boolean): ResponseUploadStory {
        return if (!error) {
            ResponseUploadStory(error = false, message = "success")
        } else {
            ResponseUploadStory(error = true, message = "failed")
        }
    }

    private fun generateResponseLogin(error: Boolean): ResponseLogin {
        return if (!error) {
            ResponseLogin(
                error = false,
                message = "success",
                loginResult = LoginResult(
                    userId = "user-yj5pc_LARC_AgK61",
                    name = "Yoga Prasetyo",
                    token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9"
                )
            )
        } else {
            ResponseLogin(
                error = true,
                message = "failed",
                loginResult = null
            )
        }
    }

    private fun generateResponseRegister(error: Boolean): ResponseRegister {
        return if (!error) {
            ResponseRegister(false, "success")
        } else {
            ResponseRegister(true, "failed")
        }
    }

    private fun generateResponseStories(error: Boolean): ResponseStories {
        return if (!error) {
            ResponseStories(generateDummyStoriesWithLocation(), error, "success")
        } else {
            ResponseStories(emptyList(), error, "error")
        }
    }

    private fun generateDummyStoriesWithLocation(): ArrayList<ListStoryItem> {
        val listStoryItem = ArrayList<ListStoryItem>()
        for (i in 0..10) {
            val story = ListStoryItem(
                "https://story-api.dicoding.dev/images/stories/photos-1641623658595_dummy-pic.png",
                "2022-01-08T06:34:18.598Z",
                "Yoga",
                "Lorem ipsum $i",
                -10.212,
                "story-$i-FvU4u0Vp2S3PMsFg",
                -16.002
            )
            listStoryItem.add(story)
        }
        return listStoryItem
    }
}