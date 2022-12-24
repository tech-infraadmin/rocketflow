//package com.rf.taskmodule.ui.main.filter
//
//import com.rf.taskmodule.data.DataManager
//import com.rf.taskmodule.utils.rx.SchedulerProvider
//import dagger.Module
//import dagger.Provides
//
//
///**
// * Created by Vikas Kesharvani on 04/11/20.
// * rocketflyer technology pvt. ltd
// * vikas.kesharvani@rocketflyer.in
// */
//
//@Module
//open class TaskFilterActivityModule {
//
//    @Provides
//    open fun provideTaskFilterViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider): TaskFilterViewModel {
//        return TaskFilterViewModel(dataManager, schedulerProvider)
//    }
//
//}