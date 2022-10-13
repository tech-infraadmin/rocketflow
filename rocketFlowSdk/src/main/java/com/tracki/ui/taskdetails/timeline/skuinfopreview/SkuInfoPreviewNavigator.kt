package com.tracki.ui.taskdetails.timeline.skuinfopreview

import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.ui.base.BaseNavigator

interface SkuInfoPreviewNavigator :BaseNavigator{
    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?)
    fun handleExecutiveMapResponse(position: Int, callback: ApiCallback, result: Any?, error: APIError?)
    fun  handleTaskInfoDataResponse(callback: ApiCallback, result: Any?, error: APIError?)
    fun  handleUploadFileResponse(callback: ApiCallback, result: Any?, error: APIError?)
    fun handleExecuteUpdateResponse(apiCallback: ApiCallback?, result: Any?, error: APIError?)

}