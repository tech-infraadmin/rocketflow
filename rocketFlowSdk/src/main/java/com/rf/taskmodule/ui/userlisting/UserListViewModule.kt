//package com.rf.taskmodule.ui.userlisting
//
//import com.rf.taskmodule.data.DataManager
//import com.rf.taskmodule.utils.rx.SchedulerProvider
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