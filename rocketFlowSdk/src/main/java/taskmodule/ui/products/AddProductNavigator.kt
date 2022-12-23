package taskmodule.ui.products

import taskmodule.data.network.APIError
import taskmodule.data.network.ApiCallback
import taskmodule.ui.base.BaseSdkNavigator

interface AddProductNavigator : BaseSdkNavigator {

    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?)
    fun handleUpdateResponse(callback: ApiCallback, result: Any?, error: APIError?)
    fun handleSendImageResponse(callback: ApiCallback, result: Any?, error: APIError?)
    fun handleUnitsResponse(callback: ApiCallback, result: Any?, error: APIError?)
    fun handleProductDetailsResponse(callback: ApiCallback, result: Any?, error: APIError?)
}