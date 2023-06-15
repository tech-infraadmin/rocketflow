package com.rf.taskmodule.ui.tasklisting

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rf.taskmodule.R
import com.rf.taskmodule.data.model.response.config.DashBoardBoxItem
import com.rf.taskmodule.data.model.response.config.Service
import com.rf.taskmodule.databinding.LayoutItemServicesTaskListBinding
import com.rf.taskmodule.databinding.LayoutStagesSdkBinding
import com.rf.taskmodule.ui.base.BaseSdkViewHolder


/**
 * Created by Vikas Kesharvani on 21/08/20.
 * rocketflyer technology pvt. ltd
 * vikas.kesharvani@rocketflyer.in
 */
class TaskServicesListAdapter(private var mList: ArrayList<Service>?) : RecyclerView.Adapter<BaseSdkViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseSdkViewHolder {
        val simpleViewItemBinding: LayoutItemServicesTaskListBinding = LayoutItemServicesTaskListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SimpleViewHolder(simpleViewItemBinding)
    }

    override fun getItemCount(): Int {
        return if (mList != null && mList!!.isNotEmpty()) {
            mList!!.size
        } else {
            0
        }
    }

    override fun onBindViewHolder(holder: BaseSdkViewHolder, position: Int) = holder.onBind(position)

    fun addItems(list: ArrayList<Service>) {
        mList = list
        notifyDataSetChanged()
    }


    interface DashBoardListener {
        fun onItemClick(response: Service)
    }

    inner class SimpleViewHolder(private val mBinding: LayoutItemServicesTaskListBinding) :
            BaseSdkViewHolder(mBinding.root) {

        override fun onBind(position: Int) {
            val lists = mList!![position]
            mBinding.data = lists
            Glide.with(mBinding.imageView3.context).load(lists.icon).into(mBinding.imageView3)
            mBinding.executePendingBindings()
        }
    }


}