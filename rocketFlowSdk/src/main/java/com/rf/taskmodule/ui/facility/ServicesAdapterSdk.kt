package com.rf.taskmodule.ui.facility

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.rf.taskmodule.R
import com.rf.taskmodule.data.model.response.config.Service
import com.rf.taskmodule.databinding.ItemFacilitySdkBinding
import com.rf.taskmodule.databinding.MyPlaceEmptyViewSdkBinding

class ServicesAdapterSdk (var items: ArrayList<Service>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val VIEW_TYPE_EMPTY = 0
    private val VIEW_TYPE_NORMAL = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return if (viewType == VIEW_TYPE_NORMAL) {
            val view: ItemFacilitySdkBinding = DataBindingUtil.inflate(layoutInflater, R.layout.item_facility_sdk, parent, false)
            ServiceViewHolder(view)
        } else {
            val view: MyPlaceEmptyViewSdkBinding = DataBindingUtil.inflate(layoutInflater, R.layout.my_place_empty_view_sdk, parent, false)
            EmptyViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ServiceViewHolder) {
            val data = items[position]
            holder.bind(data)
        } else if (holder is EmptyViewHolder) {
            holder.bind()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (items.isNotEmpty()) {
            VIEW_TYPE_NORMAL
        } else {
            VIEW_TYPE_EMPTY
        }
    }

    fun getList(): ArrayList<Service> {
        return items
    }

    override fun getItemCount(): Int {
        if (items.isEmpty()) return 1
        return items.size
    }

    inner class ServiceViewHolder(var myTaskSelectionBinding: ItemFacilitySdkBinding) :
            RecyclerView.ViewHolder(myTaskSelectionBinding.root) {

        fun bind(dataModel: Service) {
            this.myTaskSelectionBinding.data = dataModel
            myTaskSelectionBinding.checkbox.setOnCheckedChangeListener(null)
            myTaskSelectionBinding.checkbox.isChecked = dataModel.selected!!
            myTaskSelectionBinding.checkbox.setOnCheckedChangeListener { buttonView, isChecked ->
                dataModel.selected = isChecked
            }
            myTaskSelectionBinding.executePendingBindings()
        }
    }

    inner class EmptyViewHolder(var myPlaceBinding: MyPlaceEmptyViewSdkBinding) :
            RecyclerView.ViewHolder(myPlaceBinding.root) {
        fun bind() {
            myPlaceBinding.executePendingBindings()
        }
    }
}