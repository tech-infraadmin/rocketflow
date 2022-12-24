//package com.rf.taskmodule.ui.taskdetails.timeline.skuinfopreview
//
//import com.rf.taskmodule.data.DataManager
//import com.rf.taskmodule.ui.myDocument.DocumentsAdapter
//import com.rf.taskmodule.ui.myDocument.MyDocumentViewModel
//import com.rf.taskmodule.utils.rx.SchedulerProvider
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