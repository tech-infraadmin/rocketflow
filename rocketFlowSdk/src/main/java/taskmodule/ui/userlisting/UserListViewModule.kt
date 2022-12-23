//package taskmodule.ui.userlisting
//
//import taskmodule.data.DataManager
//import taskmodule.utils.rx.SchedulerProvider
//import dagger.Module
//import dagger.Provides
//import java.util.ArrayList
//
//
//@Module
//class UserListViewModule {
//    @Provides
//    open fun provideUserListViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider): UserListViewModel {
//        return UserListViewModel(dataManager, schedulerProvider)
//    }
//
//
//    @Provides
//    fun provideUserListAdapter(): UserListAdapter {
//        return UserListAdapter(ArrayList())
//    }
//
//}