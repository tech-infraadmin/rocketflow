package com.rf.taskmodule.ui.earnings

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import com.rf.taskmodule.TrackiSdkApplication
import com.rf.taskmodule.data.DataManager
import com.rf.taskmodule.data.model.request.MyEarningRequest
import com.rf.taskmodule.data.network.APIError
import com.rf.taskmodule.data.network.ApiCallback
import com.rf.taskmodule.data.network.HttpManager
import com.rf.taskmodule.ui.base.BaseSdkViewModel
import com.rf.taskmodule.utils.ApiType
import com.rf.taskmodule.utils.AppConstants
import com.rf.taskmodule.utils.rx.SchedulerProvider

/**
 * Created by Rahul Abrol on 27/12/19.
 */
class MyEarningsViewModel(dataManager: com.rf.taskmodule.data.DataManager, schedulerProvider: com.rf.taskmodule.utils.rx.SchedulerProvider) :
        com.rf.taskmodule.ui.base.BaseSdkViewModel<MyEarningsNavigator>(dataManager, schedulerProvider) {

    val isTodayEarning = ObservableBoolean(true)
    val totalRides = ObservableField<String>("0")
    val totalEarnings = ObservableField<String>(com.rf.taskmodule.utils.AppConstants.INR + " 0")
    val dateRange = ObservableField<String>("Select Date Range")

    fun setDateRange(string: String) {
        dateRange.set(string)
    }

    fun selectDateRange() {
        navigator.selectDateRange()
    }

    fun viewDetails() {
        navigator.viewDetails()
    }

    fun search() {
        navigator.search()
    }

    private lateinit var httpManager: com.rf.taskmodule.data.network.HttpManager

    fun getMyEarnings(httpManager: com.rf.taskmodule.data.network.HttpManager, earningRequest: MyEarningRequest) {
        this.httpManager = httpManager
        GetMyEarnings(earningRequest).hitApi()
    }

    inner class GetMyEarnings(private val earningRequest: MyEarningRequest) :
        com.rf.taskmodule.data.network.ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            navigator.handleResponse(this, result, error)
        }

        override fun hitApi() {
            val api = com.rf.taskmodule.TrackiSdkApplication.getApiMap()[ApiType.MY_EARNINGS]!!
            dataManager.getMyEarnings(this, httpManager, earningRequest, api)
        }

        override fun isAvailable(): Boolean {
            return true
        }

        override fun onNetworkErrorClose() {
        }

        override fun onRequestTimeOut(callBack: com.rf.taskmodule.data.network.ApiCallback) {
            navigator.showTimeOutMessage(callBack)
        }

        override fun onLogout() {
        }
    }


}