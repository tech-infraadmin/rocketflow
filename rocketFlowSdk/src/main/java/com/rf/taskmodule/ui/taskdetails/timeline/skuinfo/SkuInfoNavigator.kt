package com.rf.taskmodule.ui.taskdetails.timeline.skuinfo

import com.rf.taskmodule.data.network.APIError
import com.rf.taskmodule.data.network.ApiCallback
import com.rf.taskmodule.ui.base.BaseSdkNavigator

interface SkuInfoNavigator :BaseSdkNavigator{
    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?)
    fun handleExecutiveMapResponse(position: Int, callback: ApiCallback, result: Any?, error: APIError?)
    fun  handleTaskInfoDataResponse(callback: ApiCallback, result: Any?, error: APIError?)
    fun  handleUploadFileResponse(callback: ApiCallback, result: Any?, error: APIError?)
    fun handleExecuteUpdateResponse(apiCallback: ApiCallback?, result: Any?, error: APIError?)

}