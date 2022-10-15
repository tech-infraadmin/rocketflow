package com.tracki.ui.scanqrcode

import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.ui.base.BaseNavigator

interface QrCodeNavigator : BaseNavigator {

    fun handleQrCodeResponse(callback: ApiCallback, result: Any?, error: APIError?)
    fun handleUserDetailsResponse(callback: ApiCallback, result: Any?, error: APIError?)
    fun handleProductDetailsResponse(callback: ApiCallback, result: Any?, error: APIError?,pid:String?)
}