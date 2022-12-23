//package taskmodule.ui.scanqrcode
//
//
//import taskmodule.data.DataManager
//
//import taskmodule.utils.rx.SchedulerProvider
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