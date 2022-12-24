package com.rf.taskmodule.ui.newdynamicform

import com.rf.taskmodule.data.network.APIError
import com.rf.taskmodule.data.network.ApiCallback
import com.rf.taskmodule.ui.base.BaseSdkNavigator

interface NewDynamicNavigator :BaseSdkNavigator {
    override fun handleResponse(callback: com.rf.taskmodule.data.network.ApiCallback, result: Any?, error: APIError?) {

    }
    fun  handleUploadFileResponse(callback: com.rf.taskmodule.data.network.ApiCallback, result: Any?, error: APIError?)
    fun handleExecutiveMapResponse(position: Int, callback: com.rf.taskmodule.data.network.ApiCallback, result: Any?, error: APIError?)

    fun  handleGetTaskDataResponse(callback: com.rf.taskmodule.data.network.ApiCallback, result: Any?, error: APIError?)
}