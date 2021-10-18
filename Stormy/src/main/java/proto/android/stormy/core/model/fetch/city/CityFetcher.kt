package proto.android.stormy.core.model.fetch.city

import android.util.DisplayMetrics
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import proto.android.stormy.Stormy
import proto.android.stormy.core.model.fetch.Fetcher
import proto.android.stormy.core.model.item.city.CityItem
import proto.android.stormy.core.model.item.weather.Day
import proto.android.stormy.core.model.item.weather.Hour
import proto.android.stormy.core.model.item.weather.Weather

class CityFetcher(
    override var reattemptStrategy: Fetcher.RequestReattemptStrategy = Fetcher.RequestReattemptStrategy.PREFERRED,
) : Fetcher<CityItem> {
    // Bare-bones OkHttp and JSON parsing were indeed simpler and more preferable for me in this use case than Retrofit
    var okHttpClient = OkHttpClient()

    override suspend fun fetchSpecific(intrinsicId: Long): CityItem? {
        var attemptNumber = 1

        do {
            try {
                okHttpClient.newCall(Request.Builder().url("https://weather.exam.bottlerocketservices.com/cities/$intrinsicId").build()).execute().run {
                    if(isSuccessful)
                        body?.charStream()?.readText()?.run {
                            close()

                            val cityJsonObject = JSONObject(this).getJSONObject("city")

                            return CityItem(
                                intrinsicId = cityJsonObject.getLong("geonameid"),
                                name = cityJsonObject.getString("name"),
                                countryCode = cityJsonObject.getString("country code"),
                                rawDataText = toString()
                            )
                        }
                    else
                        attemptNumber++
                }
            } catch(thr: Throwable) {
                attemptNumber++
            }
        } while(attemptNumber <= reattemptStrategy.attemptsNumber)

        return null
    }

    override suspend fun search(query: String) : List<CityItem>? {
        var attemptNumber = 1

        do {
            try {
                okHttpClient.newCall(Request.Builder().url("https://weather.exam.bottlerocketservices.com/cities?search=$query").build()).execute().run {
                    if(isSuccessful)
                        body?.charStream()?.readText()?.run responseString@ {
                            close()

                            return mutableListOf<CityItem>().apply {
                                val citiesArray = JSONObject(this@responseString).getJSONArray("cities")

                                for(i in 0 until citiesArray.length()) {
                                    val cityObject = citiesArray.getJSONObject(i)

                                    add(
                                        CityItem(
                                            intrinsicId = cityObject.getLong("geonameid"),
                                            name = cityObject.getString("name"),
                                            countryCode = cityObject.getString("country code"),
                                            rawDataText = toString()
                                        )
                                    )
                                }
                            }.toList()
                        }
                    else
                        attemptNumber++
                }
            } catch(thr: Throwable) {
                attemptNumber++
            }
        } while(attemptNumber <= reattemptStrategy.attemptsNumber)

        return null
    }

    override fun getInitializeWeather(rawDataText: String) : Weather? {
        try {
             return Weather(mutableListOf<Day>().apply {
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

        return null
    }

    override suspend fun fetchInitializeImageData(rawDataText: String, reattemptStrategy: Fetcher.RequestReattemptStrategy) : ByteArray? {
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

                        return body?.bytes()
                    } else
                        lAttemptNumber++

                    close()
                }
            } catch(thr: Throwable) {
                lAttemptNumber++
            }
        } while(!(lAttemptSucceeded || lAttemptNumber >= reattemptStrategy.attemptsNumber))

        return null
    }


    override suspend fun fetchInitializeRadarImageData(intrinsicId: Long, reattemptStrategy: Fetcher.RequestReattemptStrategy) : ByteArray? {
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

                                            close()

                                            return body?.bytes()

                                        } else
                                            locAttemptNumber++
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

        return null
    }
}