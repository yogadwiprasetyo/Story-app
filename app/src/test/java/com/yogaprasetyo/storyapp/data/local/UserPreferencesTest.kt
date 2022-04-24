package com.yogaprasetyo.storyapp.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.yogaprasetyo.storyapp.MainCoroutineRule
import com.yogaprasetyo.storyapp.util.DataDummy.dummyAvailableDatastore
import com.yogaprasetyo.storyapp.util.DataDummy.dummyEmptyDatastore
import com.yogaprasetyo.storyapp.util.DataDummy.shouldEmpty
import com.yogaprasetyo.storyapp.util.DataDummy.shouldEquals
import com.yogaprasetyo.storyapp.util.DataDummy.shouldFalse
import com.yogaprasetyo.storyapp.util.DataDummy.shouldNotEmpty
import com.yogaprasetyo.storyapp.util.DataDummy.shouldTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class UserPreferencesTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var userPreferences: UserPreferencesImpl

    @Before
    fun setup() {
        userPreferences = FakeUserPreferences()
    }

    @Test
    fun `when loadDatastore Available Should Return Token and Login State True`() =
        mainCoroutineRule.runBlockingTest {
            val expectedResult = dummyAvailableDatastore

            userPreferences.saveUserPreference(dummyAvailableDatastore)
            val actualResult = userPreferences.loadDatastore().first()

            assertTrue(shouldNotEmpty, actualResult.token.isNotEmpty())
            assertTrue(shouldTrue, actualResult.isLogin)
            assertEquals(shouldEquals, expectedResult.token, actualResult.token)
        }

    @Test
    fun `when loadDatastore Empty Should Return Empty Token and Login State False`() =
        mainCoroutineRule.runBlockingTest {
            val expectedResult = dummyEmptyDatastore

            userPreferences.removeUserPreference()
            val actualResult = userPreferences.loadDatastore().first()

            assertTrue(shouldEmpty, actualResult.token.isEmpty())
            assertFalse(shouldFalse, actualResult.isLogin)
            assertEquals(shouldEquals, expectedResult.token, actualResult.token)
        }

    @Test
    fun `when saveUserPreference Should Available Token and Login State True`() =
        mainCoroutineRule.runBlockingTest {
            userPreferences.saveUserPreference(dummyAvailableDatastore)
            val actualResult = userPreferences.loadDatastore().first()

            assertTrue(shouldNotEmpty, actualResult.token.isNotEmpty())
            assertTrue(shouldTrue, actualResult.isLogin)
        }

    @Test
    fun `when removeUserPreference Should Invoke Datastore Edit Function`() =
        mainCoroutineRule.runBlockingTest {
            userPreferences.removeUserPreference()
            val actualResult = userPreferences.loadDatastore().first()

            assertTrue(shouldEmpty, actualResult.token.isEmpty())
            assertFalse(shouldFalse, actualResult.isLogin)
        }
}