//package taskmodule.ui.taskdetails.timeline.skuinfopreview
//
//import taskmodule.data.DataManager
//import taskmodule.ui.myDocument.DocumentsAdapter
//import taskmodule.ui.myDocument.MyDocumentViewModel
//import taskmodule.utils.rx.SchedulerProvider
//import dagger.Module
//import dagger.Provides
//
//@Module
//class SkuInfoPreviewModule {
//
//    @Provides
//    open fun provideUploadDocumentModel(dataManager: DataManager, schedulerProvider: SchedulerProvider):
//            SkuInfoPreviewViewModel {
//        return SkuInfoPreviewViewModel(dataManager, schedulerProvider)
//    }
//
//}