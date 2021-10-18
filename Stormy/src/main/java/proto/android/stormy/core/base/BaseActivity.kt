package proto.android.stormy.core.base

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding
import bogdandonduk.appbartoolboxandroidlib.SlidrHandler
import bogdandonduk.commontoolboxlib.CommonToolbox
import bogdandonduk.tooltiptoolboxlib.TooltipsHandler
import bogdandonduk.viewdatabindingwrapperslib.BaseViewBindingHandlerActivity
import bogdandonduk.viewmodelwrapperslib.automatic.SingleAutomaticInitializationWithInitializationViewModelHandlerActivity
import com.r0adkll.slidr.model.SlidrInterface

abstract class BaseActivity<ViewBindingType : ViewBinding, ViewModelType : ViewModel>(
    viewBindingInflation: (layoutInflater: LayoutInflater) -> ViewBindingType,
    override var viewModelInitialization: (Activity) -> ViewModelType
) : BaseViewBindingHandlerActivity<ViewBindingType>(viewBindingInflation),
    SingleAutomaticInitializationWithInitializationViewModelHandlerActivity<ViewModelType>,
    SlidrHandler,
    TooltipsHandler {
    override var slidrInterface: SlidrInterface? = null

    abstract fun loadContent(forceShowIndicators: Boolean = false)

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        continueTooltips()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        CommonToolbox.registerCurrentActivity(this)
    }

    override fun onDestroy() {
        super.onDestroy()

        CommonToolbox.unregisterCurrentActivity()
    }

    override fun continueTooltips(vararg excludedKeys: String) {

    }

    override fun dismissTooltips(vararg excludedKeys: String) {

    }
}