package proto.android.stormy.core.model

import android.content.Context
import proto.android.stormy.core.model.cache.CacheDao
import proto.android.stormy.core.model.fetch.Fetcher
import proto.android.stormy.core.model.item.CoreItem
import proto.android.stormy.core.model.preferences.PreferencesManager

@Suppress("UNCHECKED_CAST")
class CityRepo <ItemType : CoreItem> private constructor(var fetcher: Fetcher<ItemType>, var cacheDao: CacheDao<ItemType>, var preferencesManager: PreferencesManager) {
    companion object {
        @Volatile
        private var instance: CityRepo<out CoreItem>? = null

        fun <ItemType : CoreItem> getSingleton(fetcher: Fetcher<ItemType>, cacheDao: CacheDao<ItemType>, preferencesManager: PreferencesManager) : CityRepo<ItemType> {
            if(instance == null)
                synchronized(this) {
                    instance = CityRepo(fetcher, cacheDao, preferencesManager)
                }

            return instance as CityRepo<ItemType>
        }
    }

    @JvmOverloads
    suspend fun getCached(context: Context, intrinsicId: Long = preferencesManager.getLastItemId(context)) = cacheDao.get(intrinsicId)

    @JvmOverloads
    suspend fun fetch(context: Context, intrinsicId: Long = preferencesManager.getLastItemId(context)) = fetcher.fetchSpecific(intrinsicId)

    fun getWeather(rawDataText: String) = fetcher.getInitializeWeather(rawDataText)

    suspend fun fetchImage(rawDataText: String, reattemptStrategy: Fetcher.RequestReattemptStrategy = Fetcher.RequestReattemptStrategy.PREFERRED) = fetcher.fetchInitializeImageData(rawDataText, reattemptStrategy)

    suspend fun fetchRadarImage(intrinsicId: Long, reattemptStrategy: Fetcher.RequestReattemptStrategy = Fetcher.RequestReattemptStrategy.PREFERRED) = fetcher.fetchInitializeRadarImageData(intrinsicId, reattemptStrategy)

    suspend inline fun load(
        context: Context,
        writeToCache: Boolean = true,
        receiver: (item: ItemType?, source: Source) -> Unit,
        weatherReceiver: (item: ItemType, source: Source) -> Unit = { _, _ -> },
        imageDataReceiver: (item: ItemType, source: Source) -> Unit = { _, _ -> },
        radarImageDataReceiver: (item: ItemType, source: Source) -> Unit = { _, _ -> }
    ) {
        getCached(context)?.run {
            preferencesManager.setLatestTimestamp(context, System.currentTimeMillis())
            receiver(this, Source.CACHE)
        }

        fetch(context).run {
            if(writeToCache && this != null)
                cacheDao.add(this)

            preferencesManager.setLatestTimestamp(context, System.currentTimeMillis())

            receiver(this, Source.SERVER)

            this?.run {
                getWeather(rawDataText)?.let {
                    weather = it

                    weatherReceiver(this, Source.SERVER)
                }

                fetchImage(rawDataText)?.let {
                    imageData = it

                    cache(this)

                    imageDataReceiver(this, Source.SERVER)
                }

                fetchRadarImage(intrinsicId)?.let {
                    radarImageData = it

                    cache(this)

                    radarImageDataReceiver(this, Source.SERVER)
                }
            }
        }
    }

    suspend fun cache(item: ItemType) {
        cacheDao.add(item)
    }

    enum class Source {
        SERVER,
        CACHE
    }
}