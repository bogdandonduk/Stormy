package proto.android.stormy.core.model.fetch.city

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface AllWeatherAPI {
    companion object {
        const val BASE_URL = "https://weather.exam.bottlerocketservices.com/"
    }

    @GET("cities")
    suspend fun getSearchCities(@Query("search") query: String) : Call<String>

    @GET("cities/{geonameid}")
    suspend fun getCityDetails(@Path("geonameid") intrinsicId: Long) : Call<String>
}