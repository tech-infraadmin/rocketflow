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
//class UserListNewViewModule {
//    @Provides
//    open fun provideUserListNewViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider): UserListNewViewModel {
//        return UserListNewViewModel(dataManager, schedulerProvider)
//    }
//
//
//    @Provides
//    fun provideUserListNewAdapter(): UserListNewAdapter {
//        return UserListNewAdapter(ArrayList())
//    }
//
//}