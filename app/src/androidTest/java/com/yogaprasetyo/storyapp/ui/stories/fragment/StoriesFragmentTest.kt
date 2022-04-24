package com.yogaprasetyo.storyapp.ui.stories.fragment

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.yogaprasetyo.storyapp.R
import com.yogaprasetyo.storyapp.data.remote.retrofit.ApiConfig
import com.yogaprasetyo.storyapp.ui.stories.JsonConverter
import com.yogaprasetyo.storyapp.util.EspressoIdlingResource
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@MediumTest
class StoriesFragmentTest {

    private val mockWebServer = MockWebServer()
    private val expectedResponseStoryAuthor = "tubagus"
    private val expectedResponseDescription = "444444"

    @Before
    fun setup() {
        mockWebServer.start(8080)
        ApiConfig.baseUrl = "http://127.0.0.1:8080/"
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun loadStories_Success() {
        launchFragmentInContainer<StoriesFragment>(themeResId = R.style.Theme_StoryApp)

        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody(JsonConverter.readStringFromFile("success_response.json"))
        mockWebServer.enqueue(mockResponse)

        onView(withId(R.id.rv_stories)).check(matches(isDisplayed()))

        onView(withText(expectedResponseStoryAuthor)).check(matches(isDisplayed()))
        onView(withId(R.id.rv_stories)).perform(
            RecyclerViewActions.scrollTo<RecyclerView.ViewHolder>(
                hasDescendant(withText(expectedResponseDescription))
            )
        )
    }

    @Test
    fun loadStories_Error() {
        launchFragmentInContainer<StoriesFragment>(themeResId = R.style.Theme_StoryApp)

        val mockResponse = MockResponse().setResponseCode(500)
        mockWebServer.enqueue(mockResponse)

        onView(withId(R.id.iv_empty_story)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_empty)).check(matches(isDisplayed()))
        onView(withText(R.string.empty_stories)).check(matches(isDisplayed()))
    }
}