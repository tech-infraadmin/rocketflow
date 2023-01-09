package com.rf.taskmodule.ui.newdynamicform

import com.rf.taskmodule.data.network.APIError
import com.rf.taskmodule.data.network.ApiCallback
import com.rf.taskmodule.ui.base.BaseSdkNavigator

interface NewDynamicNavigator :BaseSdkNavigator {
    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?) {

    }
    fun  handleUploadFileResponse(callback: ApiCallback, result: Any?, error: APIError?)
    fun handleExecutiveMapResponse(position: Int, callback: ApiCallback, result: Any?, error: APIError?)

    fun  handleGetTaskDataResponse(callback: ApiCallback, result: Any?, error: APIError?)
}