//package taskmodule.ui.products
//
//import taskmodule.data.DataManager
//import taskmodule.utils.rx.SchedulerProvider
//import dagger.Module
//import dagger.Provides
//
//
//@Module
//class AddProductViewModule {
//    @Provides
//    open fun provideAddProductViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider): AddProductViewModel {
//        return AddProductViewModel(dataManager, schedulerProvider)
//    }
//
//}