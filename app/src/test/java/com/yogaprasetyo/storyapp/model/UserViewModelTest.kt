package com.yogaprasetyo.storyapp.model

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.yogaprasetyo.storyapp.MainCoroutineRule
import com.yogaprasetyo.storyapp.data.UserRepository
import com.yogaprasetyo.storyapp.data.local.UserPreferences
import com.yogaprasetyo.storyapp.data.remote.response.ListStoryItem
import com.yogaprasetyo.storyapp.data.remote.response.ResponseStories
import com.yogaprasetyo.storyapp.getOrAwaitValue
import com.yogaprasetyo.storyapp.ui.stories.adapter.StoryAdapter
import com.yogaprasetyo.storyapp.util.DataDummy.dummyAvailableDatastore
import com.yogaprasetyo.storyapp.util.DataDummy.dummyDescription
import com.yogaprasetyo.storyapp.util.DataDummy.dummyEmptyDatastore
import com.yogaprasetyo.storyapp.util.DataDummy.dummyFailedAllStoryLocation
import com.yogaprasetyo.storyapp.util.DataDummy.dummyFailedLogin
import com.yogaprasetyo.storyapp.util.DataDummy.dummyFailedRegister
import com.yogaprasetyo.storyapp.util.DataDummy.dummyFailedUpload
import com.yogaprasetyo.storyapp.util.DataDummy.dummyFile
import com.yogaprasetyo.storyapp.util.DataDummy.dummyListStory
import com.yogaprasetyo.storyapp.util.DataDummy.dummyLocation
import com.yogaprasetyo.storyapp.util.DataDummy.dummyRequestLogin
import com.yogaprasetyo.storyapp.util.DataDummy.dummyRequestRegister
import com.yogaprasetyo.storyapp.util.DataDummy.dummySuccessAllStoryLocation
import com.yogaprasetyo.storyapp.util.DataDummy.dummySuccessLogin
import com.yogaprasetyo.storyapp.util.DataDummy.dummySuccessRegister
import com.yogaprasetyo.storyapp.util.DataDummy.dummySuccessUpload
import com.yogaprasetyo.storyapp.util.DataDummy.dummyToken
import com.yogaprasetyo.storyapp.util.DataDummy.shouldEmpty
import com.yogaprasetyo.storyapp.util.DataDummy.shouldEquals
import com.yogaprasetyo.storyapp.util.DataDummy.shouldFalse
import com.yogaprasetyo.storyapp.util.DataDummy.shouldNotEmpty
import com.yogaprasetyo.storyapp.util.DataDummy.shouldNotNull
import com.yogaprasetyo.storyapp.util.DataDummy.shouldNull
import com.yogaprasetyo.storyapp.util.DataDummy.shouldTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class UserViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Mock
    private lateinit var userRepository: UserRepository

    @Mock
    private lateinit var preference: UserPreferences

    private lateinit var userViewModel: UserViewModel

    @Before
    fun setup() {
        userViewModel = UserViewModel(userRepository)
    }

    @Test
    fun `when Success Get All Stories with Location Should Not Empty List and Error is False`() {
        val expectedResponse = MutableLiveData<ResponseStories>()
        expectedResponse.value = dummySuccessAllStoryLocation

        `when`(userViewModel.loadAllStoriesWithLocation(dummyToken)).thenReturn(expectedResponse)
        val actualResponse = userViewModel.loadAllStoriesWithLocation(dummyToken).getOrAwaitValue()

        assertFalse(shouldFalse, actualResponse.error)
        assertFalse(shouldNotNull, actualResponse.listStory.isNullOrEmpty())
        assertEquals(
            shouldEquals,
            dummySuccessAllStoryLocation.listStory.size,
            actualResponse.listStory.size
        )
    }

    @Test
    fun `when Failed Get All Stories with Location Should Return Empty List and Error is True`() {
        val expectedResponse = MutableLiveData<ResponseStories>()
        expectedResponse.value = dummyFailedAllStoryLocation

        `when`(userViewModel.loadAllStoriesWithLocation("token")).thenReturn(expectedResponse)
        val actualResponse = userViewModel.loadAllStoriesWithLocation("token").getOrAwaitValue()

        assertTrue(shouldTrue, actualResponse.error)
        assertTrue(shouldNull, actualResponse.listStory.isNullOrEmpty())
    }

    @Test
    fun `when Register is Success Should Return Error False`() {
        val newUser = dummyRequestRegister
        val expectedResponse = flowOf(dummySuccessRegister)

        `when`(userRepository.createUser(newUser)).thenReturn(expectedResponse)
        userViewModel.register(newUser)
        val actualResponse = userViewModel.responseRegister.getOrAwaitValue()

        assertFalse(shouldFalse, actualResponse.error)
    }

    @Test
    fun `when Register is Fail Should Return Error True`() {
        val newUser = dummyRequestRegister
        val expectedResponse = flowOf(dummyFailedRegister)

        `when`(userRepository.createUser(newUser)).thenReturn(expectedResponse)
        userViewModel.register(newUser)
        val actualResponse = userViewModel.responseRegister.getOrAwaitValue()

        assertTrue(shouldTrue, actualResponse.error)
    }

    @Test
    fun `when Upload is Success Should Return Error False`() {
        val expectedResponse = flowOf(dummySuccessUpload)

        `when`(
            userRepository.uploadStory(
                dummyToken,
                dummyFile,
                dummyDescription,
                dummyLocation
            )
        ).thenReturn(
            expectedResponse
        )
        userViewModel.uploadStory(dummyToken, dummyFile, dummyDescription, dummyLocation)
        val actualResponse = userViewModel.responseStory.getOrAwaitValue()

        assertFalse(shouldFalse, actualResponse.error)
    }

    @Test
    fun `when Upload is Failed Should Return Error True`() {
        val expectedResponse = flowOf(dummyFailedUpload)

        `when`(
            userRepository.uploadStory(
                dummyToken,
                dummyFile,
                dummyDescription,
                dummyLocation
            )
        ).thenReturn(
            expectedResponse
        )
        userViewModel.uploadStory(dummyToken, dummyFile, dummyDescription, dummyLocation)
        val actualResponse = userViewModel.responseStory.getOrAwaitValue()

        assertTrue(shouldTrue, actualResponse.error)
    }

    @Test
    fun `when Login is Success Should Return Error False and Result Not Null`() {
        val newUser = dummyRequestLogin
        val expectedResponse = flowOf(dummySuccessLogin)

        `when`(userRepository.authenticate(newUser, preference)).thenReturn(expectedResponse)
        userViewModel.login(newUser, preference)
        val actualResponse = userViewModel.responseLogin.getOrAwaitValue()

        assertFalse(shouldFalse, actualResponse.error)
        assertNotNull(shouldNotNull, actualResponse.loginResult)
    }

    @Test
    fun `when Login is Failed Should Return Error True and Result Null`() {
        val newUser = dummyRequestLogin
        val expectedResponse = flowOf(dummyFailedLogin)

        `when`(userRepository.authenticate(newUser, preference)).thenReturn(expectedResponse)
        userViewModel.login(newUser, preference)
        val actualResponse = userViewModel.responseLogin.getOrAwaitValue()

        assertTrue(shouldTrue, actualResponse.error)
        assertNull(shouldNull, actualResponse.loginResult)
    }

    @Test
    fun `when Logout is Called Should Invoke Remove Preference Function`() =
        mainCoroutineRule.dispatcher.runBlockingTest {
            userViewModel.logout(preference)
            verify(preference).removeUserPreference()
        }

    @Test
    fun `when DataStore Available Should Return Token and State Login True`() {
        val expectedResponse = flowOf(dummyAvailableDatastore)

        `when`(preference.loadDataStore()).thenReturn(expectedResponse)
        val actualResponse = userViewModel.loadPreferences(preference).getOrAwaitValue()

        assertTrue(shouldNotEmpty, actualResponse.token.isNotEmpty())
        assertTrue(shouldTrue, actualResponse.isLogin)
    }

    @Test
    fun `when DataStore Not Available Should Return Empty Token and State Login False`() {
        val expectedResponse = flowOf(dummyEmptyDatastore)

        `when`(preference.loadDataStore()).thenReturn(expectedResponse)
        val actualResponse = userViewModel.loadPreferences(preference).getOrAwaitValue()

        assertTrue(shouldEmpty, actualResponse.token.isEmpty())
        assertFalse(shouldFalse, actualResponse.isLogin)
    }

    @Test
    fun `when Success Get List Story Paging Should Not Null`() = mainCoroutineRule.runBlockingTest {
        val dummyListStory = dummyListStory
        val data = PagedTestDataSource.snapshot(dummyListStory)
        val storiesLiveData = MutableLiveData<PagingData<ListStoryItem>>()
        storiesLiveData.value = data

        `when`(userRepository.getAllStories("token")).thenReturn(storiesLiveData)
        val actualStories: PagingData<ListStoryItem> =
            userViewModel.loadAllStories("token").getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.mDiffCallback,
            updateCallback = noopListUpdateCallback,
            mainDispatcher = mainCoroutineRule.dispatcher,
            workerDispatcher = mainCoroutineRule.dispatcher
        )
        differ.submitData(actualStories)

        advanceUntilIdle()

        assertNotNull(shouldNotNull, differ.snapshot())
        assertEquals(shouldEquals, dummyListStory.size, differ.snapshot().size)
        assertEquals(shouldEquals, dummyListStory[0].id, differ.snapshot()[0]?.id)
    }

    @Test
    fun `when Failed Get List Story Paging Should Empty`() = mainCoroutineRule.runBlockingTest {
        val dummyListStory = emptyList<ListStoryItem>()
        val data = PagedTestDataSource.snapshot(dummyListStory)
        val storiesLiveData = MutableLiveData<PagingData<ListStoryItem>>()
        storiesLiveData.value = data

        `when`(userRepository.getAllStories("token")).thenReturn(storiesLiveData)
        val actualStories: PagingData<ListStoryItem> =
            userViewModel.loadAllStories("token").getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.mDiffCallback,
            updateCallback = noopListUpdateCallback,
            mainDispatcher = mainCoroutineRule.dispatcher,
            workerDispatcher = mainCoroutineRule.dispatcher
        )
        differ.submitData(actualStories)

        advanceUntilIdle()

        assertTrue(shouldEmpty, differ.snapshot().isEmpty())
        assertEquals(shouldEquals, dummyListStory.size, differ.snapshot().size)
    }
}

class PagedTestDataSource private constructor() :
    PagingSource<Int, LiveData<List<ListStoryItem>>>() {

    override fun getRefreshKey(state: PagingState<Int, LiveData<List<ListStoryItem>>>): Int {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<ListStoryItem>>> {
        return LoadResult.Page(emptyList(), 0, 1)
    }

    companion object {
        fun snapshot(items: List<ListStoryItem>): PagingData<ListStoryItem> {
            return PagingData.from(items)
        }
    }
}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}
