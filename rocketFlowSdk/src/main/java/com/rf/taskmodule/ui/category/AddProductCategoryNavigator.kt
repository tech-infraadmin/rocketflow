package com.rf.taskmodule.ui.category

import com.rf.taskmodule.data.network.APIError
import com.rf.taskmodule.data.network.ApiCallback
import com.rf.taskmodule.ui.base.BaseSdkNavigator

interface AddProductCategoryNavigator : BaseSdkNavigator {

    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?)
    fun handleProductCategoryResponse(callback: ApiCallback, result: Any?, error: APIError?)
    fun handleSendImageResponse(apiCallback: ApiCallback?, result: Any?, error: APIError?)
    fun handleUpdateProductCategoryResponse(callback: ApiCallback, result: Any?, error: APIError?)

}