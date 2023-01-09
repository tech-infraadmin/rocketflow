package com.rf.taskmodule.ui.tasklisting.assignedtome;//package com.rf.taskmodule.ui.tasklisting.assignedtome;
//
//import androidx.lifecycle.ViewModelProvider;
//import androidx.recyclerview.widget.LinearLayoutManager;
//
//import com.rf.taskmodule.ui.ViewModelProviderFactory;
//import com.rf.taskmodule.data.DataManager;
//import com.rf.taskmodule.data.local.prefs.PreferencesHelper;
//import com.rf.taskmodule.ui.tasklisting.TaskListingAdapter;
//import com.rf.taskmodule.utils.rx.SchedulerProvider;
//
//import java.util.ArrayList;
//
//import dagger.Module;
//import dagger.Provides;
//
///**
// * Created by rahul on 8/10/18
// */
//@Module
//public class AssignedtoMeFragmentModule {
//
//    @Provides
//    AssignedToMeViewModel assignedtoMeViewModel(DataManager dataManager,
//                                                SchedulerProvider schedulerProvider) {
//        return new AssignedToMeViewModel(dataManager, schedulerProvider);
//    }
//
//    @Provides
//    AssignedtoMeAdapter provideAssignedtoMeAdapter(PreferencesHelper preferencesHelper) {
//        return new AssignedtoMeAdapter(new ArrayList<>(), preferencesHelper);
//    }
//
//    @Provides
//    TaskListingAdapter provideTaskListingAdapter(PreferencesHelper preferencesHelper) {
//        return new TaskListingAdapter(new ArrayList<>(), preferencesHelper);
//    }
//
//    @Provides
//    ViewModelProvider.Factory provideAssignedtoMeViewModel(AssignedToMeViewModel assignedToMeViewModel) {
//        return new ViewModelProviderFactory<>(assignedToMeViewModel);
//    }
//
//    @Provides
//    LinearLayoutManager provideLinearLayoutManager(AssignedtoMeFragment fragment) {
//        return new LinearLayoutManager(fragment.getActivity());
//    }
//
//}
