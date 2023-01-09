package com.rf.taskmodule.ui.userlisting

import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.*
import android.widget.ImageView
import androidx.annotation.Nullable
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.rf.taskmodule.R
import com.rf.taskmodule.data.model.response.config.UserData
import com.rf.taskmodule.databinding.ItemMyEarningEmptySdkBinding
import com.rf.taskmodule.databinding.ItemUserNewSdkBinding
//import com.rf.taskmodule.databinding.ItemEmployeeListBinding
//import com.rf.taskmodule.ui.addcustomer.AddCustomerActivity
//import com.rf.taskmodule.ui.attendance.EmployeeListActivity
import com.rf.taskmodule.ui.base.BaseSdkViewHolder
import com.rf.taskmodule.ui.custom.GlideApp
import com.rf.taskmodule.ui.earnings.MyEarningsEmptyItemViewModel

class UserListNewAdapter : RecyclerView.Adapter<BaseSdkViewHolder> {

    private var context: Context? = null

    var mList: ArrayList<UserData>? = null
    private var copyList: ArrayList<UserData>? = null

    constructor(mList: ArrayList<UserData>?) : super() {
        this.mList = mList

    }
    companion object {
        private const val VIEW_TYPE_EMPTY = 0
        private const val VIEW_TYPE_USERS = 1
    }

    private var listener: UserListNewAdapter.onUserSelected?=null

    fun setListener(context: Context){
        listener=context as UserListNewAdapter.onUserSelected
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseSdkViewHolder {
        context = parent.context
        return when (viewType) {
            VIEW_TYPE_USERS -> {
                val simpleViewItemBinding: ItemUserNewSdkBinding =
                    ItemUserNewSdkBinding.inflate(
                                LayoutInflater.from(parent.context), parent, false)
                EmployeeListViewHolder(simpleViewItemBinding)
            }
            else -> {
                val emptyViewBinding: ItemMyEarningEmptySdkBinding =
                        ItemMyEarningEmptySdkBinding.inflate(
                                LayoutInflater.from(parent.context), parent, false)
                EmptyViewHolder(emptyViewBinding)
            }
        }
    }

    override fun getItemCount(): Int {
        return if (mList != null && mList!!.isNotEmpty()) {
            mList!!.size
        } else {
            1
        }
    }

    override fun getItemViewType(position: Int) = if (mList != null && mList!!.isNotEmpty()) {
        VIEW_TYPE_USERS
    } else {
        VIEW_TYPE_EMPTY
    }

    override fun onBindViewHolder(holder: BaseSdkViewHolder, position: Int) = holder.onBind(position)

    fun addItems(list: ArrayList<UserData>) {
        mList!!.addAll(list)
        if (copyList == null) {
            copyList = ArrayList()
        } else {
            copyList!!.clear()
        }
        if (mList != null)
            copyList!!.addAll(mList!!)
//        val set: MutableSet<EmpData> = HashSet()
//        set.addAll(mList!!)
//        mList!!.clear()
//        mList!!.addAll(set)
        notifyDataSetChanged()
    }

    fun getAllList(): ArrayList<UserData> {
        return mList!!
    }

    fun clearList() {
        if (mList!!.isNotEmpty()) {
            mList!!.clear()
            notifyDataSetChanged()
        }

    }
    fun addFilter(newText: String) {
        mList?.clear()
        if (newText.isEmpty()) {
            mList?.addAll(copyList!!)
        } else {
            for (name in copyList!!) {
                if (name.firstName != null && name.firstName!!.toLowerCase().contains(newText.toLowerCase())
                        ||name.mobile != null && name.mobile!!.contains(newText.toLowerCase())) {
                    mList?.add(name)
                }
            }
        }
        notifyDataSetChanged()
    }
    fun populateList() {
        if(mList!=null)
            mList!!.clear()
        mList?.addAll(copyList!!)
        notifyDataSetChanged()
    }

    interface onUserSelected{
        fun addUser(userId: String)
        fun removeUser(userId: String)
    }

    inner class EmployeeListViewHolder(private val mBinding: ItemUserNewSdkBinding) :
            BaseSdkViewHolder(mBinding.root) {

        override fun onBind(position: Int) {
            val data = mList!![position]
            var name=""
            if(data.firstName!=null&&data.middleName!=null&&data.lastName!=null){
                name =data.firstName+" "+data.middleName+" "+data.lastName
            }else  if(data.firstName!=null&&data.lastName!=null){
                name =data.firstName+" "+data.lastName
            }else  if(data.firstName!=null&&data.middleName!=null){
                name =data.firstName+" "+data.middleName
            }else if(data.firstName!=null){
                name=data.firstName!!
            }
            mBinding.tvName.text=name
            mBinding.data = data

            if(data.email!=null&&data.email!!.isNotEmpty()){
                mBinding.tvEmail.text = data.email
                mBinding.tvEmail.visibility=View.VISIBLE
            }else{
                mBinding.tvEmail.visibility=View.GONE
            }
            if(data.mobile!=null&&data.mobile!!.isNotEmpty()){
                mBinding.tvMobile.text = data.mobile
                mBinding.tvMobile.visibility=View.VISIBLE
            }
            else{
                mBinding.tvMobile.visibility=View.GONE
            }


            if(data.profileImg!=null && data.profileImg!!.isNotEmpty()) {
                GlideApp.with(context!!)
                        .load(data.profileImg)
                        .apply(RequestOptions().circleCrop()
                                .placeholder(R.drawable.ic_my_profile))
                        .error(R.drawable.ic_my_profile)
                        .into(mBinding.ivUser)
                mBinding.ivUser.setOnClickListener {
                    openDialogShowImage(data.profileImg!!)
                }
            }else{
                mBinding.ivUser.setImageResource(R.drawable.ic_my_profile)
            }
//            if(data.userId!=null){
//                mBinding.ivMessage.visibility=View.VISIBLE
//            }

            mBinding.ivChecked.setOnCheckedChangeListener { buttonView, isChecked ->
                if(isChecked){
                    listener!!.addUser(data.userId.toString())
                }
                else{
                    listener!!.removeUser(data.userId.toString())
                }
            }
            mBinding.cardMain.setOnClickListener {
                mBinding.ivChecked.toggle()
            }
            mBinding.executePendingBindings()
        }


    }

    inner class EmptyViewHolder(private val mBinding: ItemMyEarningEmptySdkBinding) :
            BaseSdkViewHolder(mBinding.root) {

        override fun onBind(position: Int) {
            val emptyViewModel = MyEarningsEmptyItemViewModel()
            mBinding.viewModel = emptyViewModel
            mBinding.imageViewEmpty.setImageResource(R.drawable.ic_empty_buddy)
//            if (context is EmployeeListActivity) {
//                mBinding.tvMessage.text = "No Customer"
//            }
            // Immediate Binding
            // When a variable or observable changes, the binding will be scheduled to change before
            // the next frame. There are times, however, when binding must be executed immediately.
            // To force execution, use the executePendingBindings() method.
            mBinding.executePendingBindings()
        }
    }


    private fun openDialogShowImage(url: String) {

        val dialog = Dialog(context!!)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(
                ColorDrawable(
                        Color.TRANSPARENT))
        dialog.setContentView(R.layout.layout_show_image_big)
//        dialog.window!!.attributes.windowAnimations = R.style.DialogZoomOutAnimation
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.dimAmount = 0.8f
        val window = dialog.window
        window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        window.setGravity(Gravity.CENTER)
        val imageView = dialog.findViewById<View>(R.id.ivImages) as ImageView

        Glide.with(context!!)
                .asBitmap()
                .load(url)
                .error(R.drawable.ic_picture)
                .placeholder(R.drawable.ic_picture)
                .into(object : CustomTarget<Bitmap?>() {
                    override fun onLoadCleared(@Nullable placeholder: Drawable?) {
                    }

                    override fun onResourceReady(resource: Bitmap, transition: com.bumptech.glide.request.transition.Transition<in Bitmap?>?) {
                        imageView.setImageBitmap(resource)
                    }
                })
        /*  Glide.with(context!!)
                  .load(url)
                  .error(R.drawable.ic_picture)
                  .placeholder(R.drawable.ic_picture)
                  .into(imageView)*/
        dialog.window!!.attributes = lp
        // imageView.setOnClickListener { dialog.dismiss() }
        if (!dialog.isShowing) dialog.show()
    }
}