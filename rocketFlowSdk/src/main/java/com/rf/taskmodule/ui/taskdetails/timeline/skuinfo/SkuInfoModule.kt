//package com.rf.taskmodule.ui.taskdetails.timeline.skuinfo
//
//import com.rf.taskmodule.data.DataManager
//import com.rf.taskmodule.ui.myDocument.DocumentsAdapter
//import com.rf.taskmodule.ui.myDocument.MyDocumentViewModel
//import com.rf.taskmodule.utils.rx.SchedulerProvider
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