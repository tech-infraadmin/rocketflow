package com.rf.taskmodule.ui.selectorder

import com.rf.taskmodule.data.network.APIError
import com.rf.taskmodule.data.network.ApiCallback
import com.rf.taskmodule.ui.base.BaseSdkNavigator

interface SelectOrderNavigator :BaseSdkNavigator{

    override fun handleResponse(callback: com.rf.taskmodule.data.network.ApiCallback, result: Any?, error: APIError?)
    fun handleProductCategoryResponse(callback: com.rf.taskmodule.data.network.ApiCallback, result: Any?, error: APIError?)
    fun handleProductResponse(callback: com.rf.taskmodule.data.network.ApiCallback, result: Any?, error: APIError?)
    fun handleCreateOrderResponse(callback: com.rf.taskmodule.data.network.ApiCallback, result: Any?, error: APIError?)
    fun linkInventoryResponse(callback: com.rf.taskmodule.data.network.ApiCallback, result: Any?, error: APIError?)
    fun handleHubListResponse(callback: com.rf.taskmodule.data.network.ApiCallback, result: Any?, error: APIError?)
}