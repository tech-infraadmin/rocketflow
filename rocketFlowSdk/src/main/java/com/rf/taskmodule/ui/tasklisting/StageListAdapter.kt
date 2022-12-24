package com.rf.taskmodule.ui.tasklisting

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rf.taskmodule.R
import com.rf.taskmodule.data.model.response.config.DashBoardBoxItem
import com.rf.taskmodule.databinding.LayoutStagesSdkBinding
import com.rf.taskmodule.ui.base.BaseSdkViewHolder


/**
 * Created by Vikas Kesharvani on 21/08/20.
 * rocketflyer technology pvt. ltd
 * vikas.kesharvani@rocketflyer.in
 */
class StageListAdapter(private var mList: ArrayList<DashBoardBoxItem>?) : RecyclerView.Adapter<com.rf.taskmodule.ui.base.BaseSdkViewHolder>() {

    private lateinit var listener: DashBoardListener
    private var context: Context? = null


    fun setListener(listener: DashBoardListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): com.rf.taskmodule.ui.base.BaseSdkViewHolder {
        context = parent.context
        val simpleViewItemBinding: LayoutStagesSdkBinding =
                LayoutStagesSdkBinding.inflate(
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

    fun addItems(list: ArrayList<DashBoardBoxItem>) {
        mList = list
        notifyDataSetChanged()
    }


    interface DashBoardListener {
        fun onItemClick(response: DashBoardBoxItem)
    }



    inner class SimpleViewHolder(private val mBinding: LayoutStagesSdkBinding) :
            com.rf.taskmodule.ui.base.BaseSdkViewHolder(mBinding.root) {


        override fun onBind(position: Int) {
            val lists = mList!![position]
            mBinding.data = lists
            if (lists.isSelected)
                mBinding.rlMain.setBackgroundResource(R.drawable.selected_stage)
            else
                mBinding.rlMain.setBackgroundResource(R.drawable.unselected_stage)
            mBinding.rlMain.setOnClickListener(View.OnClickListener {
                for (i in mList!!.indices) {
                    mList!![i].isSelected = i == adapterPosition

                }
                listener.onItemClick(lists)
                notifyDataSetChanged()
            })

            mBinding.executePendingBindings()
        }
    }


}