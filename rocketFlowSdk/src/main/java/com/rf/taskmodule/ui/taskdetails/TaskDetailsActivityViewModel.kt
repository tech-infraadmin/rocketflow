package com.rf.taskmodule.ui.taskdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rf.taskmodule.TrackiSdkApplication
import com.rf.taskmodule.data.DataManager
import com.rf.taskmodule.data.model.request.AcceptRejectRequest
import com.rf.taskmodule.data.model.response.config.Api
import com.rf.taskmodule.data.network.APIError
import com.rf.taskmodule.data.network.ApiCallback
import com.rf.taskmodule.data.network.HttpManager
import com.rf.taskmodule.ui.base.BaseSdkViewModel
import com.rf.taskmodule.utils.ApiType
import com.rf.taskmodule.utils.rx.AppSchedulerProvider
import com.rf.taskmodule.utils.rx.SchedulerProvider


/**
 * Created by Vikas Kesharvani on 16/09/20.
 * rocketflyer technology pvt. ltd
 * vikas.kesharvani@rocketflyer.in
 */
class TaskDetailsActivityViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider) :
        BaseSdkViewModel<NewTaskDetailNavigator>(dataManager, schedulerProvider) {

        private val tag = "TaskDetailsActivityViewModel"

        lateinit var httpManager: HttpManager
        lateinit var request: AcceptRejectRequest
        lateinit var api: Api
        var geoId: String = ""
        var date: String = ""

        fun getTaskDetails(httpManager: HttpManager, taskId: String?) {
                this.httpManager = httpManager
                GetTaskDetails(taskId).hitApi()
        }

        inner class GetTaskDetails(var taskId: String?) : ApiCallback {

                override fun onResponse(result: Any?, error: APIError?) {
                        if (navigator != null)
                                navigator.handleResponse(this@GetTaskDetails, result, error)
                }

                override fun hitApi() {
                        if (dataManager != null) {
                                val api = TrackiSdkApplication.getApiMap()[ApiType.GET_TASK_BY_ID]!!
                                val request= AcceptRejectRequest(taskId!!)
                                if (dataManager != null)
                                        dataManager.getTaskById(this, httpManager, request, api)
                        }
                }

                override fun isAvailable() = true

                override fun onNetworkErrorClose() {
                }

                override fun onRequestTimeOut(callBack: ApiCallback) {
                        if (navigator != null)
                                navigator.showTimeOutMessage(callBack)
                }

                override fun onLogout() {
                }
        }

        internal class Factory(private val mDataManager: DataManager) : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return TaskDetailsActivityViewModel(mDataManager,
                            AppSchedulerProvider()
                        ) as T
                }
        }
}