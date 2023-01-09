package com.rf.taskmodule.ui.taskdetails.timeline;//package com.rf.taskmodule.ui.taskdetails.timeline;
//
//import androidx.lifecycle.ViewModelProvider;
//import androidx.recyclerview.widget.LinearLayoutManager;
//
//import com.rf.taskmodule.ui.ViewModelProviderFactory;
//import com.rf.taskmodule.data.DataManager;
//import com.rf.taskmodule.ui.dynamicform.dynamicfragment.DynamicAdapter;
//import com.rf.taskmodule.ui.dynamicform.dynamicfragment.DynamicFragment;
//import com.rf.taskmodule.ui.dynamicform.dynamicfragment.DynamicViewModel;
//import com.rf.taskmodule.ui.taskdetails.NewTaskDetailsViewModel;
//import com.rf.taskmodule.utils.rx.SchedulerProvider;
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
