//package taskmodule.ui.taskdetails
//
//import taskmodule.data.DataManager
//import taskmodule.ui.tasklisting.TaskPagerAdapter
//import taskmodule.utils.rx.SchedulerProvider
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