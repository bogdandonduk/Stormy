package proto.android.stormy.radar

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import proto.android.stormy.core.model.CityRepo
import proto.android.stormy.core.model.RepoHandler
import proto.android.stormy.core.model.cache.city.CityDatabase
import proto.android.stormy.core.model.fetch.city.CityFetcher
import proto.android.stormy.core.model.item.city.CityItem
import proto.android.stormy.core.model.preferences.city.CityPreferencesManager

class ActivityRadarImageViewModel(
    application: Application,
    override var cityRepo: CityRepo<CityItem> = CityRepo.getSingleton(
        CityFetcher(),
        CityDatabase.getSingleton(application).getDao(),
        CityPreferencesManager()
    )
) : AndroidViewModel(application), RepoHandler<CityItem> {
    override var coroutineScope = viewModelScope

    override var lastCity: CityItem? = null
}