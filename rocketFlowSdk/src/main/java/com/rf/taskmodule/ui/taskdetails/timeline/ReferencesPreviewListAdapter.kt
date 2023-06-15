package com.rf.taskmodule.ui.taskdetails.timeline

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.rf.taskmodule.R
import com.rf.taskmodule.data.model.response.config.Task
import com.rf.taskmodule.databinding.RowReferencesPreviewSdkBinding
import com.rf.taskmodule.databinding.RowReferencesSdkBinding
import com.rf.taskmodule.ui.taskdetails.NewTaskDetailsActivity
import com.rf.taskmodule.utils.AppConstants
import com.rf.taskmodule.utils.Log

class ReferencesPreviewListAdapter(var context: Context, val items: java.util.ArrayList<Task>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view: RowReferencesPreviewSdkBinding = DataBindingUtil.inflate(layoutInflater, R.layout.row_references_preview_sdk, parent, false)
        return MyPlaceListViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MyPlaceListViewHolder) {
            val data = items[position]
            holder.bind(data)
        }
    }

    fun getList(): ArrayList<Task> {
        return items
    }

    override fun getItemCount(): Int {
        if (items.isEmpty()) return 0
        return items.size
    }

    inner class MyPlaceListViewHolder(private var myPlaceBinding: RowReferencesPreviewSdkBinding) :
        RecyclerView.ViewHolder(myPlaceBinding.root) {

        fun bind(taskResponse: Task) {
            this.myPlaceBinding.data = taskResponse

            myPlaceBinding.referenceItem.setOnClickListener {
                val intent = Intent(
                    context,
                    NewTaskDetailsActivity::class.java
                )
                intent.putExtra(AppConstants.Extra.EXTRA_TASK_ID, taskResponse.taskId)
                context.startActivity(intent)
            }

            myPlaceBinding.ivReferenceDelete.setOnClickListener {
                items.remove(taskResponse)
                notifyDataSetChanged()
            }
            myPlaceBinding.executePendingBindings()
        }
    }
}