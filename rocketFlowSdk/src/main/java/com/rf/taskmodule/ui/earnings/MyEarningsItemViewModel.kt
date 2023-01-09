package com.rf.taskmodule.ui.earnings

import androidx.databinding.ObservableField
import com.rf.taskmodule.data.model.response.config.MyEarning
import com.rf.taskmodule.utils.AppConstants
import com.rf.taskmodule.utils.DateTimeUtil
import com.rf.taskmodule.utils.DateTimeUtil.Companion.DATE_FORMAT_2

/**
 * Created by Rahul Abrol on 29/12/19.
 */
class MyEarningsItemViewModel(val earning: MyEarning, val listener: MyEarningsItemListener) {
    val totalRides = ObservableField<String>("0 ${AppConstants.RIDES}")
    val totalEarning = ObservableField<String>("0")
    val date = ObservableField<String>("")

    init {
        if (earning.totalRide > 0) {
            if (earning.totalRide > 1) {
                totalRides.set("${earning.totalRide} ${AppConstants.RIDES}")
            } else {
                totalRides.set("${earning.totalRide} ${AppConstants.RIDE}")
            }
        }
        totalEarning.set("${AppConstants.INR} ${earning.totalAmount}")
        date.set(DateTimeUtil.getParsedDate(earning.date, DATE_FORMAT_2))
    }

    fun onRideClick() {
        listener.onClickItem(earning.date)
    }

    interface MyEarningsItemListener {
        fun onClickItem(date: Long)
    }
}