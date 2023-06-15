package com.rf.taskmodule.ui.taskdetails.newtimeline

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rf.taskmodule.data.DataManager
import com.rf.taskmodule.ui.base.BaseSdkViewModel
import com.rf.taskmodule.ui.taskdetails.TaskDetailsActivityViewModel
import com.rf.taskmodule.utils.rx.AppSchedulerProvider
import com.rf.taskmodule.utils.rx.SchedulerProvider

class TimelineViewModel(dataManager: DataManager?, schedulerProvider: SchedulerProvider?) : BaseSdkViewModel<TimeLineNavigator>(dataManager, schedulerProvider){
    internal class Factory(private val mDataManager: DataManager) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return TimelineViewModel(mDataManager,
                AppSchedulerProvider()
            ) as T
        }
    }
}