package proto.android.stormy.home

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import bogdandonduk.appbartoolboxandroidlib.appbar.AppBar
import bogdandonduk.appbartoolboxandroidlib.appbar.AppBarHandler
import bogdandonduk.appbartoolboxandroidlib.appbar.AppBarToolbox
import bogdandonduk.appbartoolboxandroidlib.drawer.AppBarDrawerToggle
import bogdandonduk.commontoolboxlib.CommonToolbox
import bogdandonduk.tooltiptoolboxlib.TooltipToolbox
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import proto.android.stormy.R
import proto.android.stormy.Stormy
import proto.android.stormy.citypicker.CityPickerActivity
import proto.android.stormy.core.base.BaseActivity
import proto.android.stormy.core.base.BaseRecyclerViewAdapter
import proto.android.stormy.core.extensions.configureTooltipBuilder
import proto.android.stormy.core.extensions.getDayOfWeekIndex
import proto.android.stormy.core.model.CityRepo
import proto.android.stormy.core.model.item.city.CityItem
import proto.android.stormy.databinding.ActivityHomeBinding
import proto.android.stormy.home.dailyforecast.DailyForecastAdapter
import proto.android.stormy.home.hourlyforecast.HourlyForecastAdapter
import proto.android.stormy.radar.RadarImageActivity
import java.text.SimpleDateFormat
import java.util.*

class HomeActivity : BaseActivity<ActivityHomeBinding, HomeActivityViewModel>({ ActivityHomeBinding.inflate(it) }, { HomeActivityViewModel(it.application) }), AppBarHandler {
    override var appBar: AppBar? = null
    override var appBarDrawerToggle: AppBarDrawerToggle? = null
    override var homeAsUpIndicatorView: View? = null
    override var showOptionsMenu: Boolean = true

    var radarOptionsMenuItemId = "radar"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getInitializedAppBar(this, viewBinding.activityHomeToolbar)
            .modifyAsActionBar {
                it.setDisplayShowTitleEnabled(false)
                it.setDisplayHomeAsUpEnabled(true)
                it.setHomeAsUpIndicator(R.drawable.ic_search)
            }

        viewBinding.activityHomeRefreshFloatingActionButton.setOnClickListener {
            loadContent(true)
        }

        viewBinding.activityHomeHourlyForecastRecyclerView.run {
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    getInitializedViewModel(this@HomeActivity, viewModelStore).hourlyForecastListState = layoutManager!!.onSaveInstanceState()
                }
            })
        }

        AppBarToolbox.getHomeAsUpIndicatorAsView(viewBinding.activityHomeToolbar)?.run {
            setOnLongClickListener {
                CommonToolbox.vibrateOneShot(this@HomeActivity)

                configureSearchTooltip(this@HomeActivity).show(this@HomeActivity, it)

                true
            }

            homeAsUpIndicatorView = this
        }

        viewBinding.activityHomeRefreshFloatingActionButton
            .setOnLongClickListener {
                CommonToolbox.vibrateOneShot(this@HomeActivity)

                configureUpdateImageTooltip(this@HomeActivity).show(this@HomeActivity, it)

                true
            };
        {
            AppBarToolbox.getOptionsMenuItemAsView(radarOptionsMenuItemId, viewBinding.activityHomeToolbar)?.setOnLongClickListener {
                CommonToolbox.vibrateOneShot(this@HomeActivity)

                configureRadarImageTooltip(this@HomeActivity).show(this@HomeActivity, it)


                true
            }
        }.run {
            viewBinding.activityHomeToolbar.post {
                invoke()
            }

            viewBinding.root.viewTreeObserver.addOnGlobalLayoutListener {
                invoke()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_app_bar_activity_home, menu)

        menu?.findItem(R.id.menu_app_bar_activity_home_radar_image_menu_item)?.setOnMenuItemClickListener {
            CommonToolbox.openActivity(this, RadarImageActivity::class.java)

            false
        }

        return showOptionsMenu
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home)
            CommonToolbox.openActivity(this, CityPickerActivity::class.java)

        return false
    }

    override fun loadContent(forceShowIndicators: Boolean) {
        getInitializedViewModel(this, viewModelStore).run {
            viewBinding.activityHomeFetchingProgressBarContainerLinearLayout.visibility = if(forceShowIndicators || isLastCityIntrinsicIdChanged(this@HomeActivity)) View.VISIBLE else View.GONE

            loadLastCity(this@HomeActivity, receiver = { cityItem, source ->
                cityItem.run cityItem@ {
                    val isFromServer = source.name == CityRepo.Source.SERVER.name

                    if(isFromServer)
                        viewBinding.activityHomeFetchingProgressBarContainerLinearLayout.visibility = View.GONE

                    if(this != null) {
                        viewBinding.activityHomeCityNameCountryTextView.text = getString(R.string.city_name_country_placeholder, name, countryCode)

                        viewBinding.activityHomeTimestampTextView.text = CommonToolbox.getDateTime(getLatestTimestamp(this@HomeActivity) / 1000)
                    } else {
                        if(isFromServer)
                            Toast.makeText(Stormy.instance, R.string.something_went_wrong_maybe_internet_connection_lost, Toast.LENGTH_SHORT).show()
                    }
                }
            }, weatherReceiver = { cityItem: CityItem, _ ->
                val timestamp = getLatestTimestamp(this@HomeActivity)

                viewBinding.activityHomeCurrentTemperatureTextView.text = getString(R.string.temperature_placeholder, cityItem.weather!!.days[CommonToolbox.getDayOfWeekIndex(timestamp)].hours[SimpleDateFormat(" HH ", Locale.getDefault()).format(Date(timestamp)).trim().toInt()].temperature.toString())

                viewBinding.activityHomeDailyForecastRecyclerView.run {
                    if(layoutManager == null)
                        layoutManager = LinearLayoutManager(this@HomeActivity, LinearLayoutManager.HORIZONTAL, false)

                    adapter = null
                    adapter = DailyForecastAdapter(this@HomeActivity, cityItem.weather!!.days, object : DailyForecastAdapter.Helper {
                        override fun getSelectedDayIndex(context: Context) = getLastSelectedDayIndex(context)

                        override fun onItemClicked(
                            context: Context,
                            id: Long
                        ) {
                            setLastSelectedDayIndex(context, id.toInt())

                            adapter?.run {
                                for(i in 0 until itemCount)
                                    notifyItemChanged(i)
                            }

                            loadHourlyForecast()
                        }
                    })
                }

                loadHourlyForecast()
            }, imageDataReceiver = { cityItem: CityItem, _ ->
                Glide.with(Stormy.instance)
                    .load(cityItem.imageData)
                    .into(viewBinding.activityHomeCityBigImageImageView)
            }, radarImageDataReceiver = { _, _ -> })
        }
    }

    fun loadHourlyForecast(dayIndex: Int = getInitializedViewModel(this, viewModelStore).getLastSelectedDayIndex(this)) {
        getInitializedViewModel(this, viewModelStore).run {
            lastCity?.weather?.days?.run days@ {
                viewBinding.activityHomeHourlyForecastRecyclerView.run {
                    adapter = HourlyForecastAdapter(this@HomeActivity, this@days[dayIndex].hours, object : BaseRecyclerViewAdapter.BaseHelper {
                        override fun onItemClicked(context: Context, id: Long) {  }
                    })
                }

                viewBinding.activityHomeHourlyForecastRecyclerView.layoutManager?.onRestoreInstanceState(getInitializedViewModel(this@HomeActivity, viewModelStore).hourlyForecastListState)
            }
        }
    }

    override fun continueTooltips(vararg excludedKeys: String) {
        homeAsUpIndicatorView?.run {
            configureSearchTooltip(this@HomeActivity).`continue`(this@HomeActivity, this)
        }

        AppBarToolbox.getOptionsMenuItemAsView(radarOptionsMenuItemId, viewBinding.activityHomeToolbar)?.run {
            configureRadarImageTooltip(this@HomeActivity).`continue`(this@HomeActivity, this)
        }

        configureUpdateImageTooltip(this).`continue`(this, viewBinding.activityHomeRefreshFloatingActionButton)
    }

    override fun dismissTooltips(vararg excludedKeys: String) {
        TooltipToolbox.dismissTooltip(getInitializedViewModel(this, viewModelStore).searchTooltipBuilder.getKey())
        TooltipToolbox.dismissTooltip(getInitializedViewModel(this, viewModelStore).radarImageTooltipBuilder.getKey())
        TooltipToolbox.dismissTooltip(getInitializedViewModel(this, viewModelStore).updateTooltipBuilder.getKey())
    }

    private fun configureSearchTooltip(context: Context) =
        TooltipToolbox.configureTooltipBuilder(context, getInitializedViewModel(this, viewModelStore).searchTooltipBuilder)
            .setText {
                getString(R.string.search_city)
            }
            .setOnClickAction { _, _ ->
                CommonToolbox.openActivity(this, CityPickerActivity::class.java)
            }

    private fun configureRadarImageTooltip(context: Context) =
        TooltipToolbox.configureTooltipBuilder(context, getInitializedViewModel(this, viewModelStore).radarImageTooltipBuilder)
            .setText {
                getString(R.string.city_radar_image)
            }
            .setOnClickAction { _, _ ->
                CommonToolbox.openActivity(this, RadarImageActivity::class.java)
            }

    private fun configureUpdateImageTooltip(context: Context) =
        TooltipToolbox.configureTooltipBuilder(context, getInitializedViewModel(this, viewModelStore).updateTooltipBuilder)
            .setText {
                getString(R.string.update)
            }
            .setOnClickAction { _, _ ->
                viewBinding.activityHomeRefreshFloatingActionButton.performClick()
            }
}