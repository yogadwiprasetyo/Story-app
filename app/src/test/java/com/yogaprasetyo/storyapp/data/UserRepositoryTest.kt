package com.yogaprasetyo.storyapp.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.yogaprasetyo.storyapp.MainCoroutineRule
import com.yogaprasetyo.storyapp.util.DataDummy.dummyBearerToken
import com.yogaprasetyo.storyapp.util.DataDummy.dummyDescription
import com.yogaprasetyo.storyapp.util.DataDummy.dummyFile
import com.yogaprasetyo.storyapp.util.DataDummy.dummyPage
import com.yogaprasetyo.storyapp.util.DataDummy.dummyRequestDescriptionBody
import com.yogaprasetyo.storyapp.util.DataDummy.dummyRequestImageMultipart
import com.yogaprasetyo.storyapp.util.DataDummy.dummyRequestLatBody
import com.yogaprasetyo.storyapp.util.DataDummy.dummyRequestLogin
import com.yogaprasetyo.storyapp.util.DataDummy.dummyRequestLongBody
import com.yogaprasetyo.storyapp.util.DataDummy.dummyRequestRegister
import com.yogaprasetyo.storyapp.util.DataDummy.dummySize
import com.yogaprasetyo.storyapp.util.DataDummy.dummySuccessAllStoryLocation
import com.yogaprasetyo.storyapp.util.DataDummy.dummySuccessLogin
import com.yogaprasetyo.storyapp.util.DataDummy.dummySuccessRegister
import com.yogaprasetyo.storyapp.util.DataDummy.dummySuccessUpload
import com.yogaprasetyo.storyapp.util.DataDummy.dummyToken
import com.yogaprasetyo.storyapp.util.DataDummy.shouldEquals
import com.yogaprasetyo.storyapp.util.DataDummy.shouldFalse
import com.yogaprasetyo.storyapp.util.DataDummy.shouldNotEmpty
import com.yogaprasetyo.storyapp.util.DataDummy.shouldNotNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class UserRepositoryTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var apiService: FakeApiService
    private lateinit var userRepository: UserRepository

    @Before
    fun setup() {
        apiService = FakeApiService()
        userRepository = UserRepository(apiService)
    }

    @Test
    fun `when createUser Should Error is False`() = mainCoroutineRule.runBlockingTest {
        val expectedResponse = dummySuccessRegister
        val actualResponse = apiService.createUser(dummyRequestRegister)

        assertFalse(shouldFalse, actualResponse.error)
        assertEquals(shouldEquals, expectedResponse.message, actualResponse.message)
    }

    @Test
    fun `when authenticate Should Not Null`() = mainCoroutineRule.runBlockingTest {
        val expectedResponse = dummySuccessLogin
        val actualResponse = apiService.authenticate(dummyRequestLogin)

        assertFalse(shouldFalse, actualResponse.error)
        assertNotNull(shouldNotNull, actualResponse.loginResult)
        assertEquals(
            shouldEquals,
            expectedResponse.loginResult?.name,
            actualResponse.loginResult?.name
        )
    }

    @Test
    fun `when Get allStories Should Not Empty`() = mainCoroutineRule.runBlockingTest {
        val expectedResponse = dummySuccessAllStoryLocation
        val actualResponse = apiService.allStories(dummyToken, dummyPage, dummySize)

        assertFalse(shouldFalse, actualResponse.error)
        assertTrue(shouldNotEmpty, actualResponse.listStory.isNotEmpty())
        assertEquals(shouldEquals, expectedResponse.listStory.size, actualResponse.listStory.size)
    }

    @Test
    fun `when Get allStoriesWithLocation Should Not Empty`() = mainCoroutineRule.runBlockingTest {
        val expectedResponse = dummySuccessAllStoryLocation
        val actualResponse = apiService.allStoriesWithLocation(dummyToken)

        assertFalse(shouldFalse, actualResponse.error)
        assertTrue(shouldNotEmpty, actualResponse.listStory.isNotEmpty())
        assertEquals(shouldEquals, expectedResponse.listStory.size, actualResponse.listStory.size)
    }

    @Test
    fun `when uploadStory Should Error is False`() = mainCoroutineRule.runBlockingTest {
        val expectedResponse = dummySuccessUpload
        val actualResponse = apiService.uploadStory(
            dummyToken,
            dummyRequestImageMultipart,
            dummyRequestDescriptionBody,
            dummyRequestLatBody,
            dummyRequestLongBody
        )

        assertFalse(shouldFalse, actualResponse.error)
        assertEquals(shouldEquals, expectedResponse.message, actualResponse.message)
    }

    @Test
    fun `when setupToken Should Have Bearer Prefix`() {
        val expectedResult = dummyBearerToken
        val actualResult = userRepository.setupToken(dummyToken)

        assertEquals(shouldEquals, expectedResult, actualResult)
    }

    @Test
    fun `when setupBodyRequest Should Return Request Body Type`() {
        val expectedResult = dummyRequestDescriptionBody
        val actualResult = userRepository.setupRequestBody(dummyDescription)

        assertEquals(shouldEquals, expectedResult.contentType(), actualResult.contentType())
    }

    @Test
    fun `when fileToMultipart Should Change Type to MultiPartBody Image`() {
        val expectedResult = dummyRequestImageMultipart
        val actualResult = userRepository.fileToMultipart(dummyFile)

        assertEquals(
            shouldEquals,
            expectedResult.body.contentType(),
            actualResult.body.contentType()
        )
    }
}