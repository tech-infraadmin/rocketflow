import android.database.DataSetObserver
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tracki.data.model.response.config.SlotData
import com.tracki.databinding.TimeCardBinding
import com.tracki.ui.base.BaseViewHolder
class SlotAdapter(){}
//class SlotAdapter() : RecyclerView.Adapter<BaseViewHolder>(), ListAdapter {
//    var list: ArrayList<SlotData>? = null
//
//    fun setSlotList(list: ArrayList<SlotData>){
//        this.list = list
//    }
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
//        val binding: TimeCardBinding = TimeCardBinding
//            .inflate(LayoutInflater.from(parent.context), parent, false)
//
//        return SlotViewHolder(binding)
//    }
//
//    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
//        holder.onBind(position)
//    }
//
//    override fun getItemCount(): Int {
//        return if (list != null) list!!.size else 0
//    }
//
//    inner class SlotViewHolder(mBinding: TimeCardBinding) : BaseViewHolder(mBinding.root) {
//        var binding = mBinding
//        override fun onBind(position: Int) {
//            val data = list?.get(position)
//            binding.data = data
//        }
//    }
//
//    override fun registerDataSetObserver(p0: DataSetObserver?) {
//
//    }
//
//    override fun unregisterDataSetObserver(p0: DataSetObserver?) {
//
//    }
//
//    override fun getCount(): Int {
//      return 0;
//    }
//
//    override fun getItem(p0: Int): Any {
//        return p0
//    }
//
//    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
//        TODO("Not yet implemented")
//    }
//
//    override fun getViewTypeCount(): Int {
//        TODO("Not yet implemented")
//    }
//
//    override fun isEmpty(): Boolean {
//        TODO("Not yet implemented")
//    }
//
//    override fun areAllItemsEnabled(): Boolean {
//        TODO("Not yet implemented")
//    }
//
//    override fun isEnabled(p0: Int): Boolean {
//        TODO("Not yet implemented")
//    }
//}