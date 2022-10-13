//package com.tracki.ui.taskdetails.timeline.skuinfopreview
//
//import com.tracki.data.DataManager
//import com.tracki.ui.myDocument.DocumentsAdapter
//import com.tracki.ui.myDocument.MyDocumentViewModel
//import com.tracki.utils.rx.SchedulerProvider
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