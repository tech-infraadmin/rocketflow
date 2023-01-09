package com.rf.taskmodule.ui.dynamicform.dynamicfragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.rf.taskmodule.R
import com.rf.taskmodule.data.model.response.config.SlotData
import com.rf.taskmodule.databinding.DateDayCardSdkBinding
import com.rf.taskmodule.ui.base.BaseSdkViewHolder
import com.rf.taskmodule.utils.Log
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.LinkedHashMap

class SlotAdapter(val context: Context) : RecyclerView.Adapter<BaseSdkViewHolder>() {
    var list: LinkedHashMap<String, SlotData> = LinkedHashMap()
    var index = 0
    var onItemClick: ((String,Int, ArrayList<SlotData>, ArrayList<String>) -> Unit)? = null
    var listKey: ArrayList<String> = ArrayList()
    var listValues: ArrayList<SlotData> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseSdkViewHolder {
        val binding: DateDayCardSdkBinding = DateDayCardSdkBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return SlotViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BaseSdkViewHolder, position: Int) {
        holder.onBind(position)
    }

    fun String.toDate(): Date{
        return SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(this)!!
    }

    fun setMap(list1: Map<String, SlotData>){

        list1.forEach {
            list.put(it.key,it.value)
        }

        list.map { it.key to it.value }.sortedBy { it.first.toDate() }

        notifyItemRangeInserted(0,list.size)
        notifyDataSetChanged()
    }

    fun clearAll(){
        list.clear()
        notifyItemRangeRemoved(0,list.size)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return if (list != null) list.size else 0
    }

    inner class SlotViewHolder(mBinding: DateDayCardSdkBinding) : BaseSdkViewHolder(mBinding.root) {
        var binding = mBinding
        @SuppressLint("NotifyDataSetChanged")
        @RequiresApi(Build.VERSION_CODES.M)
        override fun onBind(position: Int) {
            val keys = list.keys
            val key = keys.elementAt(position).toString()

            val date = stringToDate(key)!!

            binding.dataDateSdk = getDate(date)
            binding.dataDaySdk = getDay(date)
            if(index == position){
                binding.cvDay.setCardBackgroundColor(context.getColor(R.color.light_blue))
            }
            else {
                binding.cvDay.setCardBackgroundColor(context.getColor(R.color.white))
            }

            binding.cvDay.setOnClickListener {
                onItemClick?.invoke(key,position,listValues,listKey)
                index = position
                notifyItemChanged(index)
                notifyDataSetChanged()
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun stringToDate(dateString: String): Date? {
        val date = SimpleDateFormat("dd-MM-yyyy").parse(dateString)
        return date
    }

    private fun getDate(date: Date): String {
        val cal = Calendar.getInstance()
        cal.time = date
        val dateString = checkDigit(cal.get(Calendar.DATE)).toString()
        Log.e("appLog",dateString)
        return dateString
    }

    private fun checkDigit(number: Int): String? {
        return if (number <= 9) "0$number" else number.toString()
    }

    private fun getDay(date: Date): String {
        val locale = Locale.getDefault()
        val outFormat = SimpleDateFormat("EEE",locale)
        val dayString = outFormat.format(date)
        Log.e("appLog",dayString)
        return dayString
    }
}