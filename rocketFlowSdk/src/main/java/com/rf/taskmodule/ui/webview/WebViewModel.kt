package com.rf.taskmodule.ui.webview

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rf.taskmodule.data.DataManager
import com.rf.taskmodule.data.network.APIError
import com.rf.taskmodule.data.network.ApiCallback
import com.rf.taskmodule.data.network.HttpManager
import com.rf.taskmodule.ui.base.BaseSdkViewModel
import com.rf.taskmodule.utils.rx.AppSchedulerProvider
import com.rf.taskmodule.utils.rx.SchedulerProvider

/**
 * Created by rahul on 23/10/18
 */
class WebViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider) :
        BaseSdkViewModel<WebViewNavigator>(dataManager, schedulerProvider),
    ApiCallback {

    private val mIsLoading = ObservableBoolean(false)
    private var httpManager: HttpManager? = null
    private var configVersion: String? = ""

    fun getIsLoading(): ObservableBoolean {
        return mIsLoading
    }

    fun getConfig(
        httpManager: HttpManager,
        configVersion: String
    ) {
        this.configVersion = configVersion
        this.httpManager = httpManager
        hitApi()
    }

    override fun onResponse(result: Any?, error: APIError?) {
        navigator.handleResponse(this,result, error)
    }

    override fun hitApi() {
        dataManager.getConfig(httpManager, this@WebViewModel, configVersion)
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


    private fun setIsLoading(isLoading: Boolean) {
        mIsLoading.set(isLoading)
    }

    internal class Factory(private val mDataManager: DataManager) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return WebViewModel(mDataManager,
                AppSchedulerProvider()
            ) as T
        }
    }
}