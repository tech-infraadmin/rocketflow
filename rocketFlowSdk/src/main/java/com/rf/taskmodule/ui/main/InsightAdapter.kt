package com.rf.taskmodule.ui.main

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import com.rf.taskmodule.data.model.response.config.ServiceDescr
import com.rf.taskmodule.databinding.LayoutItemInsightsSdkBinding
import com.rf.taskmodule.ui.base.BaseSdkViewHolder
import com.rf.taskmodule.utils.CommonUtils

class InsightAdapter(private var mList: ArrayList<ServiceDescr?>?) : RecyclerView.Adapter<com.rf.taskmodule.ui.base.BaseSdkViewHolder>() {

    private var context: Context? = null
    private var cellwidthWillbe: Int = 0
    fun cellWidth(cellwidthWillbe: Int) {
        this.cellwidthWillbe = cellwidthWillbe;
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): com.rf.taskmodule.ui.base.BaseSdkViewHolder {
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


    override fun onBindViewHolder(holder: com.rf.taskmodule.ui.base.BaseSdkViewHolder, position: Int) = holder.onBind(position)

    fun addItems(list: ArrayList<ServiceDescr?>) {
        mList = list
        notifyDataSetChanged()
    }


    inner class SimpleViewHolder(private val mBinding: LayoutItemInsightsSdkBinding) :
            com.rf.taskmodule.ui.base.BaseSdkViewHolder(mBinding.root) {

        override fun onBind(position: Int) {
            if(mList!=null) {
                val lists: ServiceDescr? = mList!![position]
                if (lists != null) {
                    if (cellwidthWillbe != 0) {
                        com.rf.taskmodule.utils.CommonUtils.showLogMessage("e", "cellwidthWillbe", "" + cellwidthWillbe)
                        mBinding.cardViewMain.layoutParams = FrameLayout.LayoutParams(
                                cellwidthWillbe - 20, com.rf.taskmodule.utils.CommonUtils.dpToPixel(context, 85))
                    }
                    mBinding.data = lists
                    mBinding.executePendingBindings()
                }
            }
        }
    }


}