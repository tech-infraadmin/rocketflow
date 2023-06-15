package com.rf.taskmodule.ui.facility

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rf.taskmodule.TrackiSdkApplication
import com.rf.taskmodule.data.DataManager
import com.rf.taskmodule.data.model.request.UpdateServiceRequest
import com.rf.taskmodule.data.model.response.config.Api
import com.rf.taskmodule.data.network.APIError
import com.rf.taskmodule.data.network.ApiCallback
import com.rf.taskmodule.data.network.HttpManager
import com.rf.taskmodule.ui.base.BaseSdkViewModel
import com.rf.taskmodule.ui.facility.ServiceNavigator
import com.rf.taskmodule.utils.ApiType
import com.rf.taskmodule.utils.NextScreen
import com.rf.taskmodule.utils.rx.AppSchedulerProvider
import com.rf.taskmodule.utils.rx.SchedulerProvider

class ServiceViewModel (dataManager: DataManager, schedulerProvider: SchedulerProvider) :
        BaseSdkViewModel<ServiceNavigator>(dataManager, schedulerProvider) {

    private lateinit var httpManager: HttpManager
    private lateinit var api: Api


    fun updateService(httpManager: HttpManager, api: Api, data: UpdateServiceRequest) {
        this.httpManager = httpManager
        this.api = api
        UpdateService(data).hitApi()
    }

    /**
     *
     */
    inner class UpdateService(var data: UpdateServiceRequest) : ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            navigator.handleUpdateServiceResponse(this@UpdateService, result, error)
        }

        override fun hitApi() {
            dataManager.updateService(this@UpdateService, httpManager, data, api)
        }

        override fun isAvailable() = true

        override fun onNetworkErrorClose() {
        }

        override fun onRequestTimeOut(callBack: ApiCallback) {
            navigator.showTimeOutMessage(callBack)
        }

        override fun onLogout() {
        }
    }


    fun getConfig(
        httpManager: HttpManager?,
        nextScreen: NextScreen?,
        sdkAccessId: String?,
        configVersion: String
    ) {
        this.httpManager = httpManager!!
        Config(nextScreen, sdkAccessId,configVersion).hitApi()
    }

    inner class Config(
        var nextScreen: NextScreen?,
        var sdkAccessId: String?,
        var configVersion: String
    ) : ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            navigator.handleConfigResponse(this, result, error, nextScreen, sdkAccessId)
        }

        override fun hitApi() {
            dataManager.getConfig(httpManager, this,configVersion)
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

    fun getPunchInPunchOutData(httpManager: HttpManager?, nextScreen: NextScreen?) {
        this.httpManager = httpManager!!
        PunchInPunchOutData(nextScreen).hitApi()
    }

    inner class PunchInPunchOutData(var nextScreen: NextScreen?) : ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            navigator.handlePunchInPunchOutResponse(this, result, error, nextScreen)
        }

        override fun hitApi() {
            val apiUrl = TrackiSdkApplication.getApiMap()[ApiType.GET_LAST_PUNCH]
            dataManager.punchInPunchOutData(this, httpManager, apiUrl)
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


    fun getServices(httpManager: HttpManager, api: Api) {
        this.httpManager = httpManager
        this.api = api
        GetSavedServices().hitApi()
    }

    /**
     *
     */
    inner class GetSavedServices() : ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            navigator.handleGetSavedServiceResponse(this@GetSavedServices, result, error)
        }

        override fun hitApi() {
            dataManager.getSavedServices(this@GetSavedServices, httpManager, api)
        }

        override fun isAvailable() = true

        override fun onNetworkErrorClose() {
        }

        override fun onRequestTimeOut(callBack: ApiCallback) {
            navigator.showTimeOutMessage(callBack)
        }

        override fun onLogout() {
        }
    }

    fun updateSavedService(httpManager: HttpManager, api: Api, data: UpdateServiceRequest) {
        this.httpManager = httpManager
        this.api = api
        UpdateSavedService(data).hitApi()
    }

    /**
     *
     */
    inner class UpdateSavedService(var data: UpdateServiceRequest) : ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            navigator.handleUpdateSavedServiceResponse(this@UpdateSavedService, result, error)
        }

        override fun hitApi() {
            dataManager.updateSavedServices(this@UpdateSavedService, httpManager, data, api)
        }

        override fun isAvailable() = true

        override fun onNetworkErrorClose() {
        }

        override fun onRequestTimeOut(callBack: ApiCallback) {
            navigator.showTimeOutMessage(callBack)
        }

        override fun onLogout() {
        }
    }

    internal class Factory(private val mDataManager: DataManager) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ServiceViewModel(mDataManager, AppSchedulerProvider()) as T
        }
    }

}