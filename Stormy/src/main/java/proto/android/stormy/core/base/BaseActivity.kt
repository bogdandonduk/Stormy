package proto.android.stormy.core.base

import android.app.Activity
import android.view.LayoutInflater
import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding
import bogdandonduk.appbartoolboxandroidlib.SlidrHandler
import bogdandonduk.viewdatabindingwrapperslib.BaseViewBindingHandlerActivity
import bogdandonduk.viewmodelwrapperslib.automatic.SingleAutomaticInitializationWithInitializationViewModelHandlerActivity
import com.r0adkll.slidr.model.SlidrInterface
import proto.android.stormy.home.HomeActivityViewModel

abstract class BaseActivity<ViewBindingType : ViewBinding, ViewModelType : ViewModel>(
    viewBindingInflation: (layoutInflater: LayoutInflater) -> ViewBindingType,
    override var viewModelInitialization: (Activity) -> ViewModelType
) : BaseViewBindingHandlerActivity<ViewBindingType>(viewBindingInflation), SingleAutomaticInitializationWithInitializationViewModelHandlerActivity<ViewModelType>, SlidrHandler {
    override var slidrInterface: SlidrInterface? = null

    abstract fun loadContent(forceShowIndicators: Boolean = false)
}