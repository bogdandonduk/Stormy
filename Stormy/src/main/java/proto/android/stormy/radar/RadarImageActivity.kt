package proto.android.stormy.radar

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.viewModelScope
import bogdandonduk.appbartoolboxandroidlib.appbar.AppBar
import bogdandonduk.appbartoolboxandroidlib.appbar.AppBarHandler
import bogdandonduk.appbartoolboxandroidlib.appbar.AppBarToolbox
import bogdandonduk.appbartoolboxandroidlib.drawer.AppBarDrawerToggle
import bogdandonduk.commontoolboxlib.CommonToolbox
import bogdandonduk.tooltiptoolboxlib.TooltipToolbox
import com.bumptech.glide.Glide
import com.r0adkll.slidr.Slidr
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import proto.android.stormy.R
import proto.android.stormy.core.base.BaseActivity
import proto.android.stormy.core.extensions.configureGoBackTooltip
import proto.android.stormy.core.model.CityRepo
import proto.android.stormy.databinding.ActivityRadarImageBinding

class RadarImageActivity : BaseActivity<ActivityRadarImageBinding, RadarImageActivityViewModel>(
    {
        ActivityRadarImageBinding.inflate(it)
    },
    {
        RadarImageActivityViewModel(it.application)
    }
), AppBarHandler {
    override var appBar: AppBar? = null
    override var appBarDrawerToggle: AppBarDrawerToggle? = null
    override var homeAsUpIndicatorView: View? = null
    override var showOptionsMenu = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getInitializedAppBar(this, viewBinding.activityRadarImageToolbar)
            .modifyAsActionBar {
                it.setDisplayShowTitleEnabled(false)
                it.setDisplayHomeAsUpEnabled(true)
                it.setHomeAsUpIndicator(R.drawable.ic_back_arrow)
            }
            .modifyAsToolbar {
                it.setTitleTextColor(ResourcesCompat.getColor(resources, R.color.dark, null))
            }

        slidrInterface = Slidr.attach(this)

        AppBarToolbox.getHomeAsUpIndicatorAsView(viewBinding.activityRadarImageToolbar)?.run {
            setOnLongClickListener {
                CommonToolbox.vibrateOneShot(this@RadarImageActivity)

                TooltipToolbox.configureGoBackTooltip(this@RadarImageActivity, getInitializedViewModel(this@RadarImageActivity, viewModelStore).goBackTooltipBuilder).show(this@RadarImageActivity, it)

                true
            }

            homeAsUpIndicatorView = this
        }

        loadContent()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home)
            onBackPressed()

        return false
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
                        getInitializedAppBar(this@RadarImageActivity, viewBinding.activityRadarImageToolbar)
                            .modifyAsActionBar {
                                it.setDisplayShowTitleEnabled(true)

                                it.title = "$name, $countryCode"
                            }

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
                    } else
                        viewBinding.activityRadarImageNoImageHintTextView.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun continueTooltips(vararg excludedKeys: String) {
        homeAsUpIndicatorView?.run {
            TooltipToolbox.configureGoBackTooltip(this@RadarImageActivity, getInitializedViewModel(this@RadarImageActivity, viewModelStore).goBackTooltipBuilder).`continue`(this@RadarImageActivity, this)
        }
    }

    override fun dismissTooltips(vararg excludedKeys: String) {
        TooltipToolbox.dismissTooltip(getInitializedViewModel(this, viewModelStore).goBackTooltipBuilder.getKey())
    }
}