package com.rf.taskmodule.ui.tasklisting

import android.app.Activity
import android.content.Context
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.rf.taskmodule.R
import com.rf.taskmodule.data.model.response.config.CallToActions
import com.rf.taskmodule.data.model.response.config.Task
import com.rf.taskmodule.databinding.ItemCtaButtonSdkBinding
import com.rf.taskmodule.utils.CommonUtils


class CallToActionButtonAdapter (var context: Context, var mListener: TaskClickListener, var task: Task) :
        RecyclerView.Adapter<CallToActionButtonAdapter.CtaButtonViewHolder>() {
    var items: ArrayList<CallToActions> = ArrayList()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CtaButtonViewHolder {
        var layoutInflater = LayoutInflater.from(parent.context)
        val view: ItemCtaButtonSdkBinding = DataBindingUtil.inflate(layoutInflater, R.layout.item_cta_button_sdk, parent, false)
        return  CtaButtonViewHolder(view)
    }

    override fun onBindViewHolder(holder: CtaButtonViewHolder, position: Int) {
        var data = items[position]
        holder.bind(data)

    }

    fun addData(items: List<CallToActions>){
        if(this.items.isNotEmpty()) {
            this.items.clear()
        }
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        if (items.isEmpty()) return 0
        return items.size
    }

    inner class CtaButtonViewHolder(private var ctaButtonBinding: ItemCtaButtonSdkBinding) :
            RecyclerView.ViewHolder(ctaButtonBinding.root) {

        fun bind(dataModel: CallToActions) {

            ctaButtonBinding.data=dataModel
            ctaButtonBinding.executePendingBindings()
            if(dataModel.name.toString().toUpperCase().contains("CANCEL")||dataModel.name.toString().toUpperCase().contains("REJECT"))
            {
                ctaButtonBinding.ctaButton.setTextColor(ContextCompat.getColor(context,R.color.white))
            }else if(dataModel.primary){
                ctaButtonBinding.ctaButton.background=ContextCompat.getDrawable(context,R.drawable.button_green_bg)
            }

            ctaButtonBinding.ctaButton.tag=dataModel.id
            ctaButtonBinding.ctaButton.setOnClickListener {v->

                // used to perform the action when view is clicked.
                mListener.onExecuteUpdates(v.getTag() as String, task, dataModel.name)
            }

        }

    }



}