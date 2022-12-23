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
//class ProductScanActivityModule {
//    @Provides
//    fun     getQrCodeViewModel(dataManager: DataManager?,
//                               schedulerProvider: SchedulerProvider?): ProductScanViewModel {
//        return ProductScanViewModel(dataManager!!, schedulerProvider!!)
//    }
//
//
//
//}