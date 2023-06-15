package com.rf.taskmodule.ui.taskdetails

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rf.taskmodule.R
import com.rf.taskmodule.data.model.response.config.Service
import com.rf.taskmodule.databinding.ItemServicesSdkBinding
import com.rf.taskmodule.ui.taskdetails.timeline.TaskDetailsFragment

class TaskServicesAdapter(var context: TaskDetailsFragment) :
        RecyclerView.Adapter<TaskServicesAdapter.TimeLineViewHolder>() {
    var items: ArrayList<Service> = ArrayList()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeLineViewHolder {
        var layoutInflater = LayoutInflater.from(parent.context)
        val view: ItemServicesSdkBinding = DataBindingUtil.inflate(layoutInflater, R.layout.item_services_sdk, parent, false)
       return  TimeLineViewHolder(view)
    }

    override fun onBindViewHolder(holder: TimeLineViewHolder, position: Int) {
        var data = items[position]
        holder.bind(data)
    }

    fun addData(items: List<Service>){
        if(this.items.isNotEmpty()) {
            this.items.clear()
        }
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        if (items.isEmpty()) return 0
        return items.size
    }

    inner class TimeLineViewHolder(var timeLineBinding: ItemServicesSdkBinding) :
            RecyclerView.ViewHolder(timeLineBinding.root) {

        fun bind(dataModel: Service) {
            timeLineBinding.data = dataModel
            timeLineBinding.executePendingBindings()
            Glide.with(context).load(dataModel.icon).into(timeLineBinding.btnDetails)

        }

    }


}