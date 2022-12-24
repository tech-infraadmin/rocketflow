package com.rf.taskmodule.ui.taskdetails


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
//import com.rf.taskmodule.ui.dynamicform.dynamicfragment.DynamicNavigator
import com.rf.taskmodule.utils.ApiType
import com.rf.taskmodule.utils.rx.AppSchedulerProvider
import com.rf.taskmodule.utils.rx.SchedulerProvider

class NewTaskDetailsViewModel(dataManager: com.rf.taskmodule.data.DataManager, schedulerProvider: com.rf.taskmodule.utils.rx.SchedulerProvider) :
    com.rf.taskmodule.ui.base.BaseSdkViewModel<TDNavigator>(dataManager, schedulerProvider),
    com.rf.taskmodule.data.network.ApiCallback {


    private val tag = "TaskDetailViewModel"

    lateinit var httpManager: com.rf.taskmodule.data.network.HttpManager
    lateinit var request: AcceptRejectRequest
    lateinit var api: Api
    var geoId: String = ""
    var ctaID: String? = null
    var taskID: String? = null
    var date: String = ""

    var bookSlotRequest = BookSlotRequest()


    private inner class GetTaskData(var data: Any?) :
        com.rf.taskmodule.data.network.ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            if (navigator != null)
                navigator.handleGetTaskDataResponse(this, result, error)
        }

        override fun hitApi() {
            if (com.rf.taskmodule.TrackiSdkApplication.getApiMap().containsKey(ApiType.GET_TASK_DATA)) {
                val api = com.rf.taskmodule.TrackiSdkApplication.getApiMap()[ApiType.GET_TASK_DATA]
                if (dataManager != null)
                    dataManager.taskData(this, httpManager, data, api)
            }
        }

        override fun isAvailable(): Boolean {
            return true
        }

        override fun onNetworkErrorClose() {
        }

        override fun onRequestTimeOut(callBack: com.rf.taskmodule.data.network.ApiCallback) {
            if (navigator != null)
                navigator.showTimeOutMessage(callBack)
        }

        override fun onLogout() {
        }
    }

    fun getSlotAvailability(
        httpManager: com.rf.taskmodule.data.network.HttpManager,
        api: Api,
        geoId: String,
        date: String,
        ctaID: String? = null,
        taskID: String? = null
    ) {
        this.httpManager = httpManager
        this.api = api
        this.geoId = geoId
        this.date = date
        if (ctaID != null) {
            this.ctaID = ctaID
        }
        if (taskID != null) {
            this.taskID = taskID
        }
        SlotDataAPI().hitApi()
    }

    inner class SlotDataAPI : com.rf.taskmodule.data.network.ApiCallback {

        var api = com.rf.taskmodule.TrackiSdkApplication.getApiMap()[ApiType.GET_TIME_SLOTS]!!

        override fun onResponse(result: Any?, error: APIError?) {
            if (navigator != null) {
                navigator.getSlotDataResponse(this@SlotDataAPI, result, error)
            }
        }

        override fun hitApi() {
            val apiMain = com.rf.taskmodule.TrackiSdkApplication.getApiMap()[ApiType.GET_TIME_SLOTS]!!
            val api = Api()
            api.name = ApiType.GET_TIME_SLOTS
            api.timeOut = apiMain.timeOut

            api.url = "${apiMain.url}?geoId=$geoId&date=$date"

            if (ctaID != null && taskID != null) {
                api.url = "${apiMain.url}?geoId=$geoId&date=$date&ctaId=$ctaID&taskId=$taskID"
            }

            if (dataManager != null) {
                dataManager.timeSlotData(this@SlotDataAPI, httpManager, api)
            }
        }

        override fun isAvailable(): Boolean {
            return true
        }

        override fun onNetworkErrorClose() {
        }

        override fun onRequestTimeOut(callBack: com.rf.taskmodule.data.network.ApiCallback?) {
            if (navigator != null) {
                if (callBack != null) {
                    navigator.showTimeOutMessage(callBack)
                }
            }
        }

        override fun onLogout() {
        }

    }

    fun bookSlots(httpManager: com.rf.taskmodule.data.network.HttpManager, bookSlotRequest: BookSlotRequest) {
        this.httpManager = httpManager
        this.bookSlotRequest = bookSlotRequest
        BookSlot().hitApi()
    }

    inner class BookSlot : com.rf.taskmodule.data.network.ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            navigator.handleSlotResponse(this@BookSlot, result, error)
        }

        override fun hitApi() {
            if (com.rf.taskmodule.TrackiSdkApplication.getApiMap().containsKey(ApiType.BOOK_TIME_SLOT)) {
                api = com.rf.taskmodule.TrackiSdkApplication.getApiMap()[ApiType.BOOK_TIME_SLOT]!!

                if (api != null) {
                    dataManager.bookSlot(
                        this@BookSlot,
                        httpManager,
                        api,
                        bookSlotRequest
                    )
                }
            }
            else{

            }

        }

        override fun isAvailable(): Boolean {
            return true
        }

        override fun onNetworkErrorClose() {

        }

        override fun onRequestTimeOut(callBack: com.rf.taskmodule.data.network.ApiCallback?) {
            navigator.showTimeOutMessage(callBack!!)
        }

        override fun onLogout() {

        }

    }

    fun getUserLocations(httpManager: com.rf.taskmodule.data.network.HttpManager) {
        this.httpManager = httpManager
        GetUserLocations().hitApi()
    }

    inner class GetUserLocations : com.rf.taskmodule.data.network.ApiCallback {
        private val api = com.rf.taskmodule.TrackiSdkApplication.getApiMap()[ApiType.USER_LOCATIONS]

        override fun onResponse(result: Any?, error: APIError?) {
            navigator.handleMyPlaceResponse(this@GetUserLocations, result, error)
        }

        override fun hitApi() {
            if (api != null) {
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

    private inner class SendCtaOtp(var data: SendCtaOtpRequest?) :
        com.rf.taskmodule.data.network.ApiCallback {
        override fun onResponse(result: Any?, error: APIError?) {
            navigator.handleSendCtaOtpResponse(this, result, error)
        }

        override fun hitApi() {
            if (com.rf.taskmodule.TrackiSdkApplication.getApiMap().containsKey(ApiType.CTA_SEND_OTP)) {
                val apiC = com.rf.taskmodule.TrackiSdkApplication.getApiMap()[ApiType.CTA_SEND_OTP]
                if (dataManager != null)
                    dataManager.sendCtaOtp(this, httpManager, data, apiC)
            }
        }

        override fun isAvailable(): Boolean {
            return true
        }

        override fun onNetworkErrorClose() {

        }

        override fun onRequestTimeOut(callBack: com.rf.taskmodule.data.network.ApiCallback?) {
            if (navigator != null)
                navigator.showTimeOutMessage(callBack!!)
        }

        override fun onLogout() {

        }

    }

    private inner class VerifyCtaOtp(var data: VerifyCtaOtpRequest?) :
        com.rf.taskmodule.data.network.ApiCallback {
        override fun onResponse(result: Any?, error: APIError?) {
            navigator.handleVerifyCtaOtpResponse(this, result, error)
        }

        override fun hitApi() {
            if (com.rf.taskmodule.TrackiSdkApplication.getApiMap().containsKey(ApiType.CTA_VERIFY_OTP)) {
                val apiC = com.rf.taskmodule.TrackiSdkApplication.getApiMap()[ApiType.CTA_VERIFY_OTP]
                if (dataManager != null)
                    dataManager.verifyCtaOtp(this, httpManager, data, apiC)
            }
        }

        override fun isAvailable(): Boolean {
            return true
        }

        override fun onNetworkErrorClose() {

        }

        override fun onRequestTimeOut(callBack: com.rf.taskmodule.data.network.ApiCallback?) {
            if (navigator != null)
                navigator.showTimeOutMessage(callBack!!)
        }

        override fun onLogout() {

        }

    }

    /* fun getTaskData( httpManager: HttpManager, data:Any?) {
         this.httpManager = httpManager
         GetTaskData(data).hitApi()
     }


     fun getPreviousFormData( httpManager: HttpManager, data:Any?) {
         this.httpManager = httpManager
         GetPreviousFormData(data).hitApi()
     }

     private inner class GetPreviousFormData(var data:Any?) : ApiCallback {

         override fun onResponse(result: Any?, error: APIError?) {
             if(navigator!=null)
                 navigator.handleGetPreviousFormResponse(this, result, error)
         }

         override fun hitApi() {
             if(TrackiApplication.getApiMap().containsKey(ApiType.GET_TASK_DATA)) {
                 val api = TrackiApplication.getApiMap()[ApiType.GET_TASK_DATA]
                 if(dataManager!=null)
                     dataManager.taskData(this, httpManager, data, api)
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

     private inner class GetTaskData(var data:Any?) : ApiCallback {

         override fun onResponse(result: Any?, error: APIError?) {
             if(navigator!=null)
                 navigator.handleGetTaskDataResponse(this, result, error)
         }

         override fun hitApi() {
             if(TrackiApplication.getApiMap().containsKey(ApiType.GET_TASK_DATA)) {
                 val api = TrackiApplication.getApiMap()[ApiType.GET_TASK_DATA]
                 if(dataManager!=null)
                     dataManager.taskData(this, httpManager, data, api)
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
     }*/

    fun sendCtaOtp(httpManager: com.rf.taskmodule.data.network.HttpManager, request: SendCtaOtpRequest) {
        this.httpManager = httpManager
        SendCtaOtp(request).hitApi()
    }

    fun verifyCtaOtp(httpManager: com.rf.taskmodule.data.network.HttpManager, request: VerifyCtaOtpRequest) {
        this.httpManager = httpManager
        VerifyCtaOtp(request).hitApi()
    }

    fun expandCollapse() {
        navigator.expandCollapse()
    }

    fun getPaymentUrl(httpManager: com.rf.taskmodule.data.network.HttpManager, data: PaymentRequest?) {
        this.httpManager = httpManager
        GetPaymentUrl(data).hitApi()
    }

    private inner class GetPaymentUrl(var data: PaymentRequest?) :
        com.rf.taskmodule.data.network.ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            if (navigator != null)
                navigator.handlePaymentUrlResponse(this, result, error)
        }

        override fun hitApi() {
            if (com.rf.taskmodule.TrackiSdkApplication.getApiMap().containsKey(ApiType.INIT_TASK_PAYMENT)) {
                val api = com.rf.taskmodule.TrackiSdkApplication.getApiMap()[ApiType.INIT_TASK_PAYMENT]
                if (dataManager != null)
                    dataManager.initTaskPayment(this, data, httpManager, api)
            }
        }

        override fun isAvailable(): Boolean {
            return true
        }

        override fun onNetworkErrorClose() {
        }

        override fun onRequestTimeOut(callBack: com.rf.taskmodule.data.network.ApiCallback) {
            if (navigator != null)
                navigator.showTimeOutMessage(callBack)
        }

        override fun onLogout() {
        }
    }

    fun getTaskData(httpManager: com.rf.taskmodule.data.network.HttpManager, data: Any?) {
        this.httpManager = httpManager
        GetTaskData(data).hitApi()
    }


    fun getTaskById(httpManager: com.rf.taskmodule.data.network.HttpManager, request: AcceptRejectRequest, api: Api?) {
        this.httpManager = httpManager
        this.request = request
        this.api = api!!
        hitApi()
    }

    override fun onResponse(result: Any?, error: APIError?) {
        navigator.handleResponse(this, result, error)
    }

    override fun hitApi() {
        api = com.rf.taskmodule.TrackiSdkApplication.getApiMap()[ApiType.GET_TASK_BY_ID]!!
        dataManager.getTaskById(this, httpManager, request, api)
    }

    override fun isAvailable() = true

    override fun onNetworkErrorClose() {

    }

    override fun onRequestTimeOut(callBack: com.rf.taskmodule.data.network.ApiCallback) {
        navigator.showTimeOutMessage(callBack)
    }

    override fun onLogout() {

    }


    //    void acceptTask(HttpManager httpManager, String taskId, Api api) {
    //        this.httpManager = httpManager;
    //        this.apiUrl = api;
    //        new AcceptTask(new AcceptRejectRequest(taskId)).hitApi();
    //    }
    //    void startTask(HttpManager httpManager, StartTaskRequest startTaskRequest, Api api) {
    //        this.httpManager = httpManager;
    //        this.apiUrl = api;
    //        new StartTask(startTaskRequest).hitApi();
    //    }
    //    void cancelTask(HttpManager httpManager, String taskId, Api api) {
    //        this.httpManager = httpManager;
    //        this.apiUrl = api;
    //        new CancelTask(new CancelRequest(taskId, false)).hitApi();
    //    }
    //    void endTask(HttpManager httpManager, EndTaskRequest endTaskRequest, Api api) {
    //        this.httpManager = httpManager;
    //        this.apiUrl = api;
    //        new EndTask(endTaskRequest).hitApi();
    //    }
    //    void arriveReachTask(HttpManager httpManager, ArriveReachRequest request, Api api) {
    //        this.httpManager = httpManager;
    //        this.apiUrl = api;
    //        new ArriveReachTask(request).hitApi();
    //
    //    }
    fun executeUpdates(httpManager: com.rf.taskmodule.data.network.HttpManager?, request: ExecuteUpdateRequest?, api: Api?) {
        this.httpManager = httpManager!!

        this.api = com.rf.taskmodule.TrackiSdkApplication.getApiMap()[ApiType.EXECUTE_UPDATE]!!
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
            return NewTaskDetailsViewModel(mDataManager,
                com.rf.taskmodule.utils.rx.AppSchedulerProvider()
            ) as T
        }
    }


}