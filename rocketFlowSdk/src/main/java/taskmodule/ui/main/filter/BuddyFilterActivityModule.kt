//package taskmodule.ui.main.filter
//
//import taskmodule.data.DataManager
//import taskmodule.utils.rx.SchedulerProvider
//import dagger.Module
//import dagger.Provides
//
///**
// * Created by rahul on 12/10/18
// */
//@Module
//open class BuddyFilterActivityModule {
//
//    @Provides
//    open fun provideBuddyFilterViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider): BuddyFilterViewModel {
//        return BuddyFilterViewModel(dataManager, schedulerProvider)
//    }
//
//}