package com.rf.taskmodule.ui.taskdetails.timeline.skuinfopreview

import com.rf.taskmodule.data.network.APIError
import com.rf.taskmodule.data.network.ApiCallback
import com.rf.taskmodule.ui.base.BaseSdkNavigator

interface SkuInfoPreviewNavigator :BaseSdkNavigator{
    override fun handleResponse(callback: com.rf.taskmodule.data.network.ApiCallback, result: Any?, error: APIError?)
    fun handleExecutiveMapResponse(position: Int, callback: com.rf.taskmodule.data.network.ApiCallback, result: Any?, error: APIError?)
    fun  handleTaskInfoDataResponse(callback: com.rf.taskmodule.data.network.ApiCallback, result: Any?, error: APIError?)
    fun  handleUploadFileResponse(callback: com.rf.taskmodule.data.network.ApiCallback, result: Any?, error: APIError?)
    fun handleExecuteUpdateResponse(apiCallback: com.rf.taskmodule.data.network.ApiCallback?, result: Any?, error: APIError?)

}