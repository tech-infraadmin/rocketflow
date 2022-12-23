package taskmodule.ui.webview

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import taskmodule.data.DataManager
import taskmodule.ui.base.BaseSdkViewModel
import taskmodule.utils.rx.AppSchedulerProvider
import taskmodule.utils.rx.SchedulerProvider


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
            return PaymentViewModel(mDataManager, AppSchedulerProvider()) as T
        }
    }
}