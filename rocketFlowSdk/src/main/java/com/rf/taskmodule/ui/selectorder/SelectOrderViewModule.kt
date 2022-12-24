//package com.rf.taskmodule.ui.selectorder
//
//import com.rf.taskmodule.data.DataManager
//import com.rf.taskmodule.utils.rx.SchedulerProvider
//import dagger.Module
//import dagger.Provides
//
//
//@Module
//class SelectOrderViewModule {
//    @Provides
//    open fun provideSelectOrderViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider): SelectOrderViewModel {
//        return SelectOrderViewModel(dataManager, schedulerProvider)
//    }
//
//}