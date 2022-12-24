package com.rf.taskmodule.ui.taskdetails.subtask;//package com.rf.taskmodule.ui.taskdetails.subtask;
//
//import androidx.lifecycle.ViewModelProvider;
//
//import com.rf.taskmodule.ViewModelProviderFactory;
//import com.rf.taskmodule.data.DataManager;
//import com.rf.taskmodule.utils.rx.SchedulerProvider;
//
//import dagger.Module;
//import dagger.Provides;
//
///**
// * Created by Vikas Kesharvani on 17/09/20.
// * rocketflyer technology pvt. ltd
// * vikas.kesharvani@rocketflyer.in
// */
//@Module
//public class SubTaskFragmentModule {
//    @Provides
//    SubTaskViewModel subTaskViewModel(DataManager dataManager,
//                                                    SchedulerProvider schedulerProvider) {
//        return new SubTaskViewModel(dataManager, schedulerProvider);
//    }
//
//
//    @Provides
//    ViewModelProvider.Factory provideSubTaskViewModel(SubTaskViewModel subTaskViewModel) {
//        return new ViewModelProviderFactory<>(subTaskViewModel);
//    }
//
//}
