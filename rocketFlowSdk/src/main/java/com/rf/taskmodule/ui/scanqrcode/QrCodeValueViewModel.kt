package com.rf.taskmodule.ui.scanqrcode

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rf.taskmodule.TrackiSdkApplication
import com.rf.taskmodule.data.DataManager
import com.rf.taskmodule.data.model.request.AcceptRejectRequest
import com.rf.taskmodule.data.model.request.QrScanRequest
import com.rf.taskmodule.data.model.response.config.Api
import com.rf.taskmodule.data.network.APIError
import com.rf.taskmodule.data.network.ApiCallback
import com.rf.taskmodule.data.network.HttpManager
import com.rf.taskmodule.ui.base.BaseSdkViewModel
import com.rf.taskmodule.utils.ApiType
import com.rf.taskmodule.utils.Log
import com.rf.taskmodule.utils.rx.AppSchedulerProvider
import com.rf.taskmodule.utils.rx.SchedulerProvider

class QrCodeValueViewModel (dataManager: DataManager, schedulerProvider: SchedulerProvider) :
    BaseSdkViewModel<QrCodeNavigator>(dataManager, schedulerProvider) {

    private lateinit var httpManager: HttpManager



    fun getQrCodeValue(httpManager: HttpManager, id: String) {
        this.httpManager = httpManager
        GetQrCodeValue(id).hitApi()
    }

    inner class GetQrCodeValue(var id: String) :
        ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            Log.e("qrResp","$result - error=>${error?.errorType}")
            navigator.handleQrCodeResponse(this, result, error)
        }

        override fun hitApi() {
            if (TrackiSdkApplication.getApiMap().containsKey(ApiType.ENTITY_SCANNER)) {
                val apiMain = TrackiSdkApplication.getApiMap()[ApiType.ENTITY_SCANNER]!!
                val api = Api()
                api.name = ApiType.ENTITY_SCANNER
                api.timeOut = apiMain.timeOut
                api.url = apiMain.url!!

                val qrScanRequest = QrScanRequest()
                qrScanRequest.code = id

                Log.e("dataManager","$dataManager  api=>${apiMain.url}")


                if(dataManager!=null){
                    dataManager.getQrCodeValue(this, httpManager, api, qrScanRequest)
                }

            }
        }

        override fun isAvailable(): Boolean {
            return true
        }

        override fun onNetworkErrorClose() {
        }

        override fun onRequestTimeOut(callBack: ApiCallback) {
            navigator.showTimeOutMessage(callBack)
        }

        override fun onLogout() {
        }
    }

    fun getUserDetail(httpManager: HttpManager, userId: String?) {
        this.httpManager = httpManager
        GetUserDetails(userId).hitApi()
    }

    inner class GetUserDetails(var userId: String?) :
        ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            if (navigator != null)
                navigator.handleUserDetailsResponse(this@GetUserDetails, result, error)
        }

        override fun hitApi() {
            if (dataManager != null) {
                val oldApi = TrackiSdkApplication.getApiMap()[ApiType.USER_DETAIL]!!
                val api = Api()
                if (userId != null) {
                    api.url = oldApi.url + "?fetchAddress=false&userId=${userId}"
                } else {
                    api.url = oldApi.url
                }
                api.name = ApiType.USER_DETAIL
                api.timeOut = oldApi.timeOut
                if (dataManager != null)
                    dataManager.getUserDetails(this, httpManager, api)
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

    fun getTaskDetails(httpManager: HttpManager, taskId: String?) {
        this.httpManager = httpManager
        GetTaskDetails(taskId).hitApi()
    }

    inner class GetTaskDetails(var taskId: String?) :
        ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            if (navigator != null)
                navigator.handleResponse(this@GetTaskDetails, result, error)
        }

        override fun hitApi() {
            if (dataManager != null) {
                val api = TrackiSdkApplication.getApiMap()[ApiType.GET_TASK_BY_ID]!!
                var request= AcceptRejectRequest(taskId!!)
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

    fun getProductDetails(
        httpManager: HttpManager,
        pid: String?,
    ) {
        this.httpManager = httpManager
        GetProductDetails(pid).hitApi()
    }

    inner class GetProductDetails(
        var pid: String?,

        ) : ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            if (navigator != null)
                navigator.handleProductDetailsResponse(this, result, error,pid)
        }

        override fun hitApi() {
            if (TrackiSdkApplication.getApiMap().containsKey(ApiType.PRODUCT_DETAIL)) {
                val oldApi = TrackiSdkApplication.getApiMap()[ApiType.PRODUCT_DETAIL]!!
                var apiUrl = Api()
                if (pid != null) {
                    apiUrl.url =
                        oldApi.url + "?pid=" + pid
                } else {
                    apiUrl.url = oldApi.url
                }
                apiUrl.name = ApiType.PRODUCT_DETAIL
                apiUrl.timeOut = oldApi.timeOut
                if (dataManager != null)
                    dataManager.getProductDetails(this, httpManager, apiUrl)
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

    internal class Factory(private val mDataManager: DataManager) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return QrCodeValueViewModel(mDataManager,
                AppSchedulerProvider()
            ) as T
        }
    }

}