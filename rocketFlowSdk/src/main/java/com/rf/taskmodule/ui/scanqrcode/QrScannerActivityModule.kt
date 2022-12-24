//package com.rf.taskmodule.ui.scanqrcode
//
//
//import com.rf.taskmodule.data.DataManager
//
//import com.rf.taskmodule.utils.rx.SchedulerProvider
//import dagger.Module
//import dagger.Provides
//
//
//
//@Module
//class QrScannerActivityModule {
//    @Provides
//    fun     getQrCodeViewModel(dataManager: DataManager?,
//                               schedulerProvider: SchedulerProvider?): QrCodeValueViewModel {
//        return QrCodeValueViewModel(dataManager!!, schedulerProvider!!)
//    }
//
//
//
//}