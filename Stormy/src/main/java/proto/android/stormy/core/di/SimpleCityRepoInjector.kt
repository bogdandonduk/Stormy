package proto.android.stormy.core.di

import android.content.Context
import proto.android.stormy.core.model.CityRepo
import proto.android.stormy.core.model.cache.city.CityDatabase
import proto.android.stormy.core.model.fetch.city.CityFetcher
import proto.android.stormy.core.model.preferences.city.CityPreferencesManager

/** Using DI framework for this project was superfluous and complicating.
 * So custom simple injector was good
*/
object SimpleCityRepoInjector {
    fun getDefault(context: Context) = CityRepo.getSingleton(
        CityFetcher(),
        CityDatabase.getSingleton(context).getDao(),
        CityPreferencesManager()
    )
}