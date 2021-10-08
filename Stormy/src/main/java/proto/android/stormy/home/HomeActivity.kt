package proto.android.stormy.home

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import bogdandonduk.appbartoolboxandroidlib.appbar.AppBar
import bogdandonduk.appbartoolboxandroidlib.appbar.AppBarHandler
import bogdandonduk.appbartoolboxandroidlib.drawer.AppBarDrawerToggle
import bogdandonduk.commontoolboxlib.CommonToolbox
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import proto.android.stormy.R
import proto.android.stormy.Stormy
import proto.android.stormy.citypicker.CityPickerActivity
import proto.android.stormy.core.base.BaseActivity
import proto.android.stormy.core.extensions.getDayOfWeekIndex
import proto.android.stormy.core.model.CityRepo
import proto.android.stormy.databinding.ActivityHomeBinding
import proto.android.stormy.radar.RadarImageActivity
import java.text.SimpleDateFormat
import java.util.*

class HomeActivity : BaseActivity<ActivityHomeBinding, HomeActivityViewModel>({ ActivityHomeBinding.inflate(it) }, { HomeActivityViewModel(it.application) }), AppBarHandler {
    override var appBar: AppBar? = null
    override var appBarDrawerToggle: AppBarDrawerToggle? = null
    override var homeAsUpIndicatorView: View? = null
    override var showOptionsMenu: Boolean = true

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
    }

    override fun onResume() {
        super.onResume()

        loadContent()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_app_bar_activity_home, menu)

        menu?.findItem(R.id.menu_app_bar_activity_home_radar_image_menu_item)?.setOnMenuItemClickListener {
            startActivity(Intent(this, RadarImageActivity::class.java))

            false
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home)
            startActivity(Intent(this, CityPickerActivity::class.java))

        return false
    }

    override fun loadContent(forceShowIndicators: Boolean) {
        getInitializedViewModel(this, viewModelStore).run {
            viewBinding.activityHomeFetchingProgressBarContainerLinearLayout.visibility = if(forceShowIndicators || isLastCityIntrinsicIdChanged(this@HomeActivity)) View.VISIBLE else View.GONE

            loadLastCity(this@HomeActivity) { cityItem, source ->
                cityItem.run cityItem@ {
                    val isFromServer = source.name == CityRepo.Source.SERVER.name

                    if(isFromServer)
                        viewBinding.activityHomeFetchingProgressBarContainerLinearLayout.visibility = View.GONE

                    if(this != null) {
                        viewBinding.activityHomeCityNameCountryTextView.text = "$name, $countryCode"

                        val timestamp = getLatestTimestamp(this@HomeActivity)

                        viewBinding.activityHomeTimestampTextView.text = CommonToolbox.getDateTime(timestamp / 1000)

                        getInitializedWeather()?.run {
                            viewBinding.activityHomeCurrentTemperatureTextView.text = "${days[CommonToolbox.getDayOfWeekIndex(timestamp)].hours[SimpleDateFormat(" HH ", Locale.getDefault()).format(Date(timestamp)).trim().toInt()].temperature}°"

                            viewBinding.activityHomeDailyForecastRecyclerView.run {
                                if(layoutManager == null)
                                    layoutManager = LinearLayoutManager(this@HomeActivity, LinearLayoutManager.HORIZONTAL, false)

                                adapter = null
                                adapter = DailyForecastAdapter(days, this@HomeActivity)
                            }

                            loadHourlyForecast()
                        }

                        viewModelScope.launch(IO) {
                            fetchInitializeImageData()?.run imageData@ {
                                cacheCity(this@cityItem)

                                withContext(Main) {
                                    Glide.with(Stormy.globalContext)
                                        .load(this@imageData)
                                        .into(viewBinding.activityHomeCityBigImageImageView)
                                }
                            }

                            fetchInitializeRadarImageData()?.run {
                                cacheCity(this@cityItem)
                            }
                        }
                    } else {
                        if(isFromServer)
                            Toast.makeText(this, R.string.something_went_wrong_maybe_internet_connection_lost, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    fun loadHourlyForecast(dayIndex: Int = getInitializedViewModel(this, viewModelStore).getLastSelectedDayIndex(this)) {
        getInitializedViewModel(this, viewModelStore).lastCity?.weather?.days?.run {
            viewBinding.activityHomeHourlyForecastRecyclerView.adapter = HourlyForecastAdapter(this[dayIndex].hours, this@HomeActivity)

            viewBinding.activityHomeHourlyForecastRecyclerView.layoutManager?.onRestoreInstanceState(getInitializedViewModel(this@HomeActivity, viewModelStore).hourlyForecastListState)
        }
    }
}