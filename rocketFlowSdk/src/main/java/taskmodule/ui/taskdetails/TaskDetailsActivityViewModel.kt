package taskmodule.ui.taskdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import taskmodule.data.DataManager
import taskmodule.ui.base.BaseSdkViewModel
import taskmodule.utils.rx.AppSchedulerProvider
import taskmodule.utils.rx.SchedulerProvider


/**
 * Created by Vikas Kesharvani on 16/09/20.
 * rocketflyer technology pvt. ltd
 * vikas.kesharvani@rocketflyer.in
 */
class TaskDetailsActivityViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider) :
        BaseSdkViewModel<NewTaskDetailNavigator>(dataManager, schedulerProvider) {


        internal class Factory(private val mDataManager: DataManager) : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return TaskDetailsActivityViewModel(mDataManager, AppSchedulerProvider()) as T
                }
        }
}