package com.tracki.ui.main.filter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tracki.data.DataManager
import com.tracki.ui.base.BaseViewModel
import com.tracki.utils.rx.AppSchedulerProvider
import com.tracki.utils.rx.SchedulerProvider

/**
 * Created by rahul on 11/10/18
 */
open class BuddyFilterViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider) :
        BaseViewModel<BuddyFilterNavigator>(dataManager, schedulerProvider) {
    fun onProceedClick() {
        navigator.applyFilter()
    }



    internal class Factory(private val mDataManager: DataManager) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return BuddyFilterViewModel(mDataManager, AppSchedulerProvider()) as T
        }
    }


}