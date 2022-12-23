package taskmodule.ui.main.taskdashboard;//package taskmodule.ui.main.taskdashboard;
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
// * Created by Vikas Kesharvani on 23/11/20.
// * rocketflyer technology pvt. ltd
// * vikas.kesharvani@rocketflyer.in
// */
//
//
//@Module
//public class TaskDashBoardFragmentModule {
//
//    @Provides
//    TaskDashBoardViewModel provideTaskDashBoardViewModel(DataManager dataManager,
//                                                         SchedulerProvider schedulerProvider) {
//        return new TaskDashBoardViewModel(dataManager, schedulerProvider);
//    }
//
//    @Provides
//    ViewModelProvider.Factory provideTaskDashBoardViewModelFactory(TaskDashBoardViewModel taskDashBoardViewModel) {
//        return new ViewModelProviderFactory<>(taskDashBoardViewModel);
//    }
//
//
//}