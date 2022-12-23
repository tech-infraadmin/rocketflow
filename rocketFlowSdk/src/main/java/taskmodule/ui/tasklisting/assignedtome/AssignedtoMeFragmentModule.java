package taskmodule.ui.tasklisting.assignedtome;//package taskmodule.ui.tasklisting.assignedtome;
//
//import androidx.lifecycle.ViewModelProvider;
//import androidx.recyclerview.widget.LinearLayoutManager;
//
//import taskmodule.ViewModelProviderFactory;
//import taskmodule.data.DataManager;
//import taskmodule.data.local.prefs.PreferencesHelper;
//import taskmodule.ui.tasklisting.TaskListingAdapter;
//import taskmodule.utils.rx.SchedulerProvider;
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
