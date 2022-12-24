package com.rf.taskmodule.ui.newcreatetask

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rf.taskmodule.TrackiSdkApplication
import com.rf.taskmodule.data.DataManager
import com.rf.taskmodule.data.model.request.*
import com.rf.taskmodule.data.model.response.config.Api
import com.rf.taskmodule.data.network.APIError
import com.rf.taskmodule.data.network.ApiCallback
import com.rf.taskmodule.data.network.HttpManager
import com.rf.taskmodule.ui.base.BaseSdkViewModel
import com.rf.taskmodule.utils.*
import com.rf.taskmodule.utils.rx.AppSchedulerProvider
import com.rf.taskmodule.utils.rx.SchedulerProvider
import java.io.File
import java.util.*

open class NewCreateTaskViewModel(dataManager: com.rf.taskmodule.data.DataManager, schedulerProvider: com.rf.taskmodule.utils.rx.SchedulerProvider) :
        com.rf.taskmodule.ui.base.BaseSdkViewModel<NewCreateTaskNavigator>(dataManager, schedulerProvider) {

    private lateinit var httpManager: com.rf.taskmodule.data.network.HttpManager
    private lateinit var api: Api

    var geoId: String = ""
    var date: String = ""

    private lateinit var createTaskRequest: CreateTaskRequest

    fun onAssignNowClicked() {
        navigator.onAssignNowClicked()
    }

    fun selectDateTime(view: View) {
        navigator.selectDateTime(view)
    }

    fun isViewNullOrEmpty(string: String): Boolean {
        return com.rf.taskmodule.utils.CommonUtils.isViewNullOrEmpty(string)
    }

    fun isMobileValid(string: String): Boolean {
        return com.rf.taskmodule.utils.CommonUtils.isMobileValid(string)
    }

    fun selectLocation(view: View) {
        navigator.openPlaceAutoComplete(view)
    }

    fun createTaskApi(httpManager: com.rf.taskmodule.data.network.HttpManager, createTaskRequest: CreateTaskRequest, api: Api) {
        this.httpManager = httpManager
        this.api = api
        this.createTaskRequest = createTaskRequest
        CreateTask().hitApi()
    }

    fun onPointOfContactClick() {
        navigator.pointOfContactClicked()
    }


    /**
     *
     */
    inner class CreateTask : com.rf.taskmodule.data.network.ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            if(navigator!=null) {
                navigator.handleResponse(this@CreateTask, result, error)
            }
        }

        override fun hitApi() {
            if(dataManager!=null) {
                dataManager.createTask(this@CreateTask, httpManager, createTaskRequest, api)
            }
        }

        override fun isAvailable() = true

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

    inner class UpdateTask : com.rf.taskmodule.data.network.ApiCallback {
        override fun onResponse(result: Any?, error: APIError?) {
            if(navigator!=null) {
                navigator.handleUpdateResponse(this@UpdateTask, result, error)
            }
        }

        override fun hitApi() {
            if(dataManager!=null) {
                dataManager.updateTask(this@UpdateTask, httpManager, createTaskRequest, api)
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

    fun updateTaskApi(httpManager: com.rf.taskmodule.data.network.HttpManager, createTaskRequest: CreateTaskRequest, api: Api) {
        this.httpManager = httpManager
        this.api = api
        this.createTaskRequest = createTaskRequest
        UpdateTask().hitApi()
    }


    fun getSlotAvailability(httpManager: com.rf.taskmodule.data.network.HttpManager, api: Api, geoId: String, date: String) {
        this.httpManager = httpManager
        this.api = api
        this.geoId = geoId
        this.date = date
        SlotDataAPI().hitApi()
    }


    open fun checkFleet(httpManager: com.rf.taskmodule.data.network.HttpManager?) {
        this.httpManager = httpManager!!
        FleetListingAPI().hitApi()
    }

    open fun checkBuddy(httpManager: com.rf.taskmodule.data.network.HttpManager?) {
        this.httpManager = httpManager!!
        BuddyListingAPI().hitApi()
    }

    inner class SlotDataAPI: com.rf.taskmodule.data.network.ApiCallback {

        var api = com.rf.taskmodule.TrackiSdkApplication.getApiMap()[ApiType.GET_TIME_SLOTS]!!

        override fun onResponse(result: Any?, error: APIError?) {
            if(navigator!=null) {
                navigator.getSlotDataResponse(this@SlotDataAPI, result, error)
            }
        }

        override fun hitApi() {
            val apiMain = com.rf.taskmodule.TrackiSdkApplication.getApiMap()[ApiType.GET_TIME_SLOTS]!!
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

        override fun onRequestTimeOut(callBack: com.rf.taskmodule.data.network.ApiCallback?) {
            if(navigator!=null) {
                if (callBack != null) {
                    navigator.showTimeOutMessage(callBack)
                }
            }
        }

        override fun onLogout() {
        }

    }
    inner class BuddyListingAPI  : com.rf.taskmodule.data.network.ApiCallback {
        private val buddyRequest: BuddiesRequest
        private val api: Api?
        override fun onResponse(result: Any?, error: APIError?) {
            if(navigator!=null) {
                navigator.checkBuddyResponse(this@BuddyListingAPI, result, error)
            }
        }

        override fun hitApi() {
            if(dataManager!=null) {
                dataManager.buddyListing(this@BuddyListingAPI, httpManager, api, buddyRequest)
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

        override fun onLogout() {}

        init {
            val statusList: MutableList<BuddyStatus> = ArrayList()
            statusList.add(BuddyStatus.ON_TRIP)
            statusList.add(BuddyStatus.OFFLINE)
            statusList.add(BuddyStatus.IDLE)
            buddyRequest = BuddiesRequest(statusList, BuddyInfo.TRACKED_BY_ME, true)
            api = com.rf.taskmodule.TrackiSdkApplication.getApiMap()[ApiType.BUDDIES]
        }
    }

    fun getUserLocations(httpManager: com.rf.taskmodule.data.network.HttpManager){
        this.httpManager = httpManager
        GetUserLocations().hitApi()
    }

    inner class GetUserLocations : com.rf.taskmodule.data.network.ApiCallback {
        private val api = com.rf.taskmodule.TrackiSdkApplication.getApiMap()[ApiType.USER_LOCATIONS]
        override fun onResponse(result: Any?, error: APIError?) {
            navigator.handleMyPlaceResponse(this@GetUserLocations, result, error)
        }

        override fun hitApi() {
            if (api != null){
                dataManager.getUserLocation(
                    this@GetUserLocations,
                    httpManager,
                    api
                )
        }
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

    inner class FleetListingAPI : com.rf.taskmodule.data.network.ApiCallback {
        var fleetRequest = FleetRequest("ALL")
        var api = com.rf.taskmodule.TrackiSdkApplication.getApiMap()[ApiType.FLEETS]
        override fun onResponse(result: Any?, error: APIError?) {
            if(navigator!=null) {
                navigator.checkFleetResponse(this@FleetListingAPI, result, error)
            }
        }

        override fun hitApi() {
            if(dataManager!=null) {
                dataManager.fleetListing(this@FleetListingAPI, httpManager, fleetRequest, api)
            }
        }

        override fun isAvailable(): Boolean {
            return true
        }

        override fun onNetworkErrorClose() {}
        override fun onRequestTimeOut(callBack: com.rf.taskmodule.data.network.ApiCallback) {
            if(navigator!=null) {
                navigator.showTimeOutMessage(callBack)
            }
        }

        override fun onLogout() {}
    }

    /////
    fun uploadTaskData(dynamicFormMainData: DynamicFormMainData, httpManager: com.rf.taskmodule.data.network.HttpManager, api: Api?) {
        this.httpManager = httpManager
        this.api = api!!
        UploadFormList(dynamicFormMainData).hitApi()
    }


    inner class UploadFormList(val dynamicFormMainData: DynamicFormMainData) :
        com.rf.taskmodule.data.network.ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            if(navigator!=null) {
                navigator.handleResponse(this, result, error)
            }
        }

        override fun hitApi() {
            if(dataManager!=null) {
                dataManager.uploadFormList(this@UploadFormList, httpManager, dynamicFormMainData, api)
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

    fun uploadFileList(hashMap: HashMap<String, ArrayList<File>>, httpManager: com.rf.taskmodule.data.network.HttpManager, api: Api?, isApi:Boolean) {
        this.httpManager = httpManager
        this.api = api!!
        UploadFiles(hashMap,isApi).hitApi()
    }

    inner class UploadFiles(val hashMap: HashMap<String, ArrayList<File>>,var isApi: Boolean) :
        com.rf.taskmodule.data.network.ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            if(isApi) {
                if(navigator!=null) {
                    navigator.upLoadFileApiResponse(this, result, error)
                }
            } else{
                if(navigator!=null) {
                    navigator.upLoadFileDisposeApiResponse(this, result, error)
                }
            }
        }

        override fun hitApi() {
            dataManager.uploadFiles(this@UploadFiles, httpManager, hashMap, api)
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

    fun getRegionList(httpManager: com.rf.taskmodule.data.network.HttpManager, isStart: Boolean, regionRequest: RegionRequest) {
        this.httpManager = httpManager
        RegionList(isStart,regionRequest).hitApi()
    }
    /**
     *
     */
    inner class RegionList(var isStart: Boolean,var regionRequest: RegionRequest) :
        com.rf.taskmodule.data.network.ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            if(navigator!=null) {
                navigator.handleRegionListResponse(this@RegionList, result, error,isStart)
            }
        }

        override fun hitApi() {
            var api = com.rf.taskmodule.TrackiSdkApplication.getApiMap()[ApiType.GET_REGIONS]
            if(api!=null) {
                if(dataManager!=null) {
                    dataManager.getRegionList(this@RegionList, httpManager, regionRequest, api)
                }
            }else{
                com.rf.taskmodule.utils.Log.e("message","GET_REGIONS api is null")
            }
        }

        override fun isAvailable() = true

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
    fun getStateList(httpManager: com.rf.taskmodule.data.network.HttpManager, getManualLocationRequest: GetManualLocationRequest, isStart: Boolean) {
        this.httpManager = httpManager
        StateList(getManualLocationRequest,isStart).hitApi()
    }
    /**
     *
     */
    inner class StateList(var manualLocationRequest: GetManualLocationRequest,var isStart: Boolean) :
        com.rf.taskmodule.data.network.ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            if(navigator!=null) {
                navigator.handleStateListResponse(this@StateList, result, error,isStart)
            }
        }

        override fun hitApi() {
            var api = com.rf.taskmodule.TrackiSdkApplication.getApiMap()[ApiType.GET_STATES]
            if(api!=null){
                if(dataManager!=null) {
                    dataManager.getStateList(this@StateList, httpManager, manualLocationRequest, api)
                }
            }else{
                com.rf.taskmodule.utils.Log.e("message","GET_STATES api is null")
            }

        }

        override fun isAvailable() = true

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
    fun getCityList(httpManager: com.rf.taskmodule.data.network.HttpManager, getManualLocationRequest: GetManualLocationRequest, isStart: Boolean) {
        this.httpManager = httpManager
        CityList(getManualLocationRequest,isStart).hitApi()
    }
    /**
     *
     */
    inner class CityList(var manualLocationRequest: GetManualLocationRequest,var isStart: Boolean) :
        com.rf.taskmodule.data.network.ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            if(navigator!=null) {
                navigator.handleCityListResponse(this@CityList, result, error,isStart)
            }
        }

        override fun hitApi() {
            var api = com.rf.taskmodule.TrackiSdkApplication.getApiMap()[ApiType.GET_CITIES]
            if(api!=null) {
                if(dataManager!=null) {
                    dataManager.getCityList(this@CityList, httpManager, manualLocationRequest, api)
                }
            }else{
                com.rf.taskmodule.utils.Log.e("message","GET_CITIES api is null")
            }
        }

        override fun isAvailable() = true

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

    fun getHubList(httpManager: com.rf.taskmodule.data.network.HttpManager, getManualLocationRequest: GetManualLocationRequest, isStart: Boolean) {
        this.httpManager = httpManager
        HubList(getManualLocationRequest,isStart).hitApi()
    }
    inner class HubList(var manualLocationRequest: GetManualLocationRequest,var isStart: Boolean) :
        com.rf.taskmodule.data.network.ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            if(navigator!=null) {
                navigator.handleHubListResponse(this@HubList, result, error,isStart)
            }
        }

        override fun hitApi() {
            var api = com.rf.taskmodule.TrackiSdkApplication.getApiMap()[ApiType.GET_HUBS]
            if(api!=null){
                if(dataManager!=null) {
                    dataManager.getHubList(this@HubList, httpManager, manualLocationRequest, api)
                }
            }
            else{
                com.rf.taskmodule.utils.Log.e("message","GET_HUBS api is null")
            }
        }

        override fun isAvailable() = true

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


    internal class Factory(private val mDataManager: com.rf.taskmodule.data.DataManager) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return NewCreateTaskViewModel(mDataManager,
                com.rf.taskmodule.utils.rx.AppSchedulerProvider()
            ) as T
        }
    }
}