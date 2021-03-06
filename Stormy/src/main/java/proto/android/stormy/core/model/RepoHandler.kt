package proto.android.stormy.core.model

import android.content.Context
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import proto.android.stormy.core.model.item.CoreItem
import proto.android.stormy.core.model.item.weather.Weather

interface RepoHandler<ItemType : CoreItem> {
    var coroutineScope: CoroutineScope
    var searchJob: Job?
    
    var cityRepo: CityRepo<ItemType>
    
    var lastCity: ItemType?
    
    fun isLastCityIntrinsicIdChanged(context: Context) = cityRepo.preferencesManager.getLastItemId(context) != lastCity?.intrinsicId

    fun loadLastCity(
        context: Context,
        switchToMainThread: Boolean = true,
        receiver: (ItemType?, CityRepo.Source) -> Unit,
        weatherReceiver: (item: ItemType, source: CityRepo.Source) -> Unit = { _, _ -> },
        imageDataReceiver: (item: ItemType, source: CityRepo.Source) -> Unit = { _, _ -> },
        radarImageDataReceiver: (item: ItemType, source: CityRepo.Source) -> Unit = { _, _ -> }
    ) {
        coroutineScope.launch(IO) {
            cityRepo.load(context, receiver = { item: ItemType?, source: CityRepo.Source ->
                lastCity = item

                if(switchToMainThread)
                    withContext(Dispatchers.Main) {
                        receiver(item, source)
                    }
                else
                    receiver(item, source)
            }, weatherReceiver = { item: ItemType, source: CityRepo.Source ->
                if(switchToMainThread)
                    withContext(Dispatchers.Main) {
                        weatherReceiver(item, source)
                    }
                else
                    weatherReceiver(item, source)
            }, imageDataReceiver = { item: ItemType, source: CityRepo.Source ->
                if(switchToMainThread)
                    withContext(Dispatchers.Main) {
                        imageDataReceiver(item, source)
                    }
                else
                    imageDataReceiver(item, source)
            }, radarImageDataReceiver = { item: ItemType, source: CityRepo.Source ->
                if(switchToMainThread)
                    withContext(Dispatchers.Main) {
                        radarImageDataReceiver(item, source)
                    }
                else
                    radarImageDataReceiver(item, source)
            })
        }
    }

    fun cacheCity(city: ItemType) {
        coroutineScope.launch(IO) {
            cityRepo.cache(city)
        }
    }

    fun getLatestTimestamp(context: Context) = cityRepo.preferencesManager.getLatestTimestamp(context)

    fun getLastSelectedDayIndex(context: Context) = cityRepo.preferencesManager.getLastSelectedDayIndex(context)

    fun setLastSelectedDayIndex(context: Context, dayIndex: Int) {
        cityRepo.preferencesManager.setLastSelectedDayIndex(context, dayIndex)
    }

    fun getLastCityId(context: Context) = cityRepo.preferencesManager.getLastItemId(context)

    fun setLastCityId(context: Context, cityIntrinsicId: Long) = cityRepo.preferencesManager.setLastItemId(context, cityIntrinsicId)

    fun search(query: String, switchToMainThread: Boolean = true, receiver: (List<ItemType>?) -> Unit) {
        if(searchJob != null && searchJob!!.isActive) {
            searchJob?.cancel("Previous similar irrelevant job cancelled")

            searchJob = null
        }

        searchJob = coroutineScope.launch(IO) {
            val items = cityRepo.fetcher.search(query)

            if(switchToMainThread)
                withContext(Dispatchers.Main) {
                    receiver(items)
                }
            else
                receiver(items)
        }
    }
}