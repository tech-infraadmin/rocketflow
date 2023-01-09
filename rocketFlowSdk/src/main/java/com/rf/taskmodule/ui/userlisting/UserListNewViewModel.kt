package com.rf.taskmodule.ui.userlisting

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rf.taskmodule.TrackiSdkApplication
import com.rf.taskmodule.data.DataManager
import com.rf.taskmodule.data.model.request.AttendanceReq
import com.rf.taskmodule.data.model.request.ExecuteUpdateRequest
import com.rf.taskmodule.data.model.request.UserGetRequest
import com.rf.taskmodule.data.network.APIError
import com.rf.taskmodule.data.network.ApiCallback
import com.rf.taskmodule.data.network.HttpManager
import com.rf.taskmodule.ui.base.BaseSdkViewModel
import com.rf.taskmodule.utils.ApiType
import com.rf.taskmodule.utils.rx.AppSchedulerProvider
import com.rf.taskmodule.utils.rx.SchedulerProvider

class UserListNewViewModel (dataManager: DataManager, schedulerProvider: SchedulerProvider) :
        BaseSdkViewModel<UserListNewNavigator>(dataManager, schedulerProvider) {

    private lateinit var httpManager: HttpManager


    fun getUserList(
        httpManager: HttpManager,
        roleId: String?,
        type: String?,
        attendanceReq: AttendanceReq?,
        new: Boolean = false
    ) {
        this.httpManager = httpManager
        GetUserList(roleId, type, attendanceReq, new).hitApi()
    }

    inner class GetUserList(
        var roleId: String?,
        var type: String?,
        var attendanceReq: AttendanceReq?,
        var new: Boolean
    ) : ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            if (navigator != null)
                navigator.handleResponse(this, result, error)
        }

        override fun hitApi() {
            if (TrackiSdkApplication.getApiMap().containsKey(ApiType.GET_USERS)) {
                val oldApi = TrackiSdkApplication.getApiMap()[ApiType.GET_USERS]!!
                val userGetRequest = UserGetRequest()
                val api = oldApi
                if (new) {
                    Log.e("urlCheck", "${api.url}")
                    val array = roleId?.split(",")?.toTypedArray()
                    val arrayList = ArrayList<String>()
                    if (array != null) {
                        for (item in array){
                            arrayList.add(item)
                        }
                    }
                    userGetRequest.roleIds = arrayList
                } else {
                    //https://qa2.rocketflyer.in/rfapi/secure/tracki/user/get?pagination.datalimit=10&pagination.pageOffset=1&pagination.pageNumbers=1&pagination.pageIndex=1&pagination.dataCount=10&roleId=ad14fa67-d27f-4762-b3e3-48d58a45365f
                    if (roleId != null && roleId != "-1") {
                        //api.url = oldApi.url+"?roleId="+roleId
                        //https://qa2.rocketflyer.in/rfapi/secure/tracki/user/get?roleId=ad14fa67-d27f-4762-b3e3-48d58a45365f&offset=0&limit=10
                        userGetRequest.limit = attendanceReq!!.limit
                        userGetRequest.offset = attendanceReq!!.offset
                        userGetRequest.roleId = roleId
                    } else if (type != null) {
                        // api.url = oldApi.url+"?type="+type

                        userGetRequest.limit = attendanceReq!!.limit
                        userGetRequest.offset = attendanceReq!!.offset
                        userGetRequest.type = type
                        //  api.url = oldApi.url+"?type="+type
                    } else {
                        api.url = oldApi.url
                    }
                }
                api.name = ApiType.GET_USERS
                api.timeOut = oldApi.timeOut
                if (dataManager != null)
                    dataManager.getUserList(this, httpManager, api,userGetRequest)
            }
        }

        override fun isAvailable(): Boolean {
            return true
        }

        override fun onNetworkErrorClose() {
        }

        override fun onRequestTimeOut(callBack: ApiCallback) {
            if (navigator != null)
                navigator.showTimeOutMessage(callBack)
        }

        override fun onLogout() {
        }
    }

    fun executeUpdates(httpManager: HttpManager?, request: ExecuteUpdateRequest?) {
        this.httpManager = httpManager!!
        ExecuteUpdateTask(request).hitApi()
    }

    inner class ExecuteUpdateTask(var request: ExecuteUpdateRequest?) :
        ApiCallback {
        override fun onResponse(result: Any?, error: APIError?) {
            navigator.handleExecuteUpdateResponse(this, result, error)
        }

        override fun hitApi() {
            val api = TrackiSdkApplication.getApiMap()[ApiType.EXECUTE_UPDATE]
            dataManager.executeUpdateTask(this, httpManager, request, api)
        }

        override fun isAvailable(): Boolean {
            return true
        }

        override fun onNetworkErrorClose() {}
        override fun onRequestTimeOut(callBack: ApiCallback) {
            navigator.showTimeOutMessage(callBack)
        }

        override fun onLogout() {}

    }


    internal class Factory(private val mDataManager: DataManager) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return UserListNewViewModel(mDataManager,
                AppSchedulerProvider()
            ) as T
        }
    }
}
