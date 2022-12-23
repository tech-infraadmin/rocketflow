package taskmodule.ui.main

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import taskmodule.data.model.response.config.ServiceDescr
import taskmodule.databinding.LayoutItemInsightsSdkBinding
import taskmodule.ui.base.BaseSdkViewHolder
import taskmodule.utils.CommonUtils

class InsightAdapter(private var mList: ArrayList<ServiceDescr?>?) : RecyclerView.Adapter<BaseSdkViewHolder>() {

    private var context: Context? = null
    private var cellwidthWillbe: Int = 0
    fun cellWidth(cellwidthWillbe: Int) {
        this.cellwidthWillbe = cellwidthWillbe;
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseSdkViewHolder {
        context = parent.context
        val simpleViewItemBinding: LayoutItemInsightsSdkBinding =
                LayoutItemInsightsSdkBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false)

        return SimpleViewHolder(simpleViewItemBinding)
    }

    override fun getItemCount(): Int {
        return if (mList != null && mList!!.isNotEmpty()) {
            mList!!.size
        } else {
            0
        }
    }

    public fun clearList() {
        if (mList != null && mList!!.isNotEmpty()) {
            mList!!.clear()
            notifyDataSetChanged()
        }

    }


    override fun onBindViewHolder(holder: BaseSdkViewHolder, position: Int) = holder.onBind(position)

    fun addItems(list: ArrayList<ServiceDescr?>) {
        mList = list
        notifyDataSetChanged()
    }


    inner class SimpleViewHolder(private val mBinding: LayoutItemInsightsSdkBinding) :
            BaseSdkViewHolder(mBinding.root) {

        override fun onBind(position: Int) {
            if(mList!=null) {
                val lists: ServiceDescr? = mList!![position]
                if (lists != null) {
                    if (cellwidthWillbe != 0) {
                        CommonUtils.showLogMessage("e", "cellwidthWillbe", "" + cellwidthWillbe)
                        mBinding.cardViewMain.layoutParams = FrameLayout.LayoutParams(
                                cellwidthWillbe - 20, CommonUtils.dpToPixel(context, 85))
                    }
                    mBinding.data = lists
                    mBinding.executePendingBindings()
                }
            }
        }
    }


}