package proto.android.stormy.core.extensions

import bogdandonduk.commontoolboxlib.CommonToolbox
import proto.android.stormy.R
import java.util.*

fun CommonToolbox.getIconResIdForWeatherType(weatherType: String, selected: Boolean) =
    when(weatherType) {
        "sunny" -> if(!selected) R.drawable.ic_weather_active_ic_sunny_active else R.drawable.ic_weather_active_ic_sunny_active_light
        "snowSleet" -> if(!selected) R.drawable.ic_weather_active_ic_snow_sleet_active else R.drawable.ic_weather_active_ic_snow_sleet_active_light
        "cloudy" -> if(!selected) R.drawable.ic_weather_active_ic_cloudy_active else R.drawable.ic_weather_active_ic_cloudy_active_light
        "partlyCloudy" -> if(!selected) R.drawable.ic_weather_active_ic_partly_cloudy_active else R.drawable.ic_weather_active_ic_partly_cloudy_active_light
        "lightRain" -> if(!selected) R.drawable.ic_weather_active_ic_light_rain_active else R.drawable.ic_weather_active_ic_light_rain_active_light
        "heavyRain" -> if(!selected) R.drawable.ic_weather_active_ic_heavy_rain_active else R.drawable.ic_weather_active_ic_heavy_rain_active_light

        else -> throw IllegalArgumentException("Invalid weather type given")
    }

fun CommonToolbox.getConvertedHour(rawHour: Int) =
    when(rawHour) {
        0 -> "12 PM"
        1 -> "1 AM"
        2 -> "2 AM"
        3 -> "3 AM"
        4 -> "4 AM"
        5 -> "5 AM"
        6 -> "6 AM"
        7 -> "7 AM"
        8 -> "8 AM"
        9 -> "9 AM"
        10 -> "10 PM"
        11 -> "11 PM"
        12 -> "12 PM"
        13 -> "1 PM"
        14 -> "2 PM"
        15 -> "3 PM"
        16 -> "4 PM"
        17 -> "5 PM"
        18 -> "6 PM"
        19 -> "7 PM"
        20 -> "8 PM"
        21 -> "9 PM"
        22 -> "10 PM"
        23 -> "11 PM"

        else -> throw IllegalArgumentException("Invalid raw hour given, should be in range from 0 to 23")
    }

fun CommonToolbox.getDayOfWeekIndex(timeMillis: Long) =
    when(Calendar.getInstance().apply { time = Date(timeMillis) }.get(Calendar.DAY_OF_WEEK)) {
        Calendar.MONDAY -> 0
        Calendar.TUESDAY -> 1
        Calendar.WEDNESDAY -> 2
        Calendar.THURSDAY -> 3
        Calendar.FRIDAY -> 4
        Calendar.SATURDAY -> 5

        else -> 6
    }