import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.tracki.R
import com.tracki.data.model.response.config.Slot
import com.tracki.databinding.TimeCardBinding
import com.tracki.ui.base.BaseViewHolder
import com.tracki.utils.Log

class SlotChildAdapter(var list: ArrayList<Slot>, var context: Context) : RecyclerView.Adapter<BaseViewHolder>() {

    var onItemClick: ((String) -> Unit)? = null
    var index = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val binding: TimeCardBinding = TimeCardBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return SlotViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    override fun getItemCount(): Int {
        return if (list != null) list.size else 0
    }

    inner class SlotViewHolder(mBinding: TimeCardBinding) : BaseViewHolder(mBinding.root) {
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