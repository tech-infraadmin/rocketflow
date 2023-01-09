package com.rf.taskmodule.ui.webview

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rf.taskmodule.data.DataManager
import com.rf.taskmodule.ui.base.BaseSdkViewModel
import com.rf.taskmodule.utils.rx.AppSchedulerProvider
import com.rf.taskmodule.utils.rx.SchedulerProvider


class PaymentViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider) :
    BaseSdkViewModel<PaymentNavigator>(dataManager, schedulerProvider) {

    private val mIsLoading = ObservableBoolean(false)

    fun getIsLoading(): ObservableBoolean {
        return mIsLoading
    }

    private fun setIsLoading(isLoading: Boolean) {
        mIsLoading.set(isLoading)
    }


    internal class Factory(private val mDataManager: DataManager) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PaymentViewModel(mDataManager,
                AppSchedulerProvider()
            ) as T
        }
    }
}