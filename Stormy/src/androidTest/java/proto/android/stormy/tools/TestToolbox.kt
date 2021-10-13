package proto.android.stormy.tools

import android.view.View
import androidx.annotation.IdRes
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import androidx.test.espresso.matcher.ViewMatchers.withId
import org.hamcrest.core.AllOf.allOf
import org.hamcrest.core.Is.`is`
import org.hamcrest.core.IsInstanceOf.instanceOf

object TestToolbox {
    fun performViewClick(view: View, longClick: Boolean = false) {
        Espresso.onView(DirectViewMatcher(view))
            .perform(if (!longClick) ViewActions.click() else ViewActions.longClick())
    }

    fun performViewClickFoundById(id: Int, longClick: Boolean = false) {
        Espresso.onView(withId(id))
            .perform(if (!longClick) ViewActions.click() else ViewActions.longClick())
    }

    fun performViewClickFoundByContentDescription(@IdRes contentDescription: Int, longClick: Boolean = false) {
        Espresso.onView(withContentDescription(contentDescription))
            .perform(if (!longClick) ViewActions.click() else ViewActions.longClick())
    }

    fun <T> performViewClickFoundByData(data: T, dataClass: Class<T>, longClick: Boolean = false) {
            Espresso.onData(allOf(`is`(instanceOf(dataClass::class.java)), `is`(data))).perform(if (!longClick) ViewActions.click() else ViewActions.longClick())
    }
}