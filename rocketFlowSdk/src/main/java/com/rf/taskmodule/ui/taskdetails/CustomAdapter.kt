package com.rf.taskmodule.ui.taskdetails

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.RequestOptions
import com.rf.taskmodule.R
import com.rf.taskmodule.databinding.UserDetailsCardBinding
import com.rf.taskmodule.ui.custom.GlideApp
import com.rf.taskmodule.utils.CommonUtils


class CustomAdapter(
    private val heroList: ArrayList<UserDetails>,
    private val listener: (UserDetails, Int) -> Unit
) : RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = UserDetailsCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(heroList[position])
        holder.itemView.setOnClickListener { listener(heroList[position], position) }
    }

    override fun getItemCount(): Int {
        return heroList.size
    }

    class ViewHolder(private var itemBinding: UserDetailsCardBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bindItem(hero: UserDetails) {
            itemBinding.textViewTitle.text = hero.title
            if (!hero.image.isNullOrEmpty()) {
                GlideApp.with(itemBinding.textView11.context)
                    .asBitmap()
                    .load(itemBinding.textView11)
                    .apply(
                        RequestOptions()
                            .transform(com.rf.taskmodule.ui.custom.CircleTransform())
                            .placeholder(R.drawable.ic_user)
                    ).error(R.drawable.ic_user)
            }

            itemBinding.textViewName.text = hero.name
            itemBinding.textViewMobile.text = hero.phone
            if (hero.email.isNullOrEmpty()) {
                itemBinding.textView15.visibility = View.GONE
            } else {
                itemBinding.textView15.visibility = View.VISIBLE
                itemBinding.textViewEmail.text = hero.email
            }
        }
    }
}
