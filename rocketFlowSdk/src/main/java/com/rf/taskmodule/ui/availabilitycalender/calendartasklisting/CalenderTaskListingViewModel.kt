package com.rf.taskmodule.ui.availabilitycalender.calendartasklisting

import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rf.taskmodule.data.DataManager
import com.rf.taskmodule.data.model.request.TaskRequestCalendar
import com.rf.taskmodule.data.model.response.config.Api
import com.rf.taskmodule.data.model.response.config.Task
import com.rf.taskmodule.data.network.APIError
import com.rf.taskmodule.data.network.ApiCallback
import com.rf.taskmodule.data.network.HttpManager
import com.rf.taskmodule.ui.base.BaseSdkViewModel
import com.rf.taskmodule.utils.rx.AppSchedulerProvider
import com.rf.taskmodule.utils.rx.SchedulerProvider

class CalenderTaskListingViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider) : BaseSdkViewModel<CalenderTaskListingNavigator>(dataManager, schedulerProvider) {


    val taskObservableArrayList: ObservableList<Task> = ObservableArrayList()

    // Create a LiveData with a List of @Task
    private var taskListLiveData: MutableLiveData<List<Task>>? = null

    fun getTaskListLiveData(): MutableLiveData<List<Task>>? {
        if (taskListLiveData == null) {
            taskListLiveData = MutableLiveData()
        }
        return taskListLiveData
    }
    private lateinit var httpManager: HttpManager

    fun getTaskList(httpManager: HttpManager, api: Api, taskRequest: TaskRequestCalendar) {
        this.httpManager = httpManager
        GetTaskList(api, taskRequest).hitApi()
    }

    inner class GetTaskList(val apiMain: Api, private val taskRequest: TaskRequestCalendar) : ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            if(navigator!=null) {
                navigator.getSlotDataResponse(this@GetTaskList, result, error)
            }
        }

        override fun hitApi() {
            if(dataManager!=null) {
                dataManager.getTasksListCalendar(this@GetTaskList, httpManager, taskRequest ,apiMain)
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
            return CalenderTaskListingViewModel(mDataManager,
                AppSchedulerProvider()
            ) as T
        }
    }

}