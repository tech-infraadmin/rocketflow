//package com.rf.taskmodule.ui.taskdetails
//
//import com.rf.taskmodule.data.DataManager
//import com.rf.taskmodule.ui.tasklisting.TaskPagerAdapter
//import com.rf.taskmodule.utils.rx.SchedulerProvider
//import dagger.Module
//import dagger.Provides
//
//
//@Module
//class NewTaskDetailActivityModule {
//
//    @Provides
//    fun provideNewTaskDetailsViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider) =
//            TaskDetailsActivityViewModel(dataManager, schedulerProvider)
//
//    @Provides
//    fun provideTaskPagerAdapter(activity: NewTaskDetailsActivity): TaskPagerAdapter {
//        return TaskPagerAdapter(activity.supportFragmentManager)
//    }
//}