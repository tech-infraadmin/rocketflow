package com.tracki.ui.taskdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tracki.data.DataManager
import com.tracki.ui.base.BaseViewModel
import com.tracki.utils.rx.AppSchedulerProvider
import com.tracki.utils.rx.SchedulerProvider


/**
 * Created by Vikas Kesharvani on 16/09/20.
 * rocketflyer technology pvt. ltd
 * vikas.kesharvani@rocketflyer.in
 */
class TaskDetailsActivityViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider) :
        BaseViewModel<NewTaskDetailNavigator>(dataManager, schedulerProvider) {


        internal class Factory(private val mDataManager: DataManager) : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return TaskDetailsActivityViewModel(mDataManager, AppSchedulerProvider()) as T
                }
        }
}