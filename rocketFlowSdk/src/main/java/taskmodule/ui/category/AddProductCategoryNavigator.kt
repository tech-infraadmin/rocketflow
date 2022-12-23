package taskmodule.ui.category

import taskmodule.data.network.APIError
import taskmodule.data.network.ApiCallback
import taskmodule.ui.base.BaseSdkNavigator

interface AddProductCategoryNavigator : BaseSdkNavigator {

    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?)
    fun handleProductCategoryResponse(callback: ApiCallback, result: Any?, error: APIError?)
    fun handleSendImageResponse(apiCallback: ApiCallback?, result: Any?, error: APIError?)
    fun handleUpdateProductCategoryResponse(callback: ApiCallback, result: Any?, error: APIError?)

}