package taskmodule.ui.scanqrcode

import taskmodule.data.network.APIError
import taskmodule.data.network.ApiCallback
import taskmodule.ui.base.BaseSdkNavigator

interface ProductScanNavigator : BaseSdkNavigator {

    fun handleQrCodeResponse(callback: ApiCallback, result: Any?, error: APIError?)
    fun handleProductDetailsResponse(callback: ApiCallback, result: Any?, error: APIError?,pid:String?)
}