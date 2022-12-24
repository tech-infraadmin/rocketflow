package com.rf.taskmodule.ui.taskdetails.timeline.skuinfo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rf.taskmodule.TrackiSdkApplication
import com.rf.taskmodule.data.DataManager
import com.rf.taskmodule.data.model.request.ExecuteUpdateRequest
import com.rf.taskmodule.data.model.request.OtpRequest
import com.rf.taskmodule.data.model.request.SKUInfoSpecsRequest
import com.rf.taskmodule.data.model.response.config.Api
import com.rf.taskmodule.data.network.APIError
import com.rf.taskmodule.data.network.ApiCallback
import com.rf.taskmodule.data.network.HttpManager
import com.rf.taskmodule.ui.base.BaseSdkViewModel
import com.rf.taskmodule.utils.ApiType
import com.rf.taskmodule.utils.rx.AppSchedulerProvider
import com.rf.taskmodule.utils.rx.SchedulerProvider
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class SkuInfoViewModel(dataManager: com.rf.taskmodule.data.DataManager, schedulerProvider: com.rf.taskmodule.utils.rx.SchedulerProvider) :
    com.rf.taskmodule.ui.base.BaseSdkViewModel<SkuInfoNavigator>(dataManager, schedulerProvider) {

    private val tag = "SkuInfoViewModel"

    lateinit var httpManager: com.rf.taskmodule.data.network.HttpManager
    lateinit var api: Api

    fun executiveMap(position: Int, target: String, httpManager: com.rf.taskmodule.data.network.HttpManager, rollId:String?) {
        this.httpManager = httpManager
        ExecutiveMap(position,target,rollId).hitApi()
    }

    private inner class ExecutiveMap(val position: Int,var target :String,var rollId:String?) :
        com.rf.taskmodule.data.network.ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            navigator.handleExecutiveMapResponse(position, this, result, error)
        }

        override fun hitApi() {
            var apiMap=ApiType.valueOf(target)
            //val api = TrackiApplication.getApiMap()[ApiType.EXECUTIVE_MAP]

            val api = com.rf.taskmodule.TrackiSdkApplication.getApiMap()[apiMap]
            if(rollId!=null){
                var queryString="?roleIds="+ URLEncoder.encode(rollId, StandardCharsets.UTF_8.toString())
                var apiNew=Api()
                apiNew.url=api!!.url+queryString
                apiNew.name=api.name
                apiNew.timeOut=api!!.timeOut
                apiNew.cacheable=api!!.cacheable
                dataManager.executeMap(this@ExecutiveMap, httpManager, apiNew)

            }else{
                dataManager.executeMap(this@ExecutiveMap, httpManager, api)
            }
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

    private inner class GetTaskData(var taskId:String?) :
        com.rf.taskmodule.data.network.ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            if(navigator!=null)
                navigator.handleTaskInfoDataResponse(this, result, error)
        }

        override fun hitApi() {
            if(com.rf.taskmodule.TrackiSdkApplication.getApiMap().containsKey(ApiType.TASK_UNIT_CONFIG)) {
                val api = com.rf.taskmodule.TrackiSdkApplication.getApiMap()[ApiType.TASK_UNIT_CONFIG]
                val apiUrl = Api()
                apiUrl.url = api!!.url+ "?taskId=${taskId}"
                apiUrl.name = ApiType.TASK_UNIT_CONFIG
                apiUrl.timeOut = api.timeOut
                if(dataManager!=null)
                    dataManager.ctaInfo(this, httpManager, apiUrl)
            }
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

    fun getTaskData(httpManager: com.rf.taskmodule.data.network.HttpManager, taskId:String?) {
        this.httpManager = httpManager
        GetTaskData(taskId).hitApi()
    }

    fun verifyOtpServerRequest(otpRequest: OtpRequest, httpManager: com.rf.taskmodule.data.network.HttpManager?) {
        this.httpManager = httpManager!!
        VerifyOtp(otpRequest).hitApi()
    }

    fun uploadSkuInfoData(skuSpecsData: SKUInfoSpecsRequest, httpManager: com.rf.taskmodule.data.network.HttpManager, api: Api?) {
        this.httpManager = httpManager
        this.api = api!!
        UploadSkuInfoList(skuSpecsData).hitApi()
    }

    inner class UploadSkuInfoList(val skuSpecsData: SKUInfoSpecsRequest) :
        com.rf.taskmodule.data.network.ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            if(navigator!=null) {
                navigator.handleResponse(this, result, error)
            }
        }

        override fun hitApi() {
            if(dataManager!=null) {
                dataManager.uploadSkuInfoList(this, httpManager, skuSpecsData, api)
            }
        }

        override fun isAvailable(): Boolean {
            return true
        }

        override fun onNetworkErrorClose() {
        }

        override fun onRequestTimeOut(callBack: com.rf.taskmodule.data.network.ApiCallback) {
            if(navigator!=null) {
                navigator.showTimeOutMessage(callBack)
            }
        }

        override fun onLogout() {
        }

    }

    inner class VerifyOtp(val otpRequest: OtpRequest) :
        com.rf.taskmodule.data.network.ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            if(navigator!=null)
                navigator.handleUploadFileResponse(this, result, error)
        }

        override fun hitApi() {
            if(com.rf.taskmodule.TrackiSdkApplication.getApiMap().containsKey(ApiType.VERIFY_MOBILE)) {
                val api = com.rf.taskmodule.TrackiSdkApplication.getApiMap()[ApiType.VERIFY_MOBILE]
                if(dataManager!=null)
                    dataManager.verifyOtp(httpManager, this, otpRequest, api)
            }
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

    fun executeUpdates(httpManager: com.rf.taskmodule.data.network.HttpManager?, request: ExecuteUpdateRequest?, api: Api?) {
        this.httpManager = httpManager!!
        this.api = api!!
        ExecuteUpdateTask(request).hitApi()
    }

    inner class ExecuteUpdateTask(var request: ExecuteUpdateRequest?) :
        com.rf.taskmodule.data.network.ApiCallback {
        override fun onResponse(result: Any?, error: APIError?) {
            navigator.handleExecuteUpdateResponse(this, result, error)
        }

        override fun hitApi() {
            dataManager.executeUpdateTask(this, httpManager, request, api)
        }

        override fun isAvailable(): Boolean {
            return true
        }

        override fun onNetworkErrorClose() {}
        override fun onRequestTimeOut(callBack: com.rf.taskmodule.data.network.ApiCallback) {
            navigator.showTimeOutMessage(callBack)
        }

        override fun onLogout() {}

    }


    internal class Factory(private val mDataManager: com.rf.taskmodule.data.DataManager) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SkuInfoViewModel(mDataManager,
                com.rf.taskmodule.utils.rx.AppSchedulerProvider()
            ) as T
        }
    }


}