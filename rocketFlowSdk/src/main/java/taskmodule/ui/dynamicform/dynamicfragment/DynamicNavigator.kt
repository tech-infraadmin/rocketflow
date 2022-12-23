package taskmodule.ui.dynamicform.dynamicfragment

import taskmodule.data.network.APIError
import taskmodule.data.network.ApiCallback
import taskmodule.ui.base.BaseSdkNavigator

/**
 * Created by Rahul Abrol on 13/7/19.
 */
interface DynamicNavigator : BaseSdkNavigator {

    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?) {

    }

    fun handleExecutiveMapResponse(position: Int, callback: ApiCallback, result: Any?, error: APIError?)

   fun  handleGetTaskDataResponse(callback: ApiCallback, result: Any?, error: APIError?)

   fun  handleGetPreviousFormResponse(callback: ApiCallback, result: Any?, error: APIError?)

    fun  handleUploadFileResponse(callback: ApiCallback, result: Any?, error: APIError?)

}