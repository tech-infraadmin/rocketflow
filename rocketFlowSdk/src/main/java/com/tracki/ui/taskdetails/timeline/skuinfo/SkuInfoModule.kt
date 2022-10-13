//package com.tracki.ui.taskdetails.timeline.skuinfo
//
//import com.tracki.data.DataManager
//import com.tracki.ui.myDocument.DocumentsAdapter
//import com.tracki.ui.myDocument.MyDocumentViewModel
//import com.tracki.utils.rx.SchedulerProvider
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