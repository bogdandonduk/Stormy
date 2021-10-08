package proto.android.stormy.core.model.fetch.city

import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import proto.android.stormy.core.model.fetch.Fetcher
import proto.android.stormy.core.model.item.city.CityItem

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
}