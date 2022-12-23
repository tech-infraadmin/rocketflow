package taskmodule.ui.dynamicform.dynamicfragment

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import taskmodule.R
import taskmodule.data.model.response.config.Slot
import taskmodule.databinding.TimeCardSdkBinding
import taskmodule.ui.base.BaseSdkViewHolder
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class SlotChildAdapter(var list: ArrayList<Slot>, var context: Context) : RecyclerView.Adapter<BaseSdkViewHolder>() {

    var onItemClick: ((String) -> Unit)? = null
    var index = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseSdkViewHolder {
        val binding: TimeCardSdkBinding = TimeCardSdkBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return SlotViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BaseSdkViewHolder, position: Int) {
        holder.onBind(position)
    }

    override fun getItemCount(): Int {
        return if (list != null) list.size else 0
    }

    fun String.toDate(): Date {
        return SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(this)!!
    }

    inner class SlotViewHolder(mBinding: TimeCardSdkBinding) : BaseSdkViewHolder(mBinding.root) {
        var binding = mBinding
        @RequiresApi(Build.VERSION_CODES.M)
        override fun onBind(position: Int) {
            val data = list.get(position)
            binding.data = data

            binding.cvTime.setCardBackgroundColor(context.getColor(R.color.white))
            if(index == position){
                binding.cvTime.setCardBackgroundColor(context.getColor(R.color.light_blue))
            }
            else {

                if (!data.available){
                    binding.cvTime.setCardBackgroundColor(context.getColor(R.color.light_gray))
                }
            }

            binding.cvTime.setOnClickListener {
                onItemClick?.invoke(data.time)
                index = position
                notifyItemChanged(position)
                notifyDataSetChanged()
            }
        }
    }
}