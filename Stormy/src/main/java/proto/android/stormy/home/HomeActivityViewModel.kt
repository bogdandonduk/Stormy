package proto.android.stormy.home

import android.app.Application
import android.content.Context
import android.os.Parcelable
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import bogdandonduk.livedatatoolboxlib.LiveDataToolbox
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import proto.android.stormy.core.extensions.TAG
import proto.android.stormy.core.model.CityRepo
import proto.android.stormy.core.model.RepoHandler
import proto.android.stormy.core.model.cache.city.CityDatabase
import proto.android.stormy.core.model.fetch.city.CityFetcher
import proto.android.stormy.core.model.item.CoreItem
import proto.android.stormy.core.model.item.city.CityItem
import proto.android.stormy.core.model.preferences.city.CityPreferencesManager
import kotlin.coroutines.CoroutineContext

class HomeActivityViewModel(
    application: Application,
    override var cityRepo: CityRepo<CityItem> = CityRepo.getSingleton(
        CityFetcher(),
        CityDatabase.getSingleton(application).getDao(),
        CityPreferencesManager()
    )
) : AndroidViewModel(application), RepoHandler<CityItem> {
    override var coroutineScope = viewModelScope

    override var lastCity: CityItem? = null

    var hourlyForecastListState: Parcelable? = null
}