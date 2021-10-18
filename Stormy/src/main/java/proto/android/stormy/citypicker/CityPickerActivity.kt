package proto.android.stormy.citypicker

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.addTextChangedListener
import bogdandonduk.commontoolboxlib.CommonToolbox
import bogdandonduk.tooltiptoolboxlib.TooltipToolbox
import com.r0adkll.slidr.Slidr
import proto.android.stormy.R
import proto.android.stormy.core.base.BaseActivity
import proto.android.stormy.core.base.BaseRecyclerViewAdapter
import proto.android.stormy.core.extensions.configureGoBackTooltip
import proto.android.stormy.databinding.ActivityCityPickerBinding
import top.defaults.drawabletoolbox.DrawableBuilder

class CityPickerActivity : BaseActivity<ActivityCityPickerBinding, CityPickerActivityViewModel>(
    { ActivityCityPickerBinding.inflate(it) },
    { CityPickerActivityViewModel(it.application) }
) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        slidrInterface = Slidr.attach(this)

        viewBinding.activityCityPickerCloseButtonConstraintLayout.run {
            background =
                DrawableBuilder()
                    .ripple()
                    .rippleColor(CommonToolbox.getRippleColorByLuminance(this@CityPickerActivity, ResourcesCompat.getColor(resources, R.color.transparent_strong_light, null)))
                    .cornerRadius(1000000)
                    .build()

            setOnClickListener {
                onBackPressed()
            }
        }

        viewBinding.activityCityPickerSearchEditText.run {
            addTextChangedListener {
                it?.toString()?.run {
                    if(isNotEmpty() && isNotBlank())
                        loadContent()
                }
            }
        }

        viewBinding.activityCityPickerCloseButtonConstraintLayout
            .setOnLongClickListener {
                CommonToolbox.vibrateOneShot(this@CityPickerActivity)

                TooltipToolbox.configureGoBackTooltip(this@CityPickerActivity, getInitializedViewModel(this@CityPickerActivity, viewModelStore).goBackTooltipBuilder).show(this@CityPickerActivity, it)

                true
            }

        loadContent()
    }

    override fun loadContent(forceShowIndicators: Boolean) {
        getInitializedViewModel(this, viewModelStore).run {
            viewBinding.activityCityPickerResultHintTextView.visibility = View.GONE
            viewBinding.activityCityPickerFetchingProgressBar.visibility = View.VISIBLE
            viewBinding.activityCityPickerSearchResultListRecyclerView.adapter = null

            search(viewBinding.activityCityPickerSearchEditText.text.toString()) {
                viewBinding.activityCityPickerFetchingProgressBar.visibility = View.GONE

                if(it != null) {
                    if(it.isNotEmpty())
                        viewBinding.activityCityPickerSearchResultListRecyclerView.adapter = CityPickerAdapter(this@CityPickerActivity, it, object : BaseRecyclerViewAdapter.BaseHelper {
                            override fun onItemClicked(context: Context, id: Long) {
                                setLastCityId(context, id)

                                finish()
                            }
                        })
                    else
                        viewBinding.activityCityPickerResultHintTextView.run {
                            visibility = View.VISIBLE

                            text = getString(R.string.no_cities_found)
                        }
                } else {
                    viewBinding.activityCityPickerResultHintTextView.run {
                        visibility = View.VISIBLE

                        text = getString(R.string.no_cities_found)
                    }
                }
            }
        }
    }

    override fun continueTooltips(vararg excludedKeys: String) {
        TooltipToolbox.configureGoBackTooltip(this, getInitializedViewModel(this, viewModelStore).goBackTooltipBuilder).`continue`(this, viewBinding.activityCityPickerCloseButtonConstraintLayout)
    }

    override fun dismissTooltips(vararg excludedKeys: String) {
        TooltipToolbox.dismissTooltip(getInitializedViewModel(this, viewModelStore).goBackTooltipBuilder.getKey())
    }
}