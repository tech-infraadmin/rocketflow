package com.rf.taskmodule.ui.taskdetails.timeline.skuinfopreview

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

class SkuInfoPreviewViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider) :
    BaseSdkViewModel<SkuInfoPreviewNavigator>(dataManager, schedulerProvider) {

    private val tag = "SkuInfoViewModel"

    lateinit var httpManager: HttpManager
    lateinit var api: Api

    fun executiveMap(position: Int, target: String, httpManager: HttpManager, rollId:String?) {
        this.httpManager = httpManager
        ExecutiveMap(position,target,rollId).hitApi()
    }

    private inner class ExecutiveMap(val position: Int,var target :String,var rollId:String?) :
        ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            navigator.handleExecutiveMapResponse(position, this, result, error)
        }

        override fun hitApi() {
            var apiMap=ApiType.valueOf(target)
            //val api = TrackiApplication.getApiMap()[ApiType.EXECUTIVE_MAP]

            val api = TrackiSdkApplication.getApiMap()[apiMap]
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

        override fun onRequestTimeOut(callBack: ApiCallback) {
            navigator.showTimeOutMessage(callBack)
        }

        override fun onLogout() {
        }

    }

    private inner class GetUnitInfoData(val unitInfoRequest: UnitInfoRequest) :
        ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            if(navigator!=null)
                navigator.handleTaskInfoDataResponse(this, result, error)
        }

        override fun hitApi() {
            if(TrackiSdkApplication.getApiMap().containsKey(ApiType.UNIT_INFO_LISTING)) {
                val api = TrackiSdkApplication.getApiMap()[ApiType.UNIT_INFO_LISTING]
                if(dataManager != null)
                    dataManager.getUnitInfo(this, httpManager, unitInfoRequest, api)
            }
        }

        override fun isAvailable(): Boolean {
            return true
        }

        override fun onNetworkErrorClose() {
        }

        override fun onRequestTimeOut(callBack: ApiCallback) {
            if(navigator!=null)
                navigator.showTimeOutMessage(callBack)
        }

        override fun onLogout() {
        }
    }

    fun getUnitInfoData(httpManager: HttpManager, unitInfoRequest: UnitInfoRequest) {
        this.httpManager = httpManager
        GetUnitInfoData(unitInfoRequest).hitApi()
    }

    fun verifyOtpServerRequest(otpRequest: OtpRequest, httpManager: HttpManager?) {
        this.httpManager = httpManager!!
        VerifyOtp(otpRequest).hitApi()
    }

    fun uploadSkuInfoData(skuSpecsData: SKUInfoSpecsRequest, httpManager: HttpManager, api: Api?) {
        this.httpManager = httpManager
        this.api = api!!
        UploadSkuInfoList(skuSpecsData).hitApi()
    }

    inner class UploadSkuInfoList(val skuSpecsData: SKUInfoSpecsRequest) :
        ApiCallback {

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

        override fun onRequestTimeOut(callBack: ApiCallback) {
            if(navigator!=null) {
                navigator.showTimeOutMessage(callBack)
            }
        }

        override fun onLogout() {
        }

    }

    inner class VerifyOtp(val otpRequest: OtpRequest) :
        ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            if(navigator!=null)
                navigator.handleUploadFileResponse(this, result, error)
        }

        override fun hitApi() {
            if(TrackiSdkApplication.getApiMap().containsKey(ApiType.VERIFY_MOBILE)) {
                val api = TrackiSdkApplication.getApiMap()[ApiType.VERIFY_MOBILE]
                if(dataManager!=null)
                    dataManager.verifyOtp(httpManager, this, otpRequest, api)
            }
        }

        override fun isAvailable(): Boolean {
            return true
        }

        override fun onNetworkErrorClose() {
        }

        override fun onRequestTimeOut(callBack: ApiCallback) {
            if(navigator!=null)
                navigator.showTimeOutMessage(callBack)
        }

        override fun onLogout() {
        }
    }

    fun executeUpdates(httpManager: HttpManager?, request: ExecuteUpdateRequest?, api: Api?) {
        this.httpManager = httpManager!!
        this.api = api!!
        ExecuteUpdateTask(request).hitApi()
    }

    inner class ExecuteUpdateTask(var request: ExecuteUpdateRequest?) :
        ApiCallback {
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
        override fun onRequestTimeOut(callBack: ApiCallback) {
            navigator.showTimeOutMessage(callBack)
        }

        override fun onLogout() {}

    }

    internal class Factory(private val mDataManager: DataManager) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SkuInfoPreviewViewModel(mDataManager,
                AppSchedulerProvider()
            ) as T
        }
    }

}