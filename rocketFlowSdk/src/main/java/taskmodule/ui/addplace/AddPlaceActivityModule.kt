//package taskmodule.ui.addplace
//
//import taskmodule.data.DataManager
//import taskmodule.utils.rx.SchedulerProvider
//import dagger.Module
//import dagger.Provides
//
//
//@Module
//open class AddPlaceActivityModule {
//
//    @Provides
//    open fun provideAddPlaceViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider): AddPlaceViewModel {
//        return AddPlaceViewModel(dataManager, schedulerProvider)
//    }
//}