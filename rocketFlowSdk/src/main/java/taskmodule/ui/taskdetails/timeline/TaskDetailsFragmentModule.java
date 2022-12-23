package taskmodule.ui.taskdetails.timeline;//package taskmodule.ui.taskdetails.timeline;
//
//import androidx.lifecycle.ViewModelProvider;
//import androidx.recyclerview.widget.LinearLayoutManager;
//
//import taskmodule.ViewModelProviderFactory;
//import taskmodule.data.DataManager;
//import taskmodule.ui.dynamicform.dynamicfragment.DynamicAdapter;
//import taskmodule.ui.dynamicform.dynamicfragment.DynamicFragment;
//import taskmodule.ui.dynamicform.dynamicfragment.DynamicViewModel;
//import taskmodule.ui.taskdetails.NewTaskDetailsViewModel;
//import taskmodule.utils.rx.SchedulerProvider;
//
//import dagger.Module;
//import dagger.Provides;
//
//
//@Module
//public class TaskDetailsFragmentModule {
//
//  /*  @Provides
//    DynamicViewModel newDynamicViewModel(DataManager dataManager, SchedulerProvider schedulerProvider){
//        return new DynamicViewModel(dataManager,schedulerProvider);
//    }
//
//    @Provides
//    ViewModelProvider.Factory provideDynamicViewModel(DynamicViewModel dynamicViewModel) {
//        return new ViewModelProviderFactory<>(dynamicViewModel);
//    }
//*/
//
//    @Provides
//    NewTaskDetailsViewModel newTaskDetailsViewModel(DataManager dataManager,
//                                                    SchedulerProvider schedulerProvider) {
//        return new NewTaskDetailsViewModel(dataManager, schedulerProvider);
//    }
//
//
//
//    @Provides
//    ViewModelProvider.Factory provideTaskDetailsViewModel(NewTaskDetailsViewModel newTaskDetailsViewModel) {
//        return new ViewModelProviderFactory<>(newTaskDetailsViewModel);
//    }
//
//    @Provides
//    LinearLayoutManager provideLinearLayoutManager(TaskDetailsFragment fragment) {
//        return new LinearLayoutManager(fragment.getActivity());
//    }
//
//}
