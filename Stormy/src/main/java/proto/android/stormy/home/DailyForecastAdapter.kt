package proto.android.stormy.home

import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import bogdandonduk.commontoolboxlib.CommonToolbox
import bogdandonduk.viewdatabindingwrapperslib.ViewBindingHandler
import proto.android.stormy.R
import proto.android.stormy.core.extensions.getIconResIdForWeatherType
import proto.android.stormy.core.model.item.weather.Day
import proto.android.stormy.databinding.LayoutDailyForecastItemBinding

class DailyForecastAdapter(var dayItems: List<Day>, var hostActivity: HomeActivity): RecyclerView.Adapter<DailyForecastAdapter.ViewHolder>() {
    init {
        val fineDayItems = mutableListOf<Day>().apply {
            dayItems.forEach {
                if(!contains(it)) add(it)
            }
        }.toList()

        dayItems = fineDayItems
    }

    inner class ViewHolder(override var viewBinding: LayoutDailyForecastItemBinding) : RecyclerView.ViewHolder(viewBinding.root), ViewBindingHandler<LayoutDailyForecastItemBinding> {
        lateinit var dayItem: Day

        init {
            viewBinding.root.setOnClickListener {
                hostActivity.getInitializedViewModel(hostActivity, hostActivity.viewModelStore).cityRepo.preferencesManager.run {
                    setLastSelectedDayIndex(hostActivity, dayItem.intrinsicId)

                    for(i in 0 until itemCount) {
                        notifyItemChanged(i)
                    }

                    hostActivity.loadHourlyForecast()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(LayoutDailyForecastItemBinding.inflate(hostActivity.layoutInflater, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.run {
            dayItem = dayItems[position]

            viewBinding.layoutDailyForecastTitleTextView.text = hostActivity.resources.getStringArray(R.array.week_days)[dayItem.intrinsicId]

            dayItem.hours.find {
                it.intrinsicId == 0
            }?.temperature.toString().run {
                viewBinding.layoutDailyForecastTemperatureTextView.text = "$this°"
            }

            val selected = dayItem.intrinsicId == hostActivity.getInitializedViewModel(hostActivity, hostActivity.viewModelStore).cityRepo.preferencesManager.getLastSelectedDayIndex(hostActivity)

            val color = ResourcesCompat.getColor(hostActivity.resources,
                if(!selected)
                    R.color.dark
                else
                    R.color.light,
                null)

            viewBinding.layoutDailyForecastTitleTextView.setTextColor(color)
            viewBinding.layoutDailyForecastTemperatureTextView.setTextColor(color)

            viewBinding.layoutDailyForecastWeatherTypeIconImageView.setImageResource(CommonToolbox.getIconResIdForWeatherType(dayItem.weatherType, selected))
        }
    }

    override fun getItemCount() = dayItems.size
}