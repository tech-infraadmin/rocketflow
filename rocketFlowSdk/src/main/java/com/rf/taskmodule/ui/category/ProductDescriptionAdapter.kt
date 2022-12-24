package com.rf.taskmodule.ui.category

import android.content.Context
import android.view.*
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.rf.taskmodule.R
import com.rf.taskmodule.databinding.ItemRowProductDescriptionSdkBinding
import com.rf.taskmodule.ui.base.BaseSdkViewHolder


class ProductDescriptionAdapter(var context: Context? = null) :
    RecyclerView.Adapter<com.rf.taskmodule.ui.base.BaseSdkViewHolder>() {
    var mListener: OnProductDescriptionListener? = null

    interface OnProductDescriptionListener {
        fun onEditProductDescription(data: ProductDescription, position: Int)
        fun removeProductDescription(data: ProductDescription, position: Int)
    }

    init {
        mListener = context as OnProductDescriptionListener
    }

    private var mList: ArrayList<ProductDescription> = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): com.rf.taskmodule.ui.base.BaseSdkViewHolder {
        context = parent.context
        val simpleViewItemBinding: ItemRowProductDescriptionSdkBinding =
            ItemRowProductDescriptionSdkBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        return ProductDescriptionsListViewModel(simpleViewItemBinding)
    }

    override fun getItemCount(): Int {
        return if (mList!!.isNotEmpty()) {
            mList!!.size
        } else {
            0
        }
    }

    fun getAllList(): ArrayList<ProductDescription> {
        return mList
    }


    override fun onBindViewHolder(holder: com.rf.taskmodule.ui.base.BaseSdkViewHolder, position: Int) =
        holder.onBind(holder.adapterPosition)

    fun addItems(list: ArrayList<ProductDescription>) {
        mList.addAll(list)
       /* if (mList.isNotEmpty()) {
            notifyItemRangeInserted(mList.size + 1, list!!.size)
        } else {
            notifyDataSetChanged()
        }*/
        notifyDataSetChanged()

    }

    fun addItem(data:ProductDescription) {
        mList.add(data)
        if (mList.isNotEmpty()) {
            notifyItemRangeInserted(mList.size + 1, 1)
        } else {
            notifyDataSetChanged()
        }

    }


    inner class ProductDescriptionsListViewModel(private val mBinding: ItemRowProductDescriptionSdkBinding) :
        com.rf.taskmodule.ui.base.BaseSdkViewHolder(mBinding.root) {

        override fun onBind(position: Int) {
            val data = mList!![position]
            mBinding.data = data
            mBinding.ivThreeDot.setOnClickListener {
                showPopUpMenu(mBinding.ivThreeDot, data, position)
            }
            mBinding.executePendingBindings()
        }


    }

    fun removeAt(position: Int, listSize: Int) {
        mList!!.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, listSize)
    }

    fun showPopUpMenu(ivThreeDot: ImageView, data: ProductDescription, position: Int) {
        var popup = PopupMenu(context, ivThreeDot);
        //Inflating the Popup using xml file
        popup.menuInflater
            .inflate(R.menu.product_edit_options, popup.menu)
        val editmenu: MenuItem = popup.menu.findItem(R.id.action_edit)
        val deletemenu: MenuItem = popup.menu.findItem(R.id.action_delete)
        editmenu.title = context!!.getString(R.string.edit)
        deletemenu.title = context!!.getString(R.string.delete)
        popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item: MenuItem? ->

            when (item!!.itemId) {
                R.id.action_edit -> {
                    if (mListener != null) {
                        mListener!!.onEditProductDescription(data, position)
                    }

                }
                R.id.action_delete -> {
                    mListener!!.removeProductDescription(data, position)
                }

            }

            true
        })



        popup.show();
    }



}