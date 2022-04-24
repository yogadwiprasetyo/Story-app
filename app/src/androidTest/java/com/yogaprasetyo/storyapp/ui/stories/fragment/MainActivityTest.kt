package com.yogaprasetyo.storyapp.ui.stories.fragment

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.yogaprasetyo.storyapp.R
import com.yogaprasetyo.storyapp.ui.stories.DetailStoryActivity
import com.yogaprasetyo.storyapp.ui.stories.NewStoryActivity
import com.yogaprasetyo.storyapp.ui.stories.maps.UserStoryLocationActivity
import com.yogaprasetyo.storyapp.util.EspressoIdlingResource
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class MainActivityTest {

    @get:Rule
    val activityTest = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setup() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun teardown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun loadAllStories() {
        onView(withId(R.id.rv_stories)).check(matches(isDisplayed()))
        onView(withId(R.id.rv_stories)).perform(
            RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(5)
        )
    }

    @Test
    fun loadDetailStory() {
        Intents.init()

        onView(withId(R.id.rv_stories)).check(matches(isDisplayed()))
        onView(withId(R.id.rv_stories)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click())
        )

        intended(hasComponent(DetailStoryActivity::class.java.name))
        onView(withId(R.id.iv_story)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_story_name)).check(matches(isDisplayed()))
    }

    @Test
    fun loadLocationUserStories() {
        Intents.init()

        onView(withId(R.id.fb_location_story)).check(matches(isDisplayed()))
        onView(withId(R.id.fb_location_story)).perform(click())

        intended(hasComponent(UserStoryLocationActivity::class.java.name))
        onView(withId(R.id.map)).check(matches(isDisplayed()))

        onView(withId(R.id.efb_list_story)).check(matches(isDisplayed()))
        onView(withId(R.id.efb_list_story)).perform(click())

        intended(hasComponent(MainActivity::class.java.name))
        onView(withId(R.id.rv_stories)).check(matches(isDisplayed()))

        Intents.release()
    }

    @Test
    fun loadNewStories() {
        Intents.init()

        onView(withId(R.id.fb_add_story)).check(matches(isDisplayed()))
        onView(withId(R.id.fb_add_story)).perform(click())

        intended(hasComponent(NewStoryActivity::class.java.name))
        onView(withId(R.id.iv_preview)).check(matches(isDisplayed()))

        Intents.release()
    }
}