package proto.android.stormy.radar

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import bogdandonduk.commontoolboxlib.CommonToolbox
import com.bumptech.glide.Glide
import com.r0adkll.slidr.Slidr
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import proto.android.stormy.R
import proto.android.stormy.Stormy
import proto.android.stormy.core.base.BaseActivity
import proto.android.stormy.core.model.CityRepo
import proto.android.stormy.databinding.ActivityRadarImageBinding
import proto.android.stormy.home.DailyForecastAdapter

class RadarImageActivity : BaseActivity<ActivityRadarImageBinding, ActivityRadarImageViewModel>(
    {
        ActivityRadarImageBinding.inflate(it)
    },
    {
        ActivityRadarImageViewModel(it.application)
    }
) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        CommonToolbox.launchStickyImmersiveMode(window)

        slidrInterface = Slidr.attach(this)

        loadContent()
    }

    override fun onResume() {
        super.onResume()

        CommonToolbox.launchStickyImmersiveMode(window)

        loadContent()
    }

    override fun loadContent(forceShowIndicators: Boolean) {
        getInitializedViewModel(this, viewModelStore).run {
            viewBinding.activityRadarImageNoImageHintTextView.visibility = View.GONE
            viewBinding.activityRadarImageLoadingProgressBarContainerConstraintLayout.visibility = if(forceShowIndicators || isLastCityIntrinsicIdChanged(this@RadarImageActivity)) View.VISIBLE else View.GONE

            loadLastCity(this@RadarImageActivity) { cityItem, source ->
                cityItem.run cityItem@ {
                    val isFromServer = source.name == CityRepo.Source.SERVER.name

                    if(isFromServer)
                        viewBinding.activityRadarImageLoadingProgressBarContainerConstraintLayout.visibility = View.GONE

                    if(this != null) {
                        viewModelScope.launch(IO) {
                            fetchInitializeRadarImageData().run imageData@ {
                                if(this != null) {
                                    cacheCity(this@cityItem)

                                    withContext(Main) {
                                        Glide.with(this@RadarImageActivity)
                                            .asGif()
                                            .load(this@imageData)
                                            .into(viewBinding.activityRadarImageRadarImageImageView)
                                    }
                                } else
                                    withContext(Main) {
                                        viewBinding.activityRadarImageNoImageHintTextView.visibility = View.VISIBLE
                                    }
                            }
                        }
                    } else {
                        viewBinding.activityRadarImageNoImageHintTextView.visibility = View.VISIBLE

                        if(isFromServer)
                            Toast.makeText(this@RadarImageActivity, R.string.something_went_wrong_maybe_internet_connection_lost, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}