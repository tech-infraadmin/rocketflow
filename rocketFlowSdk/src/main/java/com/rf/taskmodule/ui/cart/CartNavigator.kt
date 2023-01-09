package com.rf.taskmodule.ui.cart

import com.rf.taskmodule.data.network.APIError
import com.rf.taskmodule.data.network.ApiCallback
import com.rf.taskmodule.ui.base.BaseSdkNavigator

interface CartNavigator : BaseSdkNavigator {

    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?)
    fun handleCartResponse(callback: ApiCallback, result: Any?, error: APIError?)
    fun handleCreateOrderResponse(callback: ApiCallback, result: Any?, error: APIError?)
    fun handleApplyCouponResponse(callback: ApiCallback, result: Any?, error: APIError?)
    fun linkInventoryResponse(callback: ApiCallback, result: Any?, error: APIError?)
}