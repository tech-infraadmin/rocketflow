//package taskmodule.ui.category
//
//import taskmodule.data.DataManager
//import taskmodule.utils.rx.SchedulerProvider
//import dagger.Module
//import dagger.Provides
//
//
//@Module
//class AddProductCategoryViewModule {
//    @Provides
//    open fun provideAddCategoryViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider): AddProductCategoryViewModel {
//        return AddProductCategoryViewModel(dataManager, schedulerProvider)
//    }
//
//}