package proto.android.stormy.home

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import bogdandonduk.commontoolboxlib.CommonToolbox
import bogdandonduk.viewdatabindingwrapperslib.ViewBindingHandler
import proto.android.stormy.core.extensions.getConvertedHour
import proto.android.stormy.core.extensions.getIconResIdForWeatherType
import proto.android.stormy.core.model.item.weather.Hour
import proto.android.stormy.databinding.LayoutHourlyForecastItemBinding

class HourlyForecastAdapter(var hourItems: List<Hour>, var hostActivity: HomeActivity): RecyclerView.Adapter<HourlyForecastAdapter.ViewHolder>() {
    init {
        val fineHourItems = mutableListOf<Hour>().apply {
            hourItems.forEach {
                if(!contains(it)) add(it)
            }
        }.toList()

        hourItems = fineHourItems
    }

    inner class ViewHolder(override var viewBinding: LayoutHourlyForecastItemBinding) : RecyclerView.ViewHolder(viewBinding.root), ViewBindingHandler<LayoutHourlyForecastItemBinding> {
        lateinit var hourItem: Hour

        init {
            viewBinding.root.setOnClickListener {
                hostActivity.getInitializedViewModel(hostActivity, hostActivity.viewModelStore).cityRepo.preferencesManager.run {
                    setLastSelectedDayIndex(hostActivity, hourItem.intrinsicId)

                    for(i in 0 until itemCount) {
                        notifyItemChanged(i)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(LayoutHourlyForecastItemBinding.inflate(hostActivity.layoutInflater, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.run {
            hourItem = hourItems[position]

            viewBinding.layoutHourlyForecastItemWeatherTypeIconImageView.setImageResource(CommonToolbox.getIconResIdForWeatherType(hourItem.weatherType, true))

            viewBinding.layoutHourlyForecastItemTimeTextView.text = CommonToolbox.getConvertedHour(hourItem.hour)
            viewBinding.layoutHourlyForecastItemTemperatureTextView.text = "${hourItem.temperature}°"
            viewBinding.layoutHourlyForecastItemPrecipitationTextView.text = "${hourItem.rainChance}%"
            viewBinding.layoutHourlyForecastItemWindTextView.text = hourItem.windSpeed.toString()
            viewBinding.layoutHourlyForecastItemHumidityTextView.text = "${hourItem.humidity}%"
        }
    }

    override fun getItemCount() = hourItems.size
}