package taskmodule.ui.category

import taskmodule.data.network.APIError
import taskmodule.data.network.ApiCallback
import taskmodule.ui.base.BaseSdkNavigator

interface ProductCategoryNavigator : BaseSdkNavigator {

    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?)

    fun handleProductCategoryResponse(callback: ApiCallback, result: Any?, error: APIError?)
    fun handleUpdateProductStatusCategoryResponse(callback: ApiCallback, result: Any?, error: APIError?)
    fun handleDeleteProductCategoryResponse(callback: ApiCallback, result: Any?, error: APIError?)
}