package com.rf.taskmodule.ui.availabilitycalender.slots

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rf.taskmodule.data.DataManager
import com.rf.taskmodule.ui.base.BaseSdkViewModel
import com.rf.taskmodule.utils.rx.AppSchedulerProvider
import com.rf.taskmodule.utils.rx.SchedulerProvider

class SlotsViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider) : BaseSdkViewModel<SlotsNavigator>(dataManager, schedulerProvider) {

    internal class Factory(private val mDataManager: DataManager) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SlotsViewModel(mDataManager,
                AppSchedulerProvider()
            ) as T
        }
    }
}