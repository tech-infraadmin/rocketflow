import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.provider.CalendarContract.Colors
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.tracki.R
import com.tracki.data.model.response.config.SlotData
import com.tracki.databinding.DateDayCardBinding
import com.tracki.ui.base.BaseViewHolder
import com.tracki.utils.Log
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class SlotAdapter(val context: Context) : RecyclerView.Adapter<BaseViewHolder>() {
    var list: HashMap<String, SlotData> = HashMap()
    var index = 0
    var onItemClick: ((String,Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val binding: DateDayCardBinding = DateDayCardBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return SlotViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    fun setMap(list1: Map<String, SlotData>){
        list1.forEach {
            list.put(it.key,it.value)
        }

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

    inner class SlotViewHolder(mBinding: DateDayCardBinding) : BaseViewHolder(mBinding.root) {
        var binding = mBinding
        @SuppressLint("NotifyDataSetChanged")
        @RequiresApi(Build.VERSION_CODES.M)
        override fun onBind(position: Int) {
            val keys = list.keys
            val key = keys.elementAt(position).toString()

            val date = stringToDate(key)!!

            binding.dataDate = getDate(date)
            binding.dataDay = getDay(date)
            if(index == position){
                binding.cvDay.setCardBackgroundColor(context.getColor(R.color.light_blue))
            }
            else {
                binding.cvDay.setCardBackgroundColor(context.getColor(R.color.white))
            }

            binding.cvDay.setOnClickListener {
                onItemClick?.invoke(key,position)
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