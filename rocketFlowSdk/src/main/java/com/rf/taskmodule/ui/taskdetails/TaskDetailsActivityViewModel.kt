package com.rf.taskmodule.ui.taskdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rf.taskmodule.data.DataManager
import com.rf.taskmodule.ui.base.BaseSdkViewModel
import com.rf.taskmodule.utils.rx.AppSchedulerProvider
import com.rf.taskmodule.utils.rx.SchedulerProvider


/**
 * Created by Vikas Kesharvani on 16/09/20.
 * rocketflyer technology pvt. ltd
 * vikas.kesharvani@rocketflyer.in
 */
class TaskDetailsActivityViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider) :
        BaseSdkViewModel<NewTaskDetailNavigator>(dataManager, schedulerProvider) {


        internal class Factory(private val mDataManager: DataManager) : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return TaskDetailsActivityViewModel(mDataManager,
                            AppSchedulerProvider()
                        ) as T
                }
        }
}