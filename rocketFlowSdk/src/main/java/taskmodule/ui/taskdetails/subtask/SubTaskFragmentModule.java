package taskmodule.ui.taskdetails.subtask;//package taskmodule.ui.taskdetails.subtask;
//
//import androidx.lifecycle.ViewModelProvider;
//
//import taskmodule.ViewModelProviderFactory;
//import taskmodule.data.DataManager;
//import taskmodule.utils.rx.SchedulerProvider;
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
