package com.rf.taskmodule.ui.category

import com.rf.taskmodule.data.network.APIError
import com.rf.taskmodule.data.network.ApiCallback
import com.rf.taskmodule.ui.base.BaseSdkNavigator

interface ProductCategoryNavigator : BaseSdkNavigator {

    override fun handleResponse(callback: com.rf.taskmodule.data.network.ApiCallback, result: Any?, error: APIError?)

    fun handleProductCategoryResponse(callback: com.rf.taskmodule.data.network.ApiCallback, result: Any?, error: APIError?)
    fun handleUpdateProductStatusCategoryResponse(callback: com.rf.taskmodule.data.network.ApiCallback, result: Any?, error: APIError?)
    fun handleDeleteProductCategoryResponse(callback: com.rf.taskmodule.data.network.ApiCallback, result: Any?, error: APIError?)
}