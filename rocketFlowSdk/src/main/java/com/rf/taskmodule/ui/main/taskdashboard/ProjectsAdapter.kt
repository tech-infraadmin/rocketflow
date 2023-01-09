package com.rf.taskmodule.ui.main.taskdashboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rf.taskmodule.R
import com.rf.taskmodule.data.model.response.config.ProjectCategories
import com.rf.taskmodule.utils.Log
import java.util.ArrayList


class ProjectsAdapter(
    var subjects: ArrayList<ProjectCategories>,
    var selectedProjectId: String,
    val onItemClickListener: OnItemClickListenerProject
) :
    RecyclerView.Adapter<ProjectsAdapter.ViewHolder>() {

     fun setProjectId(id: String){
        this.selectedProjectId = id
         notifyDataSetChanged()
    }
    interface OnItemClickListenerProject { fun onItemClick(item: ProjectCategories) }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.single_project_sdk, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvHeading.text = subjects[position].name

        if (subjects[position].icon!=null && subjects[position].icon!!.isNotEmpty()){
            Glide.with(holder.ivChapter.context).load(subjects[position].icon).placeholder(R.drawable.placeholder)
                .into(holder.ivChapter)
        }

        Log.d("selected", selectedProjectId)

        if(selectedProjectId == subjects[position].projectId){
            holder.selected.setBackgroundColor(ContextCompat.getColor( holder.selected.context, R.color.blue))
        }else {
            holder.selected.setBackgroundColor(ContextCompat.getColor( holder.selected.context, R.color.white))
        }

        holder.itemView.setOnClickListener {
            onItemClickListener.onItemClick(subjects[position])
        }
    }

    override fun getItemCount(): Int {
        return subjects.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvHeading: TextView
        var ivChapter: ImageView
        var cardView : LinearLayout
        var selected : View

        init {
            tvHeading = itemView.findViewById<View>(R.id.tvSubjectName) as TextView
            ivChapter = itemView.findViewById<View>(R.id.ivChapter) as ImageView
            cardView = itemView.findViewById<View>(R.id.card_view) as LinearLayout
            selected = itemView.findViewById<View>(R.id.selected) as View
        }
    }

}