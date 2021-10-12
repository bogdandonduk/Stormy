package proto.android.stormy.core.base

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import bogdandonduk.viewdatabindingwrapperslib.ViewBindingHandler

abstract class BaseRecyclerViewAdapter<
        ItemType,
        ViewHolderType : BaseRecyclerViewAdapter.BaseViewHolder<ItemType, out ViewBinding>,
        HelperType : BaseRecyclerViewAdapter.BaseHelper>(
    val context: Context,
    var items: List<ItemType>,
    val helper: HelperType,
    private val viewHolderInitialization: (LayoutInflater, parent: ViewGroup) -> ViewHolderType,
) : RecyclerView.Adapter<ViewHolderType>() {
    private val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = viewHolderInitialization.invoke(layoutInflater, parent)

    override fun onBindViewHolder(holder: ViewHolderType, position: Int) {
        holder.item = items[position]
    }

    override fun getItemCount() = items.size

    @Suppress("UNCHECKED_CAST")
    abstract class BaseViewHolder<ItemType, ViewBindingType : ViewBinding>(override var viewBinding: ViewBindingType, open val helper: BaseHelper) : RecyclerView.ViewHolder(viewBinding.root), ViewBindingHandler<ViewBindingType> {
        abstract var item: ItemType
    }

    interface BaseHelper {
        fun onItemClicked(context: Context, id: Long)
    }
}