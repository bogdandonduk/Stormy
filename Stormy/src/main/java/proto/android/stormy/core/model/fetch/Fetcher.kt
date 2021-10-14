package proto.android.stormy.core.model.fetch

import proto.android.stormy.core.model.item.CoreItem
import proto.android.stormy.core.model.item.weather.Weather

interface Fetcher<ItemType : CoreItem > {
    var reattemptStrategy: RequestReattemptStrategy

    suspend fun fetchSpecific(intrinsicId: Long) : ItemType?

    suspend fun search(query: String) : List<ItemType>?

    fun getInitializeWeather(rawDataText: String) : Weather?

    suspend fun fetchInitializeImageData(rawDataText: String, reattemptStrategy: RequestReattemptStrategy = RequestReattemptStrategy.PREFERRED) : ByteArray?

    suspend fun fetchInitializeRadarImageData(intrinsicId: Long, reattemptStrategy: RequestReattemptStrategy) : ByteArray?

    enum class RequestReattemptStrategy(var attemptsNumber: Int) {
        UNOBTRUSIVE(attemptsNumber = 1),
        PREFERRED(attemptsNumber = 3),
        STUBBORN(attemptsNumber = 5)
    }
}