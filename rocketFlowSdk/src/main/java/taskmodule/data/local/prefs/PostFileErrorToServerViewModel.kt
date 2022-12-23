package taskmodule.data.local.prefs


import taskmodule.data.DataManager
import taskmodule.data.model.response.config.ApiErrorRequest

import taskmodule.data.network.APIError
import taskmodule.data.network.ApiCallback
import taskmodule.data.network.HttpManager
import taskmodule.ui.base.BaseSdkViewModel
import taskmodule.utils.rx.SchedulerProvider

class PostFileErrorToServerViewModel  (dataManager: DataManager, schedulerProvider: SchedulerProvider) :
    BaseSdkViewModel<PostErrorToServerNavigator>(dataManager, schedulerProvider) {

    private lateinit var httpManager: HttpManager


    fun postFileErrorToServer(httpManager: HttpManager,request:ApiErrorRequest) {
        this.httpManager = httpManager
        PostErrorServer(request).hitApi()
    }

    inner class PostErrorServer(var request: ApiErrorRequest?) : ApiCallback {

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

        override fun onRequestTimeOut(callBack: ApiCallback) {
            if(navigator!=null)
                navigator.showTimeOutMessage(callBack)
        }

        override fun onLogout() {
        }
    }



}