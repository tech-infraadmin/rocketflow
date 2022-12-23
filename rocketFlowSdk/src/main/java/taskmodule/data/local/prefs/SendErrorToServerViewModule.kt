//package taskmodule.data.local.prefs
//
//import androidx.work.ListenableWorker
//import taskmodule.data.DataManager
//import taskmodule.utils.rx.SchedulerProvider
//import dagger.MapKey
//import dagger.Module
//import dagger.Provides
//import kotlin.reflect.KClass
//
//@MapKey
//@Target(AnnotationTarget.FUNCTION)
//@Retention(AnnotationRetention.RUNTIME)
//annotation class WorkerKey(val value: KClass<out ListenableWorker>)
//@Module
//class SendErrorToServerViewModule {
//    @Provides
//    fun providePostFileErrorToServerViewModel(
//        dataManager: DataManager,
//        schedulerProvider: SchedulerProvider
//    ): PostFileErrorToServerViewModel {
//        return PostFileErrorToServerViewModel(dataManager, schedulerProvider)
//    }
//
//}