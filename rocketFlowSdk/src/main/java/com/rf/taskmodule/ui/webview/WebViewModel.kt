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
class WebViewModel(dataManager: com.rf.taskmodule.data.DataManager, schedulerProvider: com.rf.taskmodule.utils.rx.SchedulerProvider) :
        com.rf.taskmodule.ui.base.BaseSdkViewModel<WebViewNavigator>(dataManager, schedulerProvider),
    com.rf.taskmodule.data.network.ApiCallback {

    private val mIsLoading = ObservableBoolean(false)
    private var httpManager: com.rf.taskmodule.data.network.HttpManager? = null
    private var configVersion: String? = ""

    fun getIsLoading(): ObservableBoolean {
        return mIsLoading
    }

    fun getConfig(
        httpManager: com.rf.taskmodule.data.network.HttpManager,
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

    override fun onRequestTimeOut(callBack: com.rf.taskmodule.data.network.ApiCallback?) {
        navigator.showTimeOutMessage(callBack!!)
    }

    override fun onLogout() {
    }


    private fun setIsLoading(isLoading: Boolean) {
        mIsLoading.set(isLoading)
    }

    internal class Factory(private val mDataManager: com.rf.taskmodule.data.DataManager) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return WebViewModel(mDataManager,
                com.rf.taskmodule.utils.rx.AppSchedulerProvider()
            ) as T
        }
    }
}