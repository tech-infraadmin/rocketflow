package taskmodule.ui.taskdetails


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import taskmodule.TrackiSdkApplication
import taskmodule.data.DataManager
import taskmodule.data.model.request.*
import taskmodule.data.model.response.config.Api
import taskmodule.data.network.APIError
import taskmodule.data.network.ApiCallback
import taskmodule.data.network.HttpManager
import taskmodule.ui.base.BaseSdkViewModel
//import taskmodule.ui.dynamicform.dynamicfragment.DynamicNavigator
import taskmodule.utils.ApiType
import taskmodule.utils.rx.AppSchedulerProvider
import taskmodule.utils.rx.SchedulerProvider

class NewTaskDetailsViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider) :
    BaseSdkViewModel<TDNavigator>(dataManager, schedulerProvider), ApiCallback {


    private val tag = "TaskDetailViewModel"

    lateinit var httpManager: HttpManager
    lateinit var request: AcceptRejectRequest
    lateinit var api: Api
    var geoId: String = ""
    var ctaID: String? = null
    var taskID: String? = null
    var date: String = ""

    var bookSlotRequest = BookSlotRequest()


    private inner class GetTaskData(var data: Any?) : ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            if (navigator != null)
                navigator.handleGetTaskDataResponse(this, result, error)
        }

        override fun hitApi() {
            if (TrackiSdkApplication.getApiMap().containsKey(ApiType.GET_TASK_DATA)) {
                val api = TrackiSdkApplication.getApiMap()[ApiType.GET_TASK_DATA]
                if (dataManager != null)
                    dataManager.taskData(this, httpManager, data, api)
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

    fun getSlotAvailability(
        httpManager: HttpManager,
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

    inner class SlotDataAPI : ApiCallback {

        var api = TrackiSdkApplication.getApiMap()[ApiType.GET_TIME_SLOTS]!!

        override fun onResponse(result: Any?, error: APIError?) {
            if (navigator != null) {
                navigator.getSlotDataResponse(this@SlotDataAPI, result, error)
            }
        }

        override fun hitApi() {
            val apiMain = TrackiSdkApplication.getApiMap()[ApiType.GET_TIME_SLOTS]!!
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

        override fun onRequestTimeOut(callBack: ApiCallback?) {
            if (navigator != null) {
                if (callBack != null) {
                    navigator.showTimeOutMessage(callBack)
                }
            }
        }

        override fun onLogout() {
        }

    }

    fun bookSlots(httpManager: HttpManager, bookSlotRequest: BookSlotRequest) {
        this.httpManager = httpManager
        this.bookSlotRequest = bookSlotRequest
        BookSlot().hitApi()
    }

    inner class BookSlot : ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            navigator.handleSlotResponse(this@BookSlot, result, error)
        }

        override fun hitApi() {
            if (TrackiSdkApplication.getApiMap().containsKey(ApiType.BOOK_TIME_SLOT)) {
                api = TrackiSdkApplication.getApiMap()[ApiType.BOOK_TIME_SLOT]!!

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

        override fun onRequestTimeOut(callBack: ApiCallback?) {
            navigator.showTimeOutMessage(callBack!!)
        }

        override fun onLogout() {

        }

    }

    fun getUserLocations(httpManager: HttpManager) {
        this.httpManager = httpManager
        GetUserLocations().hitApi()
    }

    inner class GetUserLocations : ApiCallback {
        private val api = TrackiSdkApplication.getApiMap()[ApiType.USER_LOCATIONS]

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
        override fun onRequestTimeOut(callBack: ApiCallback) {
            navigator.showTimeOutMessage(callBack)
        }

        override fun onLogout() {}
    }

    private inner class SendCtaOtp(var data: SendCtaOtpRequest?) : ApiCallback {
        override fun onResponse(result: Any?, error: APIError?) {
            navigator.handleSendCtaOtpResponse(this, result, error)
        }

        override fun hitApi() {
            if (TrackiSdkApplication.getApiMap().containsKey(ApiType.CTA_SEND_OTP)) {
                val apiC = TrackiSdkApplication.getApiMap()[ApiType.CTA_SEND_OTP]
                if (dataManager != null)
                    dataManager.sendCtaOtp(this, httpManager, data, apiC)
            }
        }

        override fun isAvailable(): Boolean {
            return true
        }

        override fun onNetworkErrorClose() {

        }

        override fun onRequestTimeOut(callBack: ApiCallback?) {
            if (navigator != null)
                navigator.showTimeOutMessage(callBack!!)
        }

        override fun onLogout() {

        }

    }

    private inner class VerifyCtaOtp(var data: VerifyCtaOtpRequest?) : ApiCallback {
        override fun onResponse(result: Any?, error: APIError?) {
            navigator.handleVerifyCtaOtpResponse(this, result, error)
        }

        override fun hitApi() {
            if (TrackiSdkApplication.getApiMap().containsKey(ApiType.CTA_VERIFY_OTP)) {
                val apiC = TrackiSdkApplication.getApiMap()[ApiType.CTA_VERIFY_OTP]
                if (dataManager != null)
                    dataManager.verifyCtaOtp(this, httpManager, data, apiC)
            }
        }

        override fun isAvailable(): Boolean {
            return true
        }

        override fun onNetworkErrorClose() {

        }

        override fun onRequestTimeOut(callBack: ApiCallback?) {
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

    fun sendCtaOtp(httpManager: HttpManager, request: SendCtaOtpRequest) {
        this.httpManager = httpManager
        SendCtaOtp(request).hitApi()
    }

    fun verifyCtaOtp(httpManager: HttpManager, request: VerifyCtaOtpRequest) {
        this.httpManager = httpManager
        VerifyCtaOtp(request).hitApi()
    }

    fun expandCollapse() {
        navigator.expandCollapse()
    }

    fun getPaymentUrl(httpManager: HttpManager, data: PaymentRequest?) {
        this.httpManager = httpManager
        GetPaymentUrl(data).hitApi()
    }

    private inner class GetPaymentUrl(var data: PaymentRequest?) : ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            if (navigator != null)
                navigator.handlePaymentUrlResponse(this, result, error)
        }

        override fun hitApi() {
            if (TrackiSdkApplication.getApiMap().containsKey(ApiType.INIT_TASK_PAYMENT)) {
                val api = TrackiSdkApplication.getApiMap()[ApiType.INIT_TASK_PAYMENT]
                if (dataManager != null)
                    dataManager.initTaskPayment(this, data, httpManager, api)
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

    fun getTaskData(httpManager: HttpManager, data: Any?) {
        this.httpManager = httpManager
        GetTaskData(data).hitApi()
    }


    fun getTaskById(httpManager: HttpManager, request: AcceptRejectRequest, api: Api?) {
        this.httpManager = httpManager
        this.request = request
        this.api = api!!
        hitApi()
    }

    override fun onResponse(result: Any?, error: APIError?) {
        navigator.handleResponse(this, result, error)
    }

    override fun hitApi() {
        api = TrackiSdkApplication.getApiMap()[ApiType.GET_TASK_BY_ID]!!
        dataManager.getTaskById(this, httpManager, request, api)
    }

    override fun isAvailable() = true

    override fun onNetworkErrorClose() {

    }

    override fun onRequestTimeOut(callBack: ApiCallback) {
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
    fun executeUpdates(httpManager: HttpManager?, request: ExecuteUpdateRequest?, api: Api?) {
        this.httpManager = httpManager!!

        this.api = TrackiSdkApplication.getApiMap()[ApiType.EXECUTE_UPDATE]!!
        ExecuteUpdateTask(request).hitApi()
    }

    inner class ExecuteUpdateTask(var request: ExecuteUpdateRequest?) : ApiCallback {
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
            return NewTaskDetailsViewModel(mDataManager, AppSchedulerProvider()) as T
        }
    }


}