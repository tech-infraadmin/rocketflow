//package taskmodule.ui.newcreatetask
//
//import taskmodule.data.DataManager
//import taskmodule.utils.rx.SchedulerProvider
//import dagger.Module
//import dagger.Provides
//
//@Module
//class NewCreateTaskActivityModule {
//    @Provides
//    open fun provideNewCreateTaskViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider): NewCreateTaskViewModel {
//        return NewCreateTaskViewModel(dataManager, schedulerProvider)
//    }
//    @Provides
//    open fun provideGetUserSuggestionListViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider): GetUserSuggestionListViewModel {
//        return GetUserSuggestionListViewModel(dataManager, schedulerProvider)
//    }
//
//}