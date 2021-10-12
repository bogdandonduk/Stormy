package proto.android.stormy.citypicker

import android.content.Context
import androidx.core.content.res.ResourcesCompat
import bogdandonduk.commontoolboxlib.CommonToolbox
import bogdandonduk.viewdatabindingwrapperslib.ViewBindingHandler
import proto.android.stormy.R
import proto.android.stormy.core.base.BaseRecyclerViewAdapter
import proto.android.stormy.core.model.item.city.CityItem
import proto.android.stormy.databinding.LayoutCityPickerItemBinding
import top.defaults.drawabletoolbox.DrawableBuilder

class CityPickerAdapter(
    context: Context,
    cityItems: List<CityItem>,
    helper: BaseHelper
): BaseRecyclerViewAdapter<CityItem, CityPickerAdapter.ViewHolder, BaseRecyclerViewAdapter.BaseHelper>(context, cityItems, helper, { layoutInflater, parent -> ViewHolder(LayoutCityPickerItemBinding.inflate(layoutInflater, parent, false), helper) }) {
    class ViewHolder(viewBinding: LayoutCityPickerItemBinding, override val helper: BaseHelper) : BaseRecyclerViewAdapter.BaseViewHolder<CityItem, LayoutCityPickerItemBinding>(viewBinding, helper), ViewBindingHandler<LayoutCityPickerItemBinding> {
        override lateinit var item: CityItem

        init {
            viewBinding.root.run {
                background =
                    DrawableBuilder()
                        .ripple()
                        .rippleColor(CommonToolbox.getRippleColorByLuminance(context, ResourcesCompat.getColor(context.resources, R.color.transparent_strong_light, null)))
                        .build()

                setOnClickListener {
                    helper.onItemClicked(context, item.intrinsicId)
                }
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)

        holder.run {
            viewBinding.layoutCityPickerItemNameCountryTextView.text = context.getString(R.string.city_name_country_placeholder, item.name, item.countryCode)
        }
    }
}