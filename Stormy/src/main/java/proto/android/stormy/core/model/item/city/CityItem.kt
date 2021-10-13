package proto.android.stormy.core.model.item.city

import android.util.DisplayMetrics
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import proto.android.stormy.Stormy
import proto.android.stormy.core.model.cache.city.CityDatabase
import proto.android.stormy.core.model.fetch.Fetcher
import proto.android.stormy.core.model.item.CoreItem
import proto.android.stormy.core.model.item.weather.Day
import proto.android.stormy.core.model.item.weather.Hour
import proto.android.stormy.core.model.item.weather.Weather

@Entity(tableName = CityDatabase.Utils.TABLE_CITIES_NAME)
data class CityItem(
//    @SerializedName("geonameid")
    override var intrinsicId: Long,

//    @SerializedName("name")
    override var name: String,

//    @SerializedName("country code")
    override var countryCode: String,

    override var rawDataText: String,

    @PrimaryKey(autoGenerate = true)
    override var persistenceId: Long = 0
) : CoreItem {

    @Ignore
    override var weather: Weather? = null

    fun getInitializedWeather() : Weather? {
        try {
            weather = Weather(mutableListOf<Day>().apply {
                JSONObject(rawDataText).getJSONObject("weather").getJSONArray("days").run {
                    for(i in 0 until length()) {
                        val dayObject = getJSONObject(i)

                        add(
                            Day(
                                intrinsicId = i,
                                weatherType = dayObject.getString("weatherType"),
                                hours = mutableListOf<Hour>().apply {
                                    dayObject.getJSONArray("hourlyWeather").run {
                                        for (j in 0 until length()) {
                                            val hourObject = getJSONObject(j)

                                            add(
                                                Hour(
                                                    intrinsicId = j,
                                                    hour = hourObject.getInt("hour"),
                                                    rainChance = hourObject.getDouble("rainChance"),
                                                    windSpeed = hourObject.getDouble("windSpeed"),
                                                    humidity = hourObject.getDouble("humidity"),
                                                    weatherType = hourObject.getString("weatherType"),
                                                    temperature = hourObject.getInt("temperature")
                                                )
                                            )
                                        }
                                    }
                                }.toList()
                            )
                        )
                    }
                }
            }.toList())
        } catch(thr: Throwable) {  }

        return weather
    }

    override var imageData: ByteArray? = null

    fun fetchInitializeImageData(reattemptStrategy: Fetcher.RequestReattemptStrategy = Fetcher.RequestReattemptStrategy.PREFERRED) : ByteArray? {
        val imagesObject = JSONObject(rawDataText).getJSONObject("city").getJSONObject("imageURLs").getJSONObject("androidImageURLs")

        val url = imagesObject.getString(
            when(Stormy.instance.resources.displayMetrics.density) {
                DisplayMetrics.DENSITY_XHIGH.toFloat(), DisplayMetrics.DENSITY_XXHIGH.toFloat(), DisplayMetrics.DENSITY_XXXHIGH.toFloat() -> "xhdpiImageURL"
                DisplayMetrics.DENSITY_MEDIUM.toFloat(), DisplayMetrics.DENSITY_LOW.toFloat() -> "mdpiImageURL"

                else -> "hdpiImageURL"
            }
        )

        var lAttemptSucceeded = false
        var lAttemptNumber = 1

        val okHttpClient = OkHttpClient()

        do {
            try {
                okHttpClient.newCall(Request.Builder().url(url).build()).execute().run {
                    if(isSuccessful) {
                        lAttemptSucceeded = true

                        imageData = this.body?.bytes()
                    } else
                        lAttemptNumber++

                    close()
                }
            } catch(thr: Throwable) {
                lAttemptNumber++
            }
        } while(!(lAttemptSucceeded || lAttemptNumber >= reattemptStrategy.attemptsNumber))

        return imageData
    }

    override var radarImageData: ByteArray? = null

    fun fetchInitializeRadarImageData(reattemptStrategy: Fetcher.RequestReattemptStrategy = Fetcher.RequestReattemptStrategy.PREFERRED) : ByteArray? {
        val okHttpClient = OkHttpClient()

        var lAttemptSucceeded = false
        var lAttemptNumber = 1

        do {
            try {
                okHttpClient.newCall(Request.Builder().url("https://weather.exam.bottlerocketservices.com/cities/$intrinsicId/radar").build()).execute().run {
                    if(isSuccessful) {
                        lAttemptSucceeded = true

                        body?.charStream()?.readText()?.run {
                            val imagesObject = JSONObject(this).getJSONObject("androidImageURLs")

                            val url = imagesObject.getString(
                                when(Stormy.instance.resources.displayMetrics.density) {
                                    DisplayMetrics.DENSITY_XHIGH.toFloat(), DisplayMetrics.DENSITY_XXHIGH.toFloat(), DisplayMetrics.DENSITY_XXXHIGH.toFloat() -> "xhdpiImageURL"
                                    DisplayMetrics.DENSITY_MEDIUM.toFloat(), DisplayMetrics.DENSITY_LOW.toFloat() -> "mdpiImageURL"

                                    else -> "hdpiImageURL"
                                }
                            )

                            var locAttemptSucceeded = false
                            var locAttemptNumber = 1

                            do {
                                try {
                                    okHttpClient.newCall(Request.Builder().url(url).build()).execute().run {
                                        if(isSuccessful) {
                                            locAttemptSucceeded = true

                                            radarImageData = this.body?.bytes()
                                        } else
                                            locAttemptNumber++

                                        close()
                                    }
                                } catch(thr: Throwable) {
                                    locAttemptNumber++
                                }
                            } while(!(locAttemptSucceeded || locAttemptNumber >= reattemptStrategy.attemptsNumber))
                        }
                    } else
                        lAttemptNumber++

                    close()
                }
            } catch(thr: Throwable) {
                lAttemptNumber++
            }
        } while(!(lAttemptSucceeded || lAttemptNumber >= reattemptStrategy.attemptsNumber))

        return radarImageData
    }
}