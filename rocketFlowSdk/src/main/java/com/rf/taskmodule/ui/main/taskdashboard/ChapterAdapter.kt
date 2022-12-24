package com.rf.taskmodule.ui.main.taskdashboard

import androidx.recyclerview.widget.RecyclerView
import com.rf.taskmodule.ui.main.taskdashboard.ChapterAdapter.CustomViewHolder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import com.rf.taskmodule.R
import com.bumptech.glide.Glide
import android.widget.TextView
import com.rf.taskmodule.data.model.response.config.Category
import com.rf.taskmodule.ui.custom.GlideApp
import com.rf.taskmodule.utils.Log

class ChapterAdapter(
    private var selectedCategory: String,
    private var chapters: ArrayList<Category>,
    val onItemClickListener: OnItemClickListenerProject
) :
    RecyclerView.Adapter<CustomViewHolder>() {

    interface OnItemClickListenerProject { fun onItemClick(item: Category) }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.single_chapter_sdk, parent, false)
        return CustomViewHolder(view)
    }


    fun setCat(id: String){
        this.selectedCategory = id
        notifyDataSetChanged()
    }

    fun setCatAndItems(id: String,items: ArrayList<Category>){
        this.selectedCategory = id
        this.chapters = items
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val (_, name, icon) = chapters[position]
        holder.tvChapterName.text = name
        if (icon!=null && icon.isNotEmpty()){
            try {
                GlideApp.with(holder.ivChapter.context).load(icon).placeholder(R.drawable.placeholder)
                    .into(holder.ivChapter)
            }catch (e:java.lang.Exception){
                e.printStackTrace()
            }
        }

        com.rf.taskmodule.utils.Log.d("selected", selectedCategory);
        if(selectedCategory == chapters[position].categoryId){
            holder.cardView.setBackgroundResource(R.drawable.layout_bg_red)
        }else {
            holder.cardView.setBackgroundResource(R.drawable.layout_bg)
        }
        holder.itemView.setOnClickListener {
            onItemClickListener?.onItemClick(chapters[position])
        }
    }

    override fun getItemCount(): Int {
        return chapters.size
    }

    inner class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var ivChapter: ImageView
        var tvChapterName: TextView
        var cardView : LinearLayout
        init {
            tvChapterName = itemView.findViewById<View>(R.id.tvChapterName) as TextView
            ivChapter = itemView.findViewById<View>(R.id.ivChapter) as ImageView
            cardView = itemView.findViewById<View>(R.id.card_view) as LinearLayout
        }
    }

}