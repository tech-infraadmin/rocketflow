package taskmodule.ui.newcreatetask

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import taskmodule.data.DataManager
import taskmodule.data.model.request.*
import taskmodule.data.model.response.config.Api
import taskmodule.data.network.APIError
import taskmodule.data.network.ApiCallback
import taskmodule.data.network.HttpManager
import taskmodule.ui.base.BaseSdkViewModel
import taskmodule.utils.rx.AppSchedulerProvider
import taskmodule.utils.rx.SchedulerProvider

open class GetUserSuggestionListViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider) :
        BaseSdkViewModel<SuggestionListNavigator>(dataManager, schedulerProvider) {

    private lateinit var httpManager: HttpManager
    private lateinit var api: Api


    fun clientSearchApi(httpManager: HttpManager, clientSearchRequest: ClientSearchRequest, api: Api) {
        this.httpManager = httpManager
        this.api = api
        ClientSearch(clientSearchRequest).hitApi()
    }


    /**
     *
     */
    inner class ClientSearch(var clientSearchRequest: ClientSearchRequest) : ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            navigator.returnList(this@ClientSearch, result, error)

        }

        override fun hitApi() {
            dataManager.getUserListForSuggestion(this@ClientSearch, httpManager, clientSearchRequest, api)
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
            return GetUserSuggestionListViewModel(mDataManager, AppSchedulerProvider()) as T
        }
    }

}