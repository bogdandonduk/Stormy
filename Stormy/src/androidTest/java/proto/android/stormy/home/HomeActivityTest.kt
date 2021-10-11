package proto.android.stormy.home

import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import bogdandonduk.appbartoolboxandroidlib.appbar.AppBarToolbox
import org.hamcrest.core.IsNull.notNullValue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import proto.android.stormy.R
import proto.android.stormy.tools.DirectViewMatcher
import proto.android.stormy.tools.TestToolbox

@RunWith(AndroidJUnit4::class)
class HomeActivityTest {
    @get:Rule val activityRule = ActivityTestRule(HomeActivity::class.java)

    @Test
    fun searchHomeAsUpIndicatorClickOpensCitySearchActivity() {
        TestToolbox.performViewClick(activityRule.activity.homeAsUpIndicatorView!!, false)

        onView(withId(R.id.activity_city_picker_search_edit_text)).check(matches(notNullValue()))
    }

    @Test
    fun radarOptionsMenuItemClickOpensRadarImageActivity() {
        TestToolbox.performViewClick(AppBarToolbox.getOptionsMenuItemAsView(activityRule.activity.radarOptionsMenuItemId, activityRule.activity.viewBinding.activityHomeToolbar)!!, false)

        onView(withId(R.id.activity_radar_image_toolbar)).check(matches(notNullValue()))
    }

    @Test
    fun indefiniteProgressBarDisplayedWhileWaitingForFetchedWeatherContent() {
        DirectViewMatcher(activityRule.activity.viewBinding.activityHomeFetchingProgressBarContainerLinearLayout).let { matcher ->
            onView(matcher).apply {
                matcher.matching = { candidate, _ ->
                    candidate.visibility == View.VISIBLE
                }
            }.check(matches(matcher))
        }
    }
}