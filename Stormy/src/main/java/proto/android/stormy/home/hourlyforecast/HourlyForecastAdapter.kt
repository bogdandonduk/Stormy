package proto.android.stormy.home.hourlyforecast

import android.content.Context
import bogdandonduk.commontoolboxlib.CommonToolbox
import bogdandonduk.viewdatabindingwrapperslib.ViewBindingHandler
import proto.android.stormy.R
import proto.android.stormy.core.base.BaseRecyclerViewAdapter
import proto.android.stormy.core.extensions.getConvertedHour
import proto.android.stormy.core.extensions.getIconResIdForWeatherType
import proto.android.stormy.core.model.item.weather.Hour
import proto.android.stormy.databinding.LayoutHourlyForecastItemBinding

class HourlyForecastAdapter(
    context: Context,
    hourItems: List<Hour>,
    helper: BaseHelper
): BaseRecyclerViewAdapter<Hour, HourlyForecastAdapter.ViewHolder, BaseRecyclerViewAdapter.BaseHelper>(context, hourItems, helper, { layoutInflater, parent -> ViewHolder(LayoutHourlyForecastItemBinding.inflate(layoutInflater, parent, false), helper) }) {
    init {
        val fineHourItems = mutableListOf<Hour>().apply {
            hourItems.forEach {
                if(!contains(it)) add(it)
            }
        }.toList()

        items = fineHourItems
    }

    class ViewHolder(override var viewBinding: LayoutHourlyForecastItemBinding, override val helper: BaseHelper) : BaseRecyclerViewAdapter.BaseViewHolder<Hour, LayoutHourlyForecastItemBinding>(viewBinding, helper), ViewBindingHandler<LayoutHourlyForecastItemBinding> {
        override lateinit var item: Hour
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)

        holder.run {
            viewBinding.layoutHourlyForecastItemWeatherTypeIconImageView.setImageResource(CommonToolbox.getIconResIdForWeatherType(item.weatherType, true))

            viewBinding.layoutHourlyForecastItemTimeTextView.text = CommonToolbox.getConvertedHour(item.hour)
            viewBinding.layoutHourlyForecastItemTemperatureTextView.text = context.getString(R.string.temperature_placeholder, item.temperature.toString())
            viewBinding.layoutHourlyForecastItemPrecipitationTextView.text = context.getString(R.string.percentage_placeholder, item.rainChance.toString())
            viewBinding.layoutHourlyForecastItemWindTextView.text = item.windSpeed.toString()
            viewBinding.layoutHourlyForecastItemHumidityTextView.text = context.getString(R.string.percentage_placeholder, item.humidity.toString())
        }
    }
}