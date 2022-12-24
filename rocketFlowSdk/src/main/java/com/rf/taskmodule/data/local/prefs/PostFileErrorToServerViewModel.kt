package com.rf.taskmodule.data.local.prefs


import com.rf.taskmodule.data.DataManager
import com.rf.taskmodule.data.model.response.config.ApiErrorRequest

import com.rf.taskmodule.data.network.APIError
import com.rf.taskmodule.data.network.ApiCallback
import com.rf.taskmodule.data.network.HttpManager
import com.rf.taskmodule.ui.base.BaseSdkViewModel
import com.rf.taskmodule.utils.rx.SchedulerProvider

class PostFileErrorToServerViewModel  (dataManager: com.rf.taskmodule.data.DataManager, schedulerProvider: com.rf.taskmodule.utils.rx.SchedulerProvider) :
    com.rf.taskmodule.ui.base.BaseSdkViewModel<com.rf.taskmodule.data.local.prefs.PostErrorToServerNavigator>(dataManager, schedulerProvider) {

    private lateinit var httpManager: com.rf.taskmodule.data.network.HttpManager


    fun postFileErrorToServer(httpManager: com.rf.taskmodule.data.network.HttpManager, request:ApiErrorRequest) {
        this.httpManager = httpManager
        PostErrorServer(request).hitApi()
    }

    inner class PostErrorServer(var request: ApiErrorRequest?) :
        com.rf.taskmodule.data.network.ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            if(navigator!=null)
                navigator.handlePostErrorResponse(this, result, error)
        }

        override fun hitApi() {
//            if(TrackiApplication.getApiMap().containsKey(ApiType.APP_ERROR)) {
//                val oldApi = TrackiApplication.getApiMap()[ApiType.APP_ERROR]!!
//
//                if(dataManager!=null)
//                    dataManager.postFileErrorToServer(this, httpManager,request, oldApi)
//            }
        }

        override fun isAvailable(): Boolean {
            return true
        }

        override fun onNetworkErrorClose() {
        }

        override fun onRequestTimeOut(callBack: com.rf.taskmodule.data.network.ApiCallback) {
            if(navigator!=null)
                navigator.showTimeOutMessage(callBack)
        }

        override fun onLogout() {
        }
    }



}