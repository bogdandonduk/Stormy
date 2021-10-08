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
    var okHttpClient = OkHttpClient()

    override suspend fun fetchSpecific(intrinsicId: Long): CityItem? {
        var attemptNumber = 1

        do {
            try {
                okHttpClient.newCall(Request.Builder().url("https://weather.exam.bottlerocketservices.com/cities/$intrinsicId").build()).execute().run {
                    if(isSuccessful)
                        body?.charStream()?.readText()?.run {
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

    override suspend fun fetchAll(): List<CityItem>? {
//        var attemptNumber = 1
//
//        okHttpClient.newCall(Request.Builder().url("https://weather.exam.bottlerocketservices.com/cities").build()).execute().run {
//            if(isSuccessful)
//                body?.charStream()?.readText()?.run {
//
//                }
//        }

        return null
    }
}