package com.rf.taskmodule.ui.scanqrcode

import com.rf.taskmodule.data.network.APIError
import com.rf.taskmodule.data.network.ApiCallback
import com.rf.taskmodule.ui.base.BaseSdkNavigator

interface QrCodeNavigator : BaseSdkNavigator {

    fun handleQrCodeResponse(callback: com.rf.taskmodule.data.network.ApiCallback, result: Any?, error: APIError?)
    fun handleUserDetailsResponse(callback: com.rf.taskmodule.data.network.ApiCallback, result: Any?, error: APIError?)
    fun handleProductDetailsResponse(callback: com.rf.taskmodule.data.network.ApiCallback, result: Any?, error: APIError?, pid:String?)
}