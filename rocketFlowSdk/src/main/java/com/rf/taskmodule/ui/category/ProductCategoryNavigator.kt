package com.rf.taskmodule.ui.category

import com.rf.taskmodule.data.network.APIError
import com.rf.taskmodule.data.network.ApiCallback
import com.rf.taskmodule.ui.base.BaseSdkNavigator

interface ProductCategoryNavigator : BaseSdkNavigator {

    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?)

    fun handleProductCategoryResponse(callback: ApiCallback, result: Any?, error: APIError?)
    fun handleUpdateProductStatusCategoryResponse(callback: ApiCallback, result: Any?, error: APIError?)
    fun handleDeleteProductCategoryResponse(callback: ApiCallback, result: Any?, error: APIError?)
}