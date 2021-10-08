package proto.android.stormy.core.model.preferences

import android.content.Context
import bogdandonduk.commontoolboxlib.CommonToolbox

interface PreferencesManager {
    val KEY_LAST_ITEM_ID: String
    val DEFAULT_ITEM_ID: Long
    val KEY_LATEST_TIMESTAMP: String
    val DEFAULT_LATEST_TIMESTAMP: Long
    val KEY_LAST_SELECTED_DAY_INDEX: String
    val DEFAULT_SELECTED_DAY_INDEX: Int

    fun getLastItemId(context: Context) : Long

    fun setLastItemId(context: Context, id: Long)

    fun deleteLastItemId(context: Context)

    fun getLatestTimestamp(context: Context) : Long

    fun setLatestTimestamp(context: Context, timestamp: Long)

    fun deleteLatestTimestamp(context: Context)

    fun getLastSelectedDayIndex(context: Context) : Int

    fun setLastSelectedDayIndex(context: Context, dayIndex: Int)

    fun deleteLastSelectedDayIndex(context: Context)
}