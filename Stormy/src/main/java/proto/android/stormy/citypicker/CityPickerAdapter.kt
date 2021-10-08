package proto.android.stormy.citypicker

import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import bogdandonduk.commontoolboxlib.CommonToolbox
import bogdandonduk.viewdatabindingwrapperslib.ViewBindingHandler
import proto.android.stormy.R
import proto.android.stormy.core.model.item.city.CityItem
import proto.android.stormy.databinding.LayoutCityPickerItemBinding
import top.defaults.drawabletoolbox.DrawableBuilder

class CityPickerAdapter(var cityItems: List<CityItem>, var hostActivity: CityPickerActivity): RecyclerView.Adapter<CityPickerAdapter.ViewHolder>() {
    inner class ViewHolder(override var viewBinding: LayoutCityPickerItemBinding) : RecyclerView.ViewHolder(viewBinding.root), ViewBindingHandler<LayoutCityPickerItemBinding> {
        lateinit var cityItem: CityItem

        init {
            viewBinding.root.run {
                background =
                    DrawableBuilder()
                        .ripple()
                        .rippleColor(CommonToolbox.getRippleColorByLuminance(hostActivity, ResourcesCompat.getColor(hostActivity.resources, R.color.transparent_strong_light, null)))
                        .build()

                setOnClickListener {
                    hostActivity.getInitializedViewModel(
                        hostActivity,
                        hostActivity.viewModelStore
                    ).cityRepo.preferencesManager.run {
                        setLastItemId(hostActivity, cityItem.intrinsicId)

                        hostActivity.finish()
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(LayoutCityPickerItemBinding.inflate(hostActivity.layoutInflater, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.run {
            cityItem = cityItems[position]

            viewBinding.layoutCityPickerItemNameCountryTextView.text = "${cityItem.name}, ${cityItem.countryCode}"
        }
    }

    override fun getItemCount() = cityItems.size
}