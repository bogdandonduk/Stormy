package proto.android.stormy.core.model

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import proto.android.stormy.core.model.item.CoreItem

interface RepoHandler<ItemType : CoreItem> {
    var coroutineScope: CoroutineScope
    
    var cityRepo: CityRepo<ItemType>
    
    var lastCity: ItemType?
    
    fun isLastCityIntrinsicIdChanged(context: Context) = cityRepo.preferencesManager.getLastItemId(context) != lastCity?.intrinsicId

    fun loadLastCity(context: Context, switchToMainThread: Boolean = true, receiver: (ItemType?, CityRepo.Source) -> Unit) {
        coroutineScope.launch(Dispatchers.IO) {
            cityRepo.load(context) { item: ItemType?, source: CityRepo.Source ->
                lastCity = item

                if(switchToMainThread)
                    withContext(Dispatchers.Main) {
                        receiver(item, source)
                    }
                else
                    receiver(item, source)
            }
        }
    }

    fun cacheCity(city: ItemType) {
        coroutineScope.launch(Dispatchers.IO) {
            cityRepo.cache(city)
        }
    }

    fun getLatestTimestamp(context: Context) = cityRepo.preferencesManager.getLatestTimestamp(context)

    fun getLastSelectedDayIndex(context: Context) = cityRepo.preferencesManager.getLastSelectedDayIndex(context)
}