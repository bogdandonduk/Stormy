package proto.android.stormy.tools

import android.view.View
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions

object TestToolbox {
    fun performViewClick(view: View, longClick: Boolean = false) {
        Espresso.onView(DirectViewMatcher(view))
            .perform(if(!longClick) ViewActions.click() else ViewActions.longClick())
    }
}