package com.tracki.ui.webview

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tracki.data.DataManager
import com.tracki.ui.base.BaseViewModel
import com.tracki.utils.rx.AppSchedulerProvider
import com.tracki.utils.rx.SchedulerProvider

/**
 * Created by rahul on 23/10/18
 */
class WebViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider) :
        BaseViewModel<WebViewNavigator>(dataManager, schedulerProvider) {

    private val mIsLoading = ObservableBoolean(false)

    fun getIsLoading(): ObservableBoolean {
        return mIsLoading
    }

    private fun setIsLoading(isLoading: Boolean) {
        mIsLoading.set(isLoading)
    }

    internal class Factory(private val mDataManager: DataManager) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return WebViewModel(mDataManager, AppSchedulerProvider()) as T
        }
    }
}