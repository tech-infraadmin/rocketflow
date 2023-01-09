package com.rf.taskmodule.ui.addplace

import android.view.View
import com.rf.taskmodule.data.DataManager
import com.rf.taskmodule.data.model.response.config.Api
import com.rf.taskmodule.data.network.APIError
import com.rf.taskmodule.data.network.ApiCallback
import com.rf.taskmodule.data.network.HttpManager
import com.rf.taskmodule.ui.base.BaseSdkViewModel
import com.rf.taskmodule.utils.CommonUtils
import com.rf.taskmodule.utils.rx.SchedulerProvider

open class AddPlaceViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider) :
        BaseSdkViewModel<AddPlaceNavigator>(dataManager, schedulerProvider) {

    private lateinit var httpManager: HttpManager
    private lateinit var api: Api
     var addPlaceRequest: AddPlaceRequest?=null


    fun isViewNullOrEmpty(string: String): Boolean {
        return CommonUtils.isViewNullOrEmpty(string)
    }

    fun isMobileValid(string: String): Boolean {
        return CommonUtils.isMobileValid(string)
    }
    fun selectLocation(view: View) {
        navigator.openPlaceAutoComplete(view)
    }


    fun getLocation(httpManager: HttpManager, api: Api) {
        this.httpManager = httpManager
        this.api = api
        GetLocation().hitApi()
    }
    fun updateLocation(httpManager: HttpManager, addPlaceRequest:AddPlaceRequest, api: Api) {
        this.httpManager = httpManager
        this.api = api
        this.addPlaceRequest = addPlaceRequest
        UpdateLocation().hitApi()
    }

    fun buttonClickAddLocation(view: View){
        navigator.addLocation(view)
    }


    /**
     *
     */
    inner class GetLocation : ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            navigator.handleStateCityResponse(this@GetLocation, result, error)
        }

        override fun hitApi() {
            dataManager.getUserLocation(this@GetLocation, httpManager,  api)
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

    inner class UpdateLocation : ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            navigator.handleAddPlaceResponse(this@UpdateLocation, result, error)
        }

        override fun hitApi() {
            dataManager.updateUserLocation(this@UpdateLocation,addPlaceRequest, httpManager,  api)
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




}