//package com.rf.taskmodule.ui.addplace
//
//import com.rf.taskmodule.data.DataManager
//import com.rf.taskmodule.utils.rx.SchedulerProvider
//import dagger.Module
//import dagger.Provides
//
//
//@Module
//open class AddPlaceActivityModule {
//
//    @Provides
//    open fun provideAddPlaceViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider): AddPlaceViewModel {
//        return AddPlaceViewModel(dataManager, schedulerProvider)
//    }
//}