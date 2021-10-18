package proto.android.stormy.home

import android.app.Application
import android.os.Parcelable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import bogdandonduk.tooltiptoolboxlib.TooltipToolbox
import kotlinx.coroutines.Job
import proto.android.stormy.core.di.SimpleCityRepoInjector
import proto.android.stormy.core.model.CityRepo
import proto.android.stormy.core.model.RepoHandler
import proto.android.stormy.core.model.item.city.CityItem

class HomeActivityViewModel(
    application: Application,
    override var cityRepo: CityRepo<CityItem> = SimpleCityRepoInjector.getDefault(application)
) : AndroidViewModel(application), RepoHandler<CityItem> {
    override var coroutineScope = viewModelScope

    override var searchJob: Job? = null

    override var lastCity: CityItem? = null

    var hourlyForecastListState: Parcelable? = null

    var searchTooltipBuilder = TooltipToolbox.buildTooltip()
    var radarImageTooltipBuilder = TooltipToolbox.buildTooltip()
    var updateTooltipBuilder = TooltipToolbox.buildTooltip()
}