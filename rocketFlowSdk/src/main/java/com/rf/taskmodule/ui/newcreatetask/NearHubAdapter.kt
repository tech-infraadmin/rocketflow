package com.rf.taskmodule.ui.newcreatetask

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.rf.taskmodule.databinding.ItemNearByHubBinding
import com.rf.taskmodule.ui.addplace.Hub
import com.rf.taskmodule.ui.base.BaseSdkViewHolder

class NearHubAdapter(): RecyclerView.Adapter<NearHubAdapter.NearHubViewHolder>() {

    var nearHubList: ArrayList<Hub> = ArrayList()
    var mListener: NearHubInterface? = null

    inner class NearHubViewHolder(var binding: ItemNearByHubBinding) : BaseSdkViewHolder(binding.root) {
        override fun onBind(position: Int) {
            binding.hub = nearHubList[position]
            binding.btnProceedHub.setOnClickListener {
                mListener?.addNearHubItems(nearHubList[position])
            }
            binding.ivPinHub.setOnClickListener {
                mListener?.openPlaceOnMaps(nearHubList[position])
            }
        }

    }

    fun setListener(context: Context){
        this.mListener = context as NearHubInterface
    }

    fun addItems(list: ArrayList<Hub>){
        this.nearHubList = list
        notifyDataSetChanged()
    }

    fun clearItems(){
        nearHubList.clear()
        notifyDataSetChanged()
        notifyItemRangeRemoved(0,nearHubList.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NearHubViewHolder {
            val simpleViewItemBinding: ItemNearByHubBinding =
                ItemNearByHubBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            return NearHubViewHolder(simpleViewItemBinding)
    }

    override fun getItemCount(): Int {
        return nearHubList.size
    }

    override fun onBindViewHolder(holder: NearHubViewHolder, position: Int) {
        holder.onBind(holder.adapterPosition)
    }

    interface NearHubInterface{
        fun addNearHubItems(hub: Hub)
        fun openPlaceOnMaps(hub: Hub)
    }
}