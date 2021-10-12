package proto.android.stormy.tools

import android.view.View
import androidx.test.espresso.matcher.BoundedMatcher
import org.hamcrest.Description

class DirectViewMatcher<T : View>(var view: T, var name: String? = null, var matching: ((candidateItem: T, view: T) -> Boolean)? = null) : BoundedMatcher<View, T>(view::class.java) {
    override fun describeTo(description: Description?) {
        description?.appendText("not the target view")
    }

    override fun matchesSafely(item: T) = if(matching == null) item == view else item == view && matching!!.invoke(item, view)
}