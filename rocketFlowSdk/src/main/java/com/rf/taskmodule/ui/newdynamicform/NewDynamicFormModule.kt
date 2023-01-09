//package com.rf.taskmodule.ui.newdynamicform
//
//import androidx.lifecycle.ViewModelProvider
//import androidx.recyclerview.widget.LinearLayoutManager
//import com.rf.taskmodule.ui.ViewModelProviderFactory
//import com.rf.taskmodule.data.DataManager
//import com.rf.taskmodule.ui.dynamicform.dynamicfragment.DynamicAdapter
//import com.rf.taskmodule.utils.rx.SchedulerProvider
//import dagger.Module
//import dagger.Provides
//import java.util.*
//
//@Module
//class NewDynamicFormModule {
//    @Provides
//    fun dynamicViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider): NewDynamicViewModel {
//        return NewDynamicViewModel(dataManager, schedulerProvider)
//    }
//
//    @Provides
//    internal fun provideDynamicViewModel(dynamicViewModel: NewDynamicViewModel): ViewModelProvider.Factory {
//        return ViewModelProviderFactory(dynamicViewModel)
//    }
//
//    @Provides
//    internal fun provideLinearLayoutManager(fragment: NewDynamicFormFragment): LinearLayoutManager {
//        return LinearLayoutManager(fragment.activity)
//    }
//
//    @Provides
//    open fun provideNotificationAdapter(): DynamicAdapter {
//        return DynamicAdapter(ArrayList())
//    }
//}