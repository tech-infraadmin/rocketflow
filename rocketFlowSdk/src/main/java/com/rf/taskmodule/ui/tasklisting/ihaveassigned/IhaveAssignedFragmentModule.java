package com.rf.taskmodule.ui.tasklisting.ihaveassigned;//package com.rf.taskmodule.ui.tasklisting.ihaveassigned;
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
// * Created by rahul on 9/10/18
// */
//@Module
//public class IhaveAssignedFragmentModule {
//
//    @Provides
//    IhaveAssignedViewModel haveAssignedViewModel(DataManager dataManager,
//                                                 SchedulerProvider schedulerProvider) {
//        return new IhaveAssignedViewModel(dataManager, schedulerProvider);
//    }
//
//    @Provides
//    IhaveAssignedAdapter provideIhaveAssignedAdapter(PreferencesHelper preferencesHelper) {
//        return new IhaveAssignedAdapter(new ArrayList<>(), preferencesHelper);
//    }
//
//    @Provides
//    TaskListingAdapter provideTaskListingAdapter(PreferencesHelper preferencesHelper) {
//        return new TaskListingAdapter(new ArrayList<>(), preferencesHelper);
//    }
//    @Provides
//    ViewModelProvider.Factory provideIhaveAssignedViewModel(IhaveAssignedViewModel ihaveAssignedViewModel) {
//        return new ViewModelProviderFactory<>(ihaveAssignedViewModel);
//    }
//
//    @Provides
//    LinearLayoutManager provideLinearLayoutManager(IhaveAssignedFragment fragment) {
//        return new LinearLayoutManager(fragment.getActivity());
//    }
//}
