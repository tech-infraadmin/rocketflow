package com.rf.taskmodule.ui.scanqrcode

import com.rf.taskmodule.data.network.APIError
import com.rf.taskmodule.data.network.ApiCallback
import com.rf.taskmodule.ui.base.BaseSdkNavigator

interface ProductScanNavigator : BaseSdkNavigator {

    fun handleQrCodeResponse(callback: ApiCallback, result: Any?, error: APIError?)
    fun handleProductDetailsResponse(callback: ApiCallback, result: Any?, error: APIError?, pid:String?)
}