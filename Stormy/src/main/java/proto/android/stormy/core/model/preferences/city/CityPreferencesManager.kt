package proto.android.stormy.core.model.preferences.city

import android.content.Context
import bogdandonduk.commontoolboxlib.CommonToolbox
import proto.android.stormy.core.model.preferences.PreferencesManager

class CityPreferencesManager : PreferencesManager {
    override val KEY_LAST_ITEM_ID = "last_city_id"
    override val DEFAULT_ITEM_ID =  4047906L
    override val KEY_LATEST_TIMESTAMP = "latest_timestamp"
    override val DEFAULT_LATEST_TIMESTAMP = 0L

    override val KEY_LAST_SELECTED_DAY_INDEX = "last_selected_day_index"
    override val DEFAULT_SELECTED_DAY_INDEX = 0

    override fun getLastItemId(context: Context) = CommonToolbox.getAppSharedPreferences(context).getLong(KEY_LAST_ITEM_ID, DEFAULT_ITEM_ID)

    override fun setLastItemId(context: Context, id: Long) {
        CommonToolbox.getAppSharedPreferences(context).edit().putLong(KEY_LAST_ITEM_ID, id).apply()
    }

    override fun deleteLastItemId(context: Context) {
        CommonToolbox.getAppSharedPreferences(context).edit().remove(KEY_LAST_ITEM_ID).apply()
    }

    override fun getLatestTimestamp(context: Context) = CommonToolbox.getAppSharedPreferences(context).getLong(KEY_LATEST_TIMESTAMP, DEFAULT_LATEST_TIMESTAMP)

    override fun setLatestTimestamp(context: Context, timestamp: Long) {
        CommonToolbox.getAppSharedPreferences(context).edit().putLong(KEY_LATEST_TIMESTAMP, timestamp).apply()
    }

    override fun deleteLatestTimestamp(context: Context) {
        CommonToolbox.getAppSharedPreferences(context).edit().remove(KEY_LATEST_TIMESTAMP).apply()
    }

    override fun getLastSelectedDayIndex(context: Context) = CommonToolbox.getAppSharedPreferences(context).getInt(KEY_LAST_SELECTED_DAY_INDEX, DEFAULT_SELECTED_DAY_INDEX)

    override fun setLastSelectedDayIndex(context: Context, dayIndex: Int) {
        CommonToolbox.getAppSharedPreferences(context).edit().putInt(KEY_LAST_SELECTED_DAY_INDEX, dayIndex).apply()
    }

    override fun deleteLastSelectedDayIndex(context: Context) {
        CommonToolbox.getAppSharedPreferences(context).edit().remove(KEY_LAST_SELECTED_DAY_INDEX).apply()
    }
}