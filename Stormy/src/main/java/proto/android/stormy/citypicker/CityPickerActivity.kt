package proto.android.stormy.citypicker

import android.os.Bundle
import androidx.core.content.res.ResourcesCompat
import bogdandonduk.commontoolboxlib.CommonToolbox
import com.r0adkll.slidr.Slidr
import proto.android.stormy.R
import proto.android.stormy.core.base.BaseActivity
import proto.android.stormy.databinding.ActivityCityPickerBinding
import top.defaults.drawabletoolbox.DrawableBuilder

class CityPickerActivity : BaseActivity<ActivityCityPickerBinding, CityPickerActivityViewModel>(
    { ActivityCityPickerBinding.inflate(it) },
    { CityPickerActivityViewModel(it.application) }
) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        slidrInterface = Slidr.attach(this)

        viewBinding.activityCityPickerCloseButtonConstraintLayout.run {
            background =
                DrawableBuilder()
                    .ripple()
                    .rippleColor(CommonToolbox.getRippleColorByLuminance(this@CityPickerActivity, ResourcesCompat.getColor(resources, R.color.transparent_strong_light, null)))
                    .cornerRadius(1000000)
                    .build()

            setOnClickListener {
                onBackPressed()
            }
        }
    }

    override fun loadContent(forceShowIndicators: Boolean) {
        TODO("Not yet implemented")
    }
}