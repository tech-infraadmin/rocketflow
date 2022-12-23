package taskmodule.ui.selectorder

import taskmodule.data.network.APIError
import taskmodule.data.network.ApiCallback
import taskmodule.ui.base.BaseSdkNavigator

interface SelectOrderNavigator :BaseSdkNavigator{

    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?)
    fun handleProductCategoryResponse(callback: ApiCallback, result: Any?, error: APIError?)
    fun handleProductResponse(callback: ApiCallback, result: Any?, error: APIError?)
    fun handleCreateOrderResponse(callback: ApiCallback, result: Any?, error: APIError?)
    fun linkInventoryResponse(callback: ApiCallback, result: Any?, error: APIError?)
    fun handleHubListResponse(callback: ApiCallback, result: Any?, error: APIError?)
}