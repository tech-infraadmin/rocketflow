//package taskmodule.ui.selectorder
//
//import taskmodule.data.DataManager
//import taskmodule.utils.rx.SchedulerProvider
//import dagger.Module
//import dagger.Provides
//
//
//@Module
//class SelectOrderViewModule {
//    @Provides
//    open fun provideSelectOrderViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider): SelectOrderViewModel {
//        return SelectOrderViewModel(dataManager, schedulerProvider)
//    }
//
//}