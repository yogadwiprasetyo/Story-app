package com.yogaprasetyo.storyapp.ui.auth

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.yogaprasetyo.storyapp.R
import com.yogaprasetyo.storyapp.util.EspressoIdlingResource
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class RegisterActivityTest {
    @get:Rule
    val activityTest = ActivityScenarioRule(RegisterActivity::class.java)

    private val resources = ApplicationProvider.getApplicationContext<Context>().resources
    private val errorEmpty = resources.getString(R.string.input_error_empty)
    private val errorRequirementEmail = resources.getString(R.string.input_error_email)
    private val errorRequirementPassword = resources.getString(R.string.input_error_password)
    private val realName = "host"
    private val realEmail = "h@gmail.com"
    private val dummyEmail = "yup"
    private val dummyPassword = "123"

    @Before
    fun setup() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun teardown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun onFieldEmpty_Testing() {
        onView(withId(R.id.btn_register)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_register)).perform(click())
        onView(withId(R.id.et_name)).check(matches(hasErrorText(errorEmpty)))

        onView(withId(R.id.et_name)).perform(typeText(realName), closeSoftKeyboard())

        onView(withId(R.id.btn_register)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_register)).perform(click())
        onView(withId(R.id.cv_email)).check(matches(hasErrorText(errorEmpty)))

        onView(withId(R.id.cv_email)).perform(typeText(realEmail), closeSoftKeyboard())

        onView(withId(R.id.btn_register)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_register)).perform(click())
        onView(withId(R.id.cv_password)).check(matches(hasErrorText(errorEmpty)))
    }

    @Test
    fun onFieldNotMatchRequirement_Testing() {
        onView(withId(R.id.cv_email)).check(matches(isDisplayed()))
        onView(withId(R.id.cv_email)).perform(typeText(dummyEmail), closeSoftKeyboard())

        onView(withId(R.id.cv_email)).check(matches(hasErrorText(errorRequirementEmail)))

        onView(withId(R.id.cv_password)).check(matches(isDisplayed()))
        onView(withId(R.id.cv_password)).perform(typeText(dummyPassword), closeSoftKeyboard())

        onView(withId(R.id.cv_password)).check(matches(hasErrorText(errorRequirementPassword)))
    }
}