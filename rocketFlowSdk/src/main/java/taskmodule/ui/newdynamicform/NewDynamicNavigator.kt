package taskmodule.ui.newdynamicform

import taskmodule.data.network.APIError
import taskmodule.data.network.ApiCallback
import taskmodule.ui.base.BaseSdkNavigator

interface NewDynamicNavigator :BaseSdkNavigator {
    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?) {

    }
    fun  handleUploadFileResponse(callback: ApiCallback, result: Any?, error: APIError?)
    fun handleExecutiveMapResponse(position: Int, callback: ApiCallback, result: Any?, error: APIError?)

    fun  handleGetTaskDataResponse(callback: ApiCallback, result: Any?, error: APIError?)
}