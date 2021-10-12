package proto.android.stormy.home.dailyforecast

import android.content.Context
import android.view.LayoutInflater
import androidx.core.content.res.ResourcesCompat
import bogdandonduk.commontoolboxlib.CommonToolbox
import bogdandonduk.viewdatabindingwrapperslib.ViewBindingHandler
import proto.android.stormy.R
import proto.android.stormy.core.base.BaseRecyclerViewAdapter
import proto.android.stormy.core.extensions.getIconResIdForWeatherType
import proto.android.stormy.core.model.item.weather.Day
import proto.android.stormy.databinding.LayoutDailyForecastItemBinding

class DailyForecastAdapter(
    context: Context,
    dayItems: List<Day>,
    helper: Helper
): BaseRecyclerViewAdapter<Day, DailyForecastAdapter.ViewHolder, DailyForecastAdapter.Helper>(context, dayItems, helper, { layoutInflater, parent -> ViewHolder(context, LayoutDailyForecastItemBinding.inflate(layoutInflater, parent, false), helper) }) {
    init {
        val fineDayItems = mutableListOf<Day>().apply {
            items.forEach {
                if(!contains(it)) add(it)
            }
        }.toList()

        items = fineDayItems
    }

    class ViewHolder(context: Context, viewBinding: LayoutDailyForecastItemBinding, override var helper: Helper) : BaseRecyclerViewAdapter.BaseViewHolder<Day, LayoutDailyForecastItemBinding>(viewBinding, helper), ViewBindingHandler<LayoutDailyForecastItemBinding> {
        override lateinit var item: Day

        init {
            viewBinding.root.setOnClickListener {
                helper.onItemClicked(context, item.intrinsicId.toLong())
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)

        holder.run {
            viewBinding.layoutDailyForecastTitleTextView.text = context.resources.getStringArray(R.array.week_days)[item.intrinsicId]

            item.hours.find {
                it.intrinsicId == 0
            }?.temperature.toString().run {
                viewBinding.layoutDailyForecastTemperatureTextView.text = context.getString(R.string.temperature_placeholder, this)
            }

            val selected = item.intrinsicId == helper.getSelectedDayIndex(context)

            val color = ResourcesCompat.getColor(context.resources,
                if(!selected)
                    R.color.dark
                else
                    R.color.light,
                null)

            viewBinding.layoutDailyForecastTitleTextView.setTextColor(color)
            viewBinding.layoutDailyForecastTemperatureTextView.setTextColor(color)

            viewBinding.layoutDailyForecastWeatherTypeIconImageView.setImageResource(CommonToolbox.getIconResIdForWeatherType(item.weatherType, selected))
        }
    }

    interface Helper : BaseHelper {
        fun getSelectedDayIndex(context: Context) : Int
    }
}