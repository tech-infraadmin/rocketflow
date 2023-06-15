package com.rf.taskmodule.ui.taskdetails

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.rf.taskmodule.R
import com.rf.taskmodule.databinding.ItemTimeLineSdkBinding
import com.rf.taskmodule.ui.taskdetails.newtimeline.TimelineFragment
import com.rf.taskmodule.utils.DateTimeUtil

class TaskTimeLineAdapter(var context: TimelineFragment) :
    RecyclerView.Adapter<TaskTimeLineAdapter.TimeLineViewHolder>() {

    var items: ArrayList<StageHistoryData> = ArrayList()
    public var mListener: PreviousFormListener? = null

    init {
        mListener = context as PreviousFormListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeLineViewHolder {
        var layoutInflater = LayoutInflater.from(parent.context)
        val view: ItemTimeLineSdkBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.item_time_line_sdk, parent, false)
        return TimeLineViewHolder(view)
    }

    override fun onBindViewHolder(holder: TimeLineViewHolder, position: Int) {
        var data = items[position]
        holder.bind(data)


    }

    fun addData(items: List<StageHistoryData>) {
        if (this.items.isNotEmpty()) {
            this.items.clear()
        }
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        if (items.isEmpty()) return 0
        return items.size
    }

    inner class TimeLineViewHolder(private var timeLineBinding: ItemTimeLineSdkBinding) :
        RecyclerView.ViewHolder(timeLineBinding.root) {

        fun bind(dataModel: StageHistoryData) {
            if (dataModel!!.timeStamp != null) {
                val timeDate =
                    DateTimeUtil.getParsedDate(dataModel!!.timeStamp!!) + " , " + DateTimeUtil.getParsedTime(
                        dataModel!!.timeStamp!!
                    )
                timeLineBinding.tvTimeDate.text = timeDate
            }
            if (dataModel.dfdId != null && dataModel.dfdId!!.isNotEmpty()) {
                timeLineBinding.btnDetails.visibility = View.VISIBLE
                timeLineBinding.rlTimeLine.setOnClickListener {
                    if (mListener != null)
                        mListener!!.openForm(dataModel)
                }
            } else {
                timeLineBinding.btnDetails.visibility = View.GONE
            }
            timeLineBinding.data = dataModel
            timeLineBinding.executePendingBindings()

            if (items.size == 1) {
                timeLineBinding.view3.visibility = View.GONE
                timeLineBinding.view4.visibility = View.GONE
            } else if (position == 0) {
                timeLineBinding.view3.visibility = View.GONE
                timeLineBinding.view4.visibility = View.VISIBLE
            } else if (position == items.size - 1) {
                timeLineBinding.view3.visibility = View.VISIBLE
                timeLineBinding.view4.visibility = View.GONE
            } else {
                timeLineBinding.view3.visibility = View.VISIBLE
                timeLineBinding.view4.visibility = View.VISIBLE
            }
        }

    }

    interface PreviousFormListener {
        fun openForm(dataModel: StageHistoryData)
    }

}