package com.rf.taskmodule.ui.selectorder

import android.content.Context
import android.view.*
import android.widget.*

import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

import com.rf.taskmodule.R
import com.rf.taskmodule.data.model.response.config.LinkOptions
import com.rf.taskmodule.databinding.ItemDimeBinding
import com.rf.taskmodule.databinding.LayoutEmptyCategoryViewSdkBinding
import com.rf.taskmodule.ui.base.BaseSdkViewHolder
import com.rf.taskmodule.utils.CommonUtils
import java.util.ArrayList

class DimeListAdapter(var context: Context) :
    RecyclerView.Adapter<BaseSdkViewHolder>() {
    var mList: HashMap<String, CatalogProduct> = HashMap()
    var mListener: OnProductRemoveListener? = null
    private val VIEW_TYPE_EMPTY = 0
    private val VIEW_TYPE_NORMAL = 1
    private var linkOption: LinkOptions? = null

    init {
        mListener = context as OnProductRemoveListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseSdkViewHolder {
        return if (viewType == VIEW_TYPE_NORMAL) {
            val simpleViewItemBinding: ItemDimeBinding =
                ItemDimeBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )

            StoreProductItemViewHolder(simpleViewItemBinding)
        } else {
            val simpleViewItemBinding: LayoutEmptyCategoryViewSdkBinding =
                LayoutEmptyCategoryViewSdkBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            EmptyViewHolder(simpleViewItemBinding)
        }


    }


    override fun getItemCount(): Int {
        return if (mList.isNotEmpty()) {
            mList.size
        } else {
            1
        }
    }

    fun addItems(list: HashMap<String, CatalogProduct>) {

        if (mList.isNotEmpty()) {
            notifyItemRangeInserted(mList.size + 1, list!!.size)
        } else {
            notifyDataSetChanged()
        }
        mList = list
    }

    fun setMap(mapTemp: HashMap<String, CatalogProduct>) {
        mList = mapTemp
    }

    fun getAllList(): HashMap<String, CatalogProduct> {
        return mList
    }

    fun removeAt(position: Int, listSize: Int) {
        val keyList = mList.keys.toMutableList()
        mList.remove(keyList[position])
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, listSize)
    }

    private fun clearItems() {
        mList.clear()
        notifyDataSetChanged()
    }

    fun clearList() {
        mList.clear()
        notifyDataSetChanged()

    }

    inner class StoreProductItemViewHolder(var binding: ItemDimeBinding) :
        BaseSdkViewHolder(binding.root) {
        override fun onBind(positionx: Int) {
            var data: CatalogProduct? = mList[mList.keys.toMutableList()[positionx]]
            if (data != null) {

                binding.data = data
                if (data.subUnit != null || data.subUnit!!.size > 0) {
                    binding.subUnit = data.subUnit!![0]

                    binding.delProd.setOnClickListener {

                        mListener?.deleteProduct(data)
                    }
                }


                binding.llImage.setOnClickListener {
                    if (mListener != null) {
                        CommonUtils.preventTwoClick(it)
                    }
                }
                binding.llTxtData.setOnClickListener {
                    if (mListener != null) {
                        CommonUtils.preventTwoClick(it)
                    }
                }
            }


        }

    }


    interface OnProductRemoveListener {
        fun deleteProduct(data: CatalogProduct)
    }

    inner class EmptyViewHolder(var binding: LayoutEmptyCategoryViewSdkBinding) :
        BaseSdkViewHolder(binding.root) {

        override fun onBind(position: Int) {
            binding.tvDesc.text = context.getString(R.string.seems_like_no_product)
            binding.tvTitle.text = context.getString(R.string.no_product_found)
            binding.ivMain.setImageDrawable(
                ContextCompat.getDrawable(
                    context,
                    R.drawable.ic_shopping_cart
                )
            )

            binding.executePendingBindings()
        }

    }

    override fun getItemViewType(position: Int): Int {
        return if (mList.isNotEmpty()) {
            VIEW_TYPE_NORMAL
        } else {
            VIEW_TYPE_EMPTY
        }
    }

    override fun onBindViewHolder(holder: BaseSdkViewHolder, position: Int) {
        holder.onBind(holder.adapterPosition)
    }


}
