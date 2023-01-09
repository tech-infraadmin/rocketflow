package com.rf.taskmodule.ui.main.filter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rf.taskmodule.data.DataManager
import com.rf.taskmodule.ui.base.BaseSdkViewModel
import com.rf.taskmodule.utils.rx.AppSchedulerProvider
import com.rf.taskmodule.utils.rx.SchedulerProvider

/**
 * Created by rahul on 11/10/18
 */
open class BuddyFilterViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider) :
        BaseSdkViewModel<BuddyFilterNavigator>(dataManager, schedulerProvider) {
    fun onProceedClick() {
        navigator.applyFilter()
    }



    internal class Factory(private val mDataManager: DataManager) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return BuddyFilterViewModel(mDataManager,
                AppSchedulerProvider()
            ) as T
        }
    }


}