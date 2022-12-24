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
class TaskDetailsActivityViewModel(dataManager: com.rf.taskmodule.data.DataManager, schedulerProvider: com.rf.taskmodule.utils.rx.SchedulerProvider) :
        com.rf.taskmodule.ui.base.BaseSdkViewModel<NewTaskDetailNavigator>(dataManager, schedulerProvider) {


        internal class Factory(private val mDataManager: com.rf.taskmodule.data.DataManager) : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return TaskDetailsActivityViewModel(mDataManager,
                            com.rf.taskmodule.utils.rx.AppSchedulerProvider()
                        ) as T
                }
        }
}