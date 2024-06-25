package br.com.marcottc.weatherpeek.view.activity

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToLastPosition
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.hasMinimumChildCount
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import br.com.marcottc.weatherpeek.R
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @Rule
    @JvmField
    var mActivityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    @Rule
    @JvmField
    var mGrantPermissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(
            "android.permission.ACCESS_FINE_LOCATION",
            "android.permission.ACCESS_COARSE_LOCATION"
        )

    private val uiAutomation = InstrumentationRegistry.getInstrumentation().uiAutomation

    private lateinit var requestIdlingResource: IdlingResource

    @Before
    fun setup() {
        uiAutomation.executeShellCommand("svc wifi enable")
        uiAutomation.executeShellCommand("svc data enable")

        mActivityScenarioRule.scenario.onActivity { mainActivity ->
            requestIdlingResource = mainActivity.requestingWeatherDataIdlingResource
        }
        IdlingRegistry.getInstance().register(requestIdlingResource)
        Intents.init()

    }

    @Test
    fun mainActivity_isRequestingWeatherData() {
        IdlingRegistry.getInstance().unregister(requestIdlingResource)

        onView(withId(R.id.current_weather_card_loading_symbol)).check(matches(isDisplayed()))
        onView(withId(R.id.wind_card_loading_symbol)).check(matches(isDisplayed()))
        onView(withId(R.id.precipitation_card_loading_symbol)).check(matches(isDisplayed()))
        onView(withId(R.id.forecast_loading_symbol)).check(matches(isDisplayed()))

        onView(withId(R.id.temperature_value)).check(matches(not(isDisplayed())))
        onView(withId(R.id.wind_speed_amount)).check(matches(not(isDisplayed())))
        onView(withId(R.id.precipitation_amount)).check(matches(not(isDisplayed())))

        onView(withId(R.id.current_weather_reload_icon)).check(matches(not(isDisplayed())))
        onView(withId(R.id.current_weather_reload_text)).check(matches(not(isDisplayed())))
        onView(withId(R.id.wind_card_reload_icon)).check(matches(not(isDisplayed())))
        onView(withId(R.id.precipitation_card_reload_icon)).check(matches(not(isDisplayed())))
        onView(withId(R.id.forecast_reload_icon)).check(matches(not(isDisplayed())))
        onView(withId(R.id.forecast_reload_text)).check(matches(not(isDisplayed())))
    }

    @Test
    fun mainActivity_hasRequestedWeatherData() {
        onView(withId(R.id.current_weather_card_loading_symbol)).check(matches(not(isDisplayed())))
        onView(withId(R.id.wind_card_loading_symbol)).check(matches(not(isDisplayed())))
        onView(withId(R.id.precipitation_card_loading_symbol)).check(matches(not(isDisplayed())))
        onView(withId(R.id.forecast_loading_symbol)).check(matches(not(isDisplayed())))

        onView(withId(R.id.temperature_value)).check(matches(isDisplayed()))
        onView(withId(R.id.wind_speed_amount)).check(matches(isDisplayed()))
        onView(withId(R.id.precipitation_amount)).check(matches(isDisplayed()))

        onView(withId(R.id.current_weather_reload_icon)).check(matches(not(isDisplayed())))
        onView(withId(R.id.current_weather_reload_text)).check(matches(not(isDisplayed())))
        onView(withId(R.id.wind_card_reload_icon)).check(matches(not(isDisplayed())))
        onView(withId(R.id.precipitation_card_reload_icon)).check(matches(not(isDisplayed())))
        onView(withId(R.id.forecast_reload_icon)).check(matches(not(isDisplayed())))
        onView(withId(R.id.forecast_reload_text)).check(matches(not(isDisplayed())))

        onView(withId(R.id.forecast_recycler_view)).check(matches(hasMinimumChildCount(5)))
        onView(withId(R.id.forecast_recycler_view)).perform(scrollToLastPosition<RecyclerView.ViewHolder>())
        onView(withId(R.id.forecast_recycler_view)).perform(scrollToPosition<RecyclerView.ViewHolder>(0))
    }

    @Test
    fun mainActivity_navigateToWeatherActivity() {
        onView(withId(R.id.fab)).perform(click())

        intended(hasComponent(WeatherActivity::class.java.name))
    }

    @After
    fun cleanup() {
        IdlingRegistry.getInstance().unregister(requestIdlingResource)
        Intents.release()
    }

}