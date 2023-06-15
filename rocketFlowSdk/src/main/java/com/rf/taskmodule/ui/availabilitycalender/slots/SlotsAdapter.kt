package com.rf.taskmodule.ui.availabilitycalender.slots

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.rf.taskmodule.R
import com.rf.taskmodule.data.model.response.config.Slot
import com.rf.taskmodule.databinding.SlotsCardSdkBinding
import com.rf.taskmodule.databinding.TimeCardSdkBinding
import com.rf.taskmodule.ui.base.BaseSdkViewHolder
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class SlotsAdapter(var list: ArrayList<Slot>, var context: Context) :
    RecyclerView.Adapter<BaseSdkViewHolder>() {

    var onItemClick: ((String) -> Unit)? = null
    var index = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseSdkViewHolder {
        val binding: SlotsCardSdkBinding = SlotsCardSdkBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return SlotViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BaseSdkViewHolder, position: Int) {
        holder.onBind(position)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class SlotViewHolder(mBinding: SlotsCardSdkBinding) : BaseSdkViewHolder(mBinding.root) {
        var binding = mBinding

        @RequiresApi(Build.VERSION_CODES.M)
        override fun onBind(position: Int) {
            val data = list[position]
            if (data.status == "AVAILABLE" || data.status == "BOOKED") {
                binding.cvTime.setCardBackgroundColor(context.getColor(R.color.white))
            } else {
                binding.cvTime.setCardBackgroundColor(context.getColor(R.color.light_gray))
            }
            binding.data = data
            binding.root.setOnClickListener {
                if (data.status == "AVAILABLE" || data.status == "BOOKED") {
                    onItemClick?.invoke(data.time)
                }
            }
        }
    }
}