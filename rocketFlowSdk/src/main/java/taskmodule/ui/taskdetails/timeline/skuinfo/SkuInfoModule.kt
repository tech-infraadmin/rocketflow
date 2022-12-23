//package taskmodule.ui.taskdetails.timeline.skuinfo
//
//import taskmodule.data.DataManager
//import taskmodule.ui.myDocument.DocumentsAdapter
//import taskmodule.ui.myDocument.MyDocumentViewModel
//import taskmodule.utils.rx.SchedulerProvider
//import dagger.Module
//import dagger.Provides
//
//@Module
//class SkuInfoModule {
//
//    @Provides
//    open fun provideUploadDocumentModel(dataManager: DataManager, schedulerProvider: SchedulerProvider):
//            SkuInfoViewModel {
//        return SkuInfoViewModel(dataManager, schedulerProvider)
//    }
//
//}