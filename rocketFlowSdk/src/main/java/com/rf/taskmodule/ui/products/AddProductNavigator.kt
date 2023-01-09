package com.rf.taskmodule.ui.products

import com.rf.taskmodule.data.network.APIError
import com.rf.taskmodule.data.network.ApiCallback
import com.rf.taskmodule.ui.base.BaseSdkNavigator

interface AddProductNavigator : BaseSdkNavigator {

    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?)
    fun handleUpdateResponse(callback: ApiCallback, result: Any?, error: APIError?)
    fun handleSendImageResponse(callback: ApiCallback, result: Any?, error: APIError?)
    fun handleUnitsResponse(callback: ApiCallback, result: Any?, error: APIError?)
    fun handleProductDetailsResponse(callback: ApiCallback, result: Any?, error: APIError?)
}