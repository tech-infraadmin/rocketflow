package com.rf.taskmodule.ui.selectorder

import com.rf.taskmodule.data.network.APIError
import com.rf.taskmodule.data.network.ApiCallback
import com.rf.taskmodule.ui.base.BaseSdkNavigator

interface SelectOrderNavigator :BaseSdkNavigator{

    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?)
    fun handleProductCategoryResponse(callback: ApiCallback, result: Any?, error: APIError?)
    fun handleProductResponse(callback: ApiCallback, result: Any?, error: APIError?)
    fun handleCreateOrderResponse(callback: ApiCallback, result: Any?, error: APIError?)
    fun linkInventoryResponse(callback: ApiCallback, result: Any?, error: APIError?)
    fun handleHubListResponse(callback: ApiCallback, result: Any?, error: APIError?)
}