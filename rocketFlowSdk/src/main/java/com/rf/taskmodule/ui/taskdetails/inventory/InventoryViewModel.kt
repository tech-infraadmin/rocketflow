package com.rf.taskmodule.ui.taskdetails.inventory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rf.taskmodule.data.DataManager
import com.rf.taskmodule.ui.base.BaseSdkViewModel
import com.rf.taskmodule.ui.taskdetails.tripdetails.TripDetailsViewModel
import com.rf.taskmodule.utils.rx.AppSchedulerProvider
import com.rf.taskmodule.utils.rx.SchedulerProvider

class InventoryViewModel(dataManager: DataManager?, schedulerProvider: SchedulerProvider?) : BaseSdkViewModel<InventoryNavigator>(
    dataManager, schedulerProvider
) {
    internal class Factory(private val mDataManager: DataManager) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return InventoryViewModel(mDataManager,
                AppSchedulerProvider()
            ) as T
        }
    }
}