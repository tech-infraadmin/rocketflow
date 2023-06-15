package com.rf.taskmodule.ui.availabilitycalender

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rf.taskmodule.TrackiSdkApplication
import com.rf.taskmodule.data.DataManager
import com.rf.taskmodule.data.model.request.SystemHubRequest
import com.rf.taskmodule.data.model.response.config.Api
import com.rf.taskmodule.data.network.APIError
import com.rf.taskmodule.data.network.ApiCallback
import com.rf.taskmodule.data.network.HttpManager
import com.rf.taskmodule.ui.base.BaseSdkViewModel
import com.rf.taskmodule.utils.ApiType
import com.rf.taskmodule.utils.rx.AppSchedulerProvider
import com.rf.taskmodule.utils.rx.SchedulerProvider

class AvailabilityCalenderViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider) : BaseSdkViewModel<AvailabilityCalenderNavigator>(dataManager, schedulerProvider) {

    private lateinit var httpManager: HttpManager
    private lateinit var api: Api
    var geoId: String = ""
    var date: String = ""

    fun getSlotAvailability(httpManager: HttpManager, api: Api, geoId: String, date: String) {
        this.httpManager = httpManager
        this.api = api
        this.geoId = geoId
        this.date = date
        SlotDataAPI().hitApi()
    }

    inner class SlotDataAPI: ApiCallback {

        var api = TrackiSdkApplication.getApiMap()[ApiType.GET_TIME_SLOTS]!!

        override fun onResponse(result: Any?, error: APIError?) {
            if(navigator!=null) {
                navigator.getSlotDataResponse(this@SlotDataAPI, result, error)
            }
        }

        override fun hitApi() {
            val apiMain = TrackiSdkApplication.getApiMap()[ApiType.GET_TIME_SLOTS]!!
            val api = Api()
            api.name = ApiType.GET_TIME_SLOTS
            api.timeOut = apiMain.timeOut
            api.url = "${apiMain.url}?geoId=$geoId&date=$date"
            if(dataManager!=null) {
                dataManager.timeSlotData(this@SlotDataAPI, httpManager, api)
            }
        }

        override fun isAvailable(): Boolean {
            return true
        }

        override fun onNetworkErrorClose() {
        }

        override fun onRequestTimeOut(callBack: ApiCallback?) {
            if(navigator!=null) {
                if (callBack != null) {
                    navigator.showTimeOutMessage(callBack)
                }
            }
        }

        override fun onLogout() {
        }

    }

    internal class Factory(private val mDataManager: DataManager) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AvailabilityCalenderViewModel(mDataManager,
                AppSchedulerProvider()
            ) as T
        }
    }

}