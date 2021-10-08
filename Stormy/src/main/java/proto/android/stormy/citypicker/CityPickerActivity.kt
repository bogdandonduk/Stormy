package proto.android.stormy.citypicker

import proto.android.stormy.core.base.BaseActivity
import proto.android.stormy.databinding.ActivityCityPickerBinding

class CityPickerActivity : BaseActivity<ActivityCityPickerBinding, CityPickerActivityViewModel>(
    { ActivityCityPickerBinding.inflate(it) },
    { CityPickerActivityViewModel(it.application) }
) {
    override fun loadContent(forceShowIndicators: Boolean) {
        TODO("Not yet implemented")
    }
}