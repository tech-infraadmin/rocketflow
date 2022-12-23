package taskmodule.ui.taskdetails.timeline.skuinfopreview

import taskmodule.data.network.APIError
import taskmodule.data.network.ApiCallback
import taskmodule.ui.base.BaseSdkNavigator

interface SkuInfoPreviewNavigator :BaseSdkNavigator{
    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?)
    fun handleExecutiveMapResponse(position: Int, callback: ApiCallback, result: Any?, error: APIError?)
    fun  handleTaskInfoDataResponse(callback: ApiCallback, result: Any?, error: APIError?)
    fun  handleUploadFileResponse(callback: ApiCallback, result: Any?, error: APIError?)
    fun handleExecuteUpdateResponse(apiCallback: ApiCallback?, result: Any?, error: APIError?)

}