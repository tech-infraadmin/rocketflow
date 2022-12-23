package taskmodule.ui.taskdetails.timeline

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import taskmodule.R
import taskmodule.data.model.response.config.OrderItemsInfo
import taskmodule.databinding.ItemMyEarningEmptySdkBinding
import taskmodule.databinding.ItemRowOrderListSdkBinding
import taskmodule.ui.base.BaseSdkViewHolder
import taskmodule.ui.custom.GlideApp
import taskmodule.ui.earnings.MyEarningsEmptyItemViewModel
//import taskmodule.ui.payouts.AdminUserPayoutsActivity
import taskmodule.utils.Log


/**
 * Created by Vikas Kesharvani on 17/11/20.
 * rocketflyer technology pvt. ltd
 * vikas.kesharvani@rocketflyer.in
 */
class OrderInventoryListAdapter(private var mList: ArrayList<OrderItemsInfo>?) : RecyclerView.Adapter<BaseSdkViewHolder>() {

    private var context: Context? = null
    var onItemClick: ((OrderItemsInfo) -> Unit)? = null


    companion object {
        private const val VIEW_TYPE_EMPTY = 0
        private const val VIEW_TYPE_INVENTORY = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseSdkViewHolder {
        context = parent.context
        return when (viewType) {
            VIEW_TYPE_INVENTORY -> {
                val simpleViewItemBinding: ItemRowOrderListSdkBinding =
                    ItemRowOrderListSdkBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false)
                OrdersListViewModel(simpleViewItemBinding)
            }
            else -> {
                val emptyViewBinding: ItemMyEarningEmptySdkBinding =
                    ItemMyEarningEmptySdkBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false)
                EmptyViewHolder(emptyViewBinding)
            }
        }
    }

    override fun getItemCount(): Int {
        return if (mList != null && mList!!.isNotEmpty()) {
            mList!!.size
        } else {
            1
        }
    }

    override fun getItemViewType(position: Int) = if (mList != null && mList!!.isNotEmpty()) {
        VIEW_TYPE_INVENTORY
    } else {
        VIEW_TYPE_EMPTY
    }

    override fun onBindViewHolder(holder: BaseSdkViewHolder, position: Int) = holder.onBind(position)

    fun addItems(list: ArrayList<OrderItemsInfo>) {
        mList = list
    }


    inner class OrdersListViewModel(private val mBinding: ItemRowOrderListSdkBinding) :
        BaseSdkViewHolder(mBinding.root) {

        override fun onBind(position: Int) {
            itemView.setOnClickListener {
                Log.d("TAG", "customerName.toString()")
                onItemClick?.invoke(mList!![position])
            }
            val lists = mList!![position]
            mBinding.data = lists
            if (lists.img != null && lists.img!!.isNotEmpty()) {
                GlideApp.with(context!!).load(lists.img)
                    .placeholder(R.drawable.ic_picture)
                    .into(mBinding.ivProduct)

            }else{
                mBinding.ivProduct.setImageResource(R.drawable.ic_picture)
            }
            mBinding.executePendingBindings()
        }


    }

    inner class EmptyViewHolder(private val mBinding: ItemMyEarningEmptySdkBinding) :
        BaseSdkViewHolder(mBinding.root) {

        override fun onBind(position: Int) {
            itemView.setOnClickListener {
                onItemClick?.invoke(mList!![position])
            }
            val emptyViewModel = MyEarningsEmptyItemViewModel()
            mBinding.viewModel = emptyViewModel
//            if (context is AdminUserPayoutsActivity) {
//                mBinding.tvMessage.text = "No Orders"
//            }
            // Immediate Binding
            // When a variable or observable changes, the binding will be scheduled to change before
            // the next frame. There are times, however, when binding must be executed immediately.
            // To force execution, use the executePendingBindings() method.
            mBinding.executePendingBindings()
        }
    }
}